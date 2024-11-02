package com.yfckevin.chatbot.bingBao.service;

import com.yfckevin.chatbot.bingBao.entity.Inventory;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

public interface InventoryService {
    List<Inventory> dailyImportInventories(List<Map<String, String>> invnetoryList);
}
