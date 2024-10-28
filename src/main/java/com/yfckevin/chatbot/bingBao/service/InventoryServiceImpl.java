package com.yfckevin.chatbot.bingBao.service;

import com.yfckevin.chatbot.bingBao.dto.InventoryDTO;
import com.yfckevin.chatbot.bingBao.entity.Inventory;
import com.yfckevin.chatbot.bingBao.entity.Product;
import com.yfckevin.chatbot.message.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.yfckevin.chatbot.GlobalConstants.BING_BAO_INVENTORY_METADATA_TYPE;

@Slf4j
@Service
public class InventoryServiceImpl implements InventoryService {
    private final MongoTemplate bingBaoMongoTemplate;
    private final EmbeddingModel embeddingModel;
    private final MessageRepository messageRepository;

    public InventoryServiceImpl(MongoTemplate bingBaoMongoTemplate, EmbeddingModel embeddingModel,
                                MessageRepository messageRepository) {
        this.bingBaoMongoTemplate = bingBaoMongoTemplate;
        this.embeddingModel = embeddingModel;
        this.messageRepository = messageRepository;
    }

    @Override
    public List<Document> dailyImportInventories() {

        Query query = new Query();
        query.addCriteria(Criteria.where("deletionDate").ne(null));

        final List<Inventory> filterInventoryList = bingBaoMongoTemplate.find(query, Inventory.class).stream().limit(20).toList();
        log.info("總共取得要匯入的庫存食材筆數：{}", filterInventoryList.size());

        final Map<String, List<Product>> productMap = getProductList(filterInventoryList);

        Map<String, Long> itemCountMap = filterInventoryList.stream()
                .peek(inventory -> {
                    final Product product = productMap.get(inventory.getProductId()).stream().findFirst().get();
                    inventory.setName(product.getName());
                })
                .collect(Collectors.groupingBy(Inventory::getReceiveItemId, Collectors.counting()));

        Map<String, Map<Long, List<Inventory>>> result = filterInventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getReceiveItemId,
                        Collectors.groupingBy(
                                inventory -> itemCountMap.get(inventory.getReceiveItemId()),
                                Collectors.toList()
                        )
                ));

        //排除庫存的食材重複的問題，加入到DTO以前先檢查uniqueReceiveItemIds是否有ReceiveItemId
        Set<String> uniqueReceiveItemIds = new HashSet<>();

        final List<InventoryDTO> inventoryDTOList = result.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream()
                        .flatMap(innerEntry -> innerEntry.getValue().stream()
                                .map(inventory -> {
                                    boolean isDelete = inventory.getDeletionDate() != null;
                                    boolean isUsed = inventory.getUsedDate() != null;
                                    LocalDate expiryDate = LocalDate.parse(inventory.getExpiryDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                    boolean isExpired = expiryDate.isBefore(LocalDate.now());
                                    InventoryDTO dto = new InventoryDTO();
                                    dto.setId(inventory.getId());
                                    dto.setContent(String.format("食材名稱：%s，是否被刪除：%s，是否用完：%s，放進冰箱日期：%s，是否過期：%s，有效日期：%s，剩餘數量：%s，存放位置：%s",
                                            inventory.getName(),
                                            isDelete,
                                            isUsed,
                                            inventory.getStoreDate(),
                                            isExpired,
                                            inventory.getExpiryDate(),
                                            innerEntry.getKey(),
                                            inventory.getStorePlace().getLabel()
                                    ));

                                    if (uniqueReceiveItemIds.add(inventory.getReceiveItemId())) {
                                        return dto;
                                    } else {
                                        return null;
                                    }
                                })
                        )
                )
                .filter(Objects::nonNull)
                .toList();

        cleanInventories();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", BING_BAO_INVENTORY_METADATA_TYPE);
        metadata.put("creation_date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        List<Document> inventoryDocs = inventoryDTOList.stream().map(
                inventory -> {
                    metadata.put("inventory_id", inventory.getId());
                    final Document document = new Document(inventory.getContent(), metadata);
                    final float[] embedded = embeddingModel.embed(document);
                    document.setEmbedding(embedded);
                    return document;
                }).toList();

        return inventoryDocs;
    }

    //取得庫存中的食材資料，用來取得食材姓名用
    private Map<String, List<Product>> getProductList(List<Inventory> filterInventoryList) {
        final Set<String> productIdSet = filterInventoryList.stream().map(Inventory::getProductId).collect(Collectors.toSet());
        Query productQuery = new Query();
        if (!productIdSet.isEmpty()) {
            productQuery.addCriteria(Criteria.where("_id").in(productIdSet));
        }
        return bingBaoMongoTemplate.find(productQuery, Product.class)
                .stream()
                .collect(Collectors.groupingBy(Product::getId));
    }

    private void cleanInventories() {
        final int deleted = messageRepository.deleteAllBingBaoInventories(BING_BAO_INVENTORY_METADATA_TYPE);
        log.info("刪除筆數：{}", deleted);
    }
}
