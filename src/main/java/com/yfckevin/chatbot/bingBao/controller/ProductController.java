package com.yfckevin.chatbot.bingBao.controller;

import com.yfckevin.chatbot.bingBao.entity.Product;
import com.yfckevin.chatbot.bingBao.service.ProductService;
import com.yfckevin.chatbot.utils.ChatUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.yfckevin.chatbot.GlobalConstants.*;

@RestController
@RequestMapping("/ai/bingBao/product")
public class ProductController {
    private final ProductService productService;
    private final VectorStore vectorStore;
    private final RedisTemplate<String, String> redisTemplate;
    private HashOperations<String, String, String> hashOperations;
    private final ChatUtil chatUtil;
    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public ProductController(ProductService productService, VectorStore vectorStore, RedisTemplate<String, String> redisTemplate, ChatUtil chatUtil) {
        this.productService = productService;
        this.vectorStore = vectorStore;
        this.redisTemplate = redisTemplate;
        this.chatUtil = chatUtil;
    }

    @GetMapping("/readProduct")
    public List<Document> readProduct (){
        final List<Document> documents = productService.dailyImportProducts(fetchRefreshData());
        cleanRedisIndex();
        return documents;
    }

    @GetMapping("/importProduct")
//    @Scheduled(cron = "0 0 */4 * * ?")
    public ResponseEntity<?> importProduct (){
        final List<Document> documents = productService.dailyImportProducts(fetchRefreshData());
        vectorStore.write(documents);
        cleanRedisIndex();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/chatSearch")
    public Product chatSearch (String query){
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

            final String productId = (String) results.get(0).getMetadata().get("product_id");
            return productService.findByProductId(productId).get();

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
