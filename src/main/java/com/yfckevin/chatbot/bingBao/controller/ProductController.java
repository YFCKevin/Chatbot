package com.yfckevin.chatbot.bingBao.controller;

import com.yfckevin.chatbot.Advisors.TokenUsageLogAdvisor;
import com.yfckevin.chatbot.bingBao.entity.Inventory;
import com.yfckevin.chatbot.bingBao.entity.Product;
import com.yfckevin.chatbot.bingBao.service.ProductService;
import com.yfckevin.chatbot.exception.ResultStatus;
import com.yfckevin.chatbot.message.dto.ChatMessageDTO;
import com.yfckevin.chatbot.utils.ChatUtil;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.yfckevin.chatbot.GlobalConstants.*;

@RestController
@RequestMapping("/ai/bingBao/product")
public class ProductController {
    private final ProductService productService;
    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private ChatMemory chatMemory = new InMemoryChatMemory();
    private final RedisTemplate<String, String> redisTemplate;
    private HashOperations<String, String, String> hashOperations;
    private final ChatUtil chatUtil;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public ProductController(ProductService productService, VectorStore vectorStore, ChatClient.Builder chatClientBuilder, RedisTemplate<String, String> redisTemplate, ChatUtil chatUtil) {
        this.productService = productService;
        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
        this.redisTemplate = redisTemplate;
        this.chatUtil = chatUtil;
    }

    @GetMapping("/readProduct")
    public List<Document> readProduct() {
        final List<Document> documents = productService.dailyImportProducts(fetchRefreshData());
        cleanRedisIndex();
        return documents;
    }

