package com.yfckevin.chatbot.bingBao.service;

import com.yfckevin.chatbot.bingBao.entity.Product;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {
    List<Document> dailyImportProducts(List<Map<String, String>> productList);

    Optional<Product> findByProductId(String productId);
}
