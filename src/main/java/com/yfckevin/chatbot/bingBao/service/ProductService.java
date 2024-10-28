package com.yfckevin.chatbot.bingBao.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface ProductService {
    List<Document> dailyImportProducts();
}
