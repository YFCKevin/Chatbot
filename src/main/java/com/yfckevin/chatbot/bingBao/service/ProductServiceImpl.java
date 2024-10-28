package com.yfckevin.chatbot.bingBao.service;

import com.yfckevin.chatbot.bingBao.dto.ProductDTO;
import com.yfckevin.chatbot.bingBao.entity.Product;
import com.yfckevin.chatbot.entity.Mapping;
import com.yfckevin.chatbot.repository.MappingRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yfckevin.chatbot.GlobalConstants.*;

@Service
public class ProductServiceImpl implements ProductService {
    private final MongoTemplate bingBaoMongoTemplate;
    private final EmbeddingModel embeddingModel;
    private final MappingRepository mappingRepository;

    public ProductServiceImpl(@Qualifier("bingBaoMongoTemplate") MongoTemplate bingBaoMongoTemplate, EmbeddingModel embeddingModel, MappingRepository mappingRepository) {
        this.bingBaoMongoTemplate = bingBaoMongoTemplate;
        this.embeddingModel = embeddingModel;
        this.mappingRepository = mappingRepository;
    }

    @Transactional
    @Override
    public List<Document> dailyImportProducts() {

        final List<String> mappedIdList = mappingRepository.findByDbUri(BING_BAO_MONGO_URI).stream()
                .map(Mapping::getMappedId).toList();

//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime threeDaysAgo = now.minusDays(2);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String nowStr = now.format(formatter);
//        String threeDaysAgoStr = threeDaysAgo.format(formatter);
        Query query = new Query();
//        query.addCriteria(Criteria.where("creationDate").gte(threeDaysAgoStr).lte(nowStr));

        final List<Product> filterProductList = bingBaoMongoTemplate.find(query, Product.class)
                .stream()
                .filter(product -> !mappedIdList.contains(product.getId()))
                .limit(20)
                .toList();

        final List<ProductDTO> productDTOList = filterProductList.stream().map(product -> {
            ProductDTO dto = new ProductDTO();
            dto.setId(product.getId());
            String subCategoryLabel = (product.getSubCategory() != null) ? product.getSubCategory().getLabel() : "";
            dto.setContent(String.format("食材名稱：%s，食材描述：%s，分類：%s / %s",
                    product.getName(),
                    product.getDescription(),
                    product.getMainCategory().getLabel(),
                    subCategoryLabel));
            return dto;
        }).toList();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", BING_BAO_PRODUCT_METADATA_TYPE);
        metadata.put("creation_date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        List<Document> productDocs = productDTOList.stream().map(
                product -> {
                    metadata.put("product_id", product.getId());
                    final Document document = new Document(product.getContent(), metadata);
                    final float[] embedded = embeddingModel.embed(document);
                    document.setEmbedding(embedded);
                    return document;
                }).toList();

        List<Mapping> mappingList = new ArrayList<>();
        productDTOList.stream().map(ProductDTO::getId).toList()
                .forEach(productId -> {
                    Mapping mapping = new Mapping();
                    mapping.setMappedId(productId);
                    mapping.setDbUri(BING_BAO_MONGO_URI);
                    mapping.setCollectionName(BING_BAO_PRODUCT_COLLECTION_NAME);
                    mappingList.add(mapping);
                });
        mappingRepository.saveAll(mappingList);

        return productDocs;
    }
}