    @GetMapping("/importProduct")
//    @Scheduled(cron = "0 0 */4 * * ?")
    public ResponseEntity<?> importProduct() {
        final List<Document> documents = productService.dailyImportProducts(fetchRefreshData());
        vectorStore.write(documents);
        cleanRedisIndex();
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/chatSearch")
    public ResponseEntity<?> chatSearch(@RequestBody ChatMessageDTO dto) {
        final String chatChannel = dto.getChatChannel();
        final String memberId = dto.getMemberId();
        final String query = dto.getQuery();

        ResultStatus resultStatus = new ResultStatus();

        Filter.Expression filterProjectType = new Filter.Expression(
                Filter.ExpressionType.EQ,
                new Filter.Key("type"),
                new Filter.Value(BING_BAO_PRODUCT_METADATA_TYPE)
        );

        SearchRequest request = SearchRequest.query(query)
                .withTopK(10)  // 可選，設置回傳的資料數量
                .withFilterExpression(filterProjectType);

        List<Document> results = vectorStore.similaritySearch(request);
        System.out.println("近似搜索查詢到的所有資料：" + results);

        results = results.stream()
                .filter(doc -> {
                    Float distance = (Float) doc.getMetadata().get("distance");
                    return distance != null && distance < 0.3;
                })
                .toList();
        System.out.println("distance優化過的資料：" + results);

        StringBuilder productInfos = new StringBuilder();
        for (Document document : results) {
            final String productId = (String) document.getMetadata().get("product_id");
            productService.findByProductId(productId)
                    .ifPresent(product -> {
                        String productInfo = String.format("名稱：%s，分類：%s%s，庫存資訊：%s",
                                product.getName(),
                                product.getMainCategory(),
                                (product.getSubCategory() != null ? "(" + product.getSubCategory() + ")" : ""),
                                constructInventoryInfo(product.getInventoryList())
                        );
                        productInfos.append(productInfo).append("\n");
                    });
        }

        // 組裝chatId
        String chatId = BING_BAO_PROJECT_NAME + "_" + memberId + "_" + chatChannel;

        final String content = chatClient.prompt()
                .advisors(new MessageChatMemoryAdvisor(chatMemory, chatId, 20), new TokenUsageLogAdvisor())
                .advisors(context -> {
                    context.param("chatId", chatId);
                    context.param("lastN", 20);
                })
                .user(query)
                .system(productInfos.toString().trim())
                .call()
                .content();

        resultStatus.setCode("C000");
        resultStatus.setMessage("成功");
        resultStatus.setData(content);
        return ResponseEntity.ok(resultStatus);
    }

    private String constructInventoryInfo(List<Inventory> inventoryList) {
        //分成效期內、效期內已用完、過期、過期已用完、刪除
        Map<String, Integer> valid = new HashMap<>();
        Map<String, Integer> validAndUsed = new HashMap<>();
        Map<String, Integer> expired = new HashMap<>();
        Map<String, Integer> expiredAndUsed = new HashMap<>();
        Map<String, Integer> deleted = new HashMap<>();

        //取得數量和存放位置
        inventoryList.forEach(inventory -> {
            String storePlace = inventory.getStorePlace();
            String condition = getCondition(inventory);
            switch (condition) {
                case "deleted" -> deleted.merge(storePlace, 1, Integer::sum);
                case "expiredAndUsed" -> expiredAndUsed.merge(storePlace, 1, Integer::sum);
                case "expired" -> expired.merge(storePlace, 1, Integer::sum);
                case "validAndUsed" -> validAndUsed.merge(storePlace, 1, Integer::sum);
                case "valid" -> valid.merge(storePlace, 1, Integer::sum);
                default -> {
                }
            }
        });

        String validDetails = valid.entrySet().stream()
                .map(entry -> String.format("在%s有%s個", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("; "));
        String validAndUsedDetails = validAndUsed.entrySet().stream()
                .map(entry -> String.format("在%s有%s個", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("; "));
        String expiredDetails = expired.entrySet().stream()
                .map(entry -> String.format("在%s有%s個", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("; "));
        String expiredAndUsedDetails = expiredAndUsed.entrySet().stream()
                .map(entry -> String.format("在%s有%s個", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("; "));
        String deletedDetails = deleted.entrySet().stream()
                .map(entry -> String.format("在%s有%s個", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("; "));

        return String.format("以下分別是該食材庫存的存放位置以及數量：在有效期限內的資料是%s，在有效期限內且用完的資料是%s，過期的資料是%s，過期且用完的資料是%s，刪除的資料是%s",
                validDetails,
                validAndUsedDetails,
                expiredDetails,
                expiredAndUsedDetails,
                deletedDetails
        );
    }

    private String getCondition(Inventory inventory) {
        if (StringUtils.isNotBlank(inventory.getDeletionDate())) {
            return "deleted";
        } else if (StringUtils.isNotBlank(inventory.getUsedDate()) &&
                "已過期".equals(inventory.getValidStr())) {
            return "expiredAndUsed";
        } else if (StringUtils.isBlank(inventory.getUsedDate()) &&
                "已過期".equals(inventory.getValidStr())) {
            return "expired";
        } else if (StringUtils.isNotBlank(inventory.getUsedDate()) &&
                "在有效期限內".equals(inventory.getValidStr())) {
            return "validAndUsed";
        } else if (StringUtils.isBlank(inventory.getUsedDate()) &&
                "在有效期限內".equals(inventory.getValidStr())) {
            return "valid";
        }
        return "none";
    }

    @GetMapping("/productSearch")
    public List<Document> productSearch(String query) {
        Filter.Expression filterProjectType = new Filter.Expression(
                Filter.ExpressionType.EQ,
                new Filter.Key("type"),
                new Filter.Value(BING_BAO_PRODUCT_METADATA_TYPE)
        );

        SearchRequest request = SearchRequest.query(query)
                .withTopK(50)  // 可選，設置回傳的資料數量
                .withFilterExpression(filterProjectType);

        List<Document> results = vectorStore.similaritySearch(request);

        results = results.stream()
                .filter(doc -> {
                    Float distance = (Float) doc.getMetadata().get("distance");
                    return distance != null && distance < 0.3;
                })
                .toList();

        return results;
    }


    private List<Map<String, String>> fetchRefreshData() {
        List<Map<String, String>> data = new ArrayList<>();
        Optional.ofNullable(redisTemplate.opsForSet().members(BING_BAO_PRODUCT_KEY_PREFIX))   //get productId index
                .ifPresent(productIds -> productIds.forEach(productId -> {
                    final Map<String, String> entries = hashOperations.entries(productId);
                    data.add(entries);
                }));
        return data;
    }

    private void cleanRedisIndex() {
        List<String> keysToDelete = new ArrayList<>();
        keysToDelete.add(BING_BAO_PRODUCT_KEY_PREFIX);
        Optional.ofNullable(redisTemplate.opsForSet().members(BING_BAO_PRODUCT_KEY_PREFIX))
                .ifPresent(keysToDelete::addAll);
        redisTemplate.delete(keysToDelete);    //delete index (Set) and delete all product data (Hash)
    }
}
