package com.yfckevin.chatbot.bingBao.service;

import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

public interface InventoryService {
    List<Document> dailyImportInventories();
}
