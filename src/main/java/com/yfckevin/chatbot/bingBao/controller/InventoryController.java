package com.yfckevin.chatbot.bingBao.controller;

import com.yfckevin.chatbot.Advisors.MyVectorStoreChatMemoryAdvisor;
import com.yfckevin.chatbot.Advisors.TokenUsageLogAdvisor;
import com.yfckevin.chatbot.bingBao.service.InventoryService;
import com.yfckevin.chatbot.message.dto.ChatMemory;
import com.yfckevin.chatbot.message.MessageService;
import com.yfckevin.chatbot.message.dto.MessageText;
import com.yfckevin.chatbot.utils.ChatUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

import static com.yfckevin.chatbot.GlobalConstants.*;

@Slf4j
@RestController
@RequestMapping("/ai/bingBao/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    private final VectorStore vectorStore;
    private final MessageService messageService;
    private final ChatClient chatClient;
    private final RedisTemplate<String, String> redisTemplate;
    private HashOperations<String, String, String> hashOperations;
    private final ChatUtil chatUtil;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public InventoryController(InventoryService inventoryService, VectorStore vectorStore, MessageService messageService, ChatClient.Builder chatClientBuilder, RedisTemplate<String, String> redisTemplate, ChatUtil chatUtil) {
        this.inventoryService = inventoryService;
        this.vectorStore = vectorStore;
        this.messageService = messageService;
        this.chatClient = chatClientBuilder.build();
        this.redisTemplate = redisTemplate;
        this.chatUtil = chatUtil;
    }

    @GetMapping("/readInventory")
    public List<Document> readInventory() {
        final List<Document> documents = inventoryService.dailyImportInventories();
        return documents;
    }

    @GetMapping("/importInventory")
//    @Scheduled(cron = "0 0 */4 * * ?")
    public ResponseEntity<?> importInventory() {
        final List<Document> documents = inventoryService.dailyImportInventories();
        vectorStore.write(chatUtil.keywordDocuments(documents));
        return ResponseEntity.ok(documents.size());
    }

    /**
     * 記憶式詢問食材庫存對話
     * @param query
     * @param memberId
     * @param chatChannel
     * @return
     */
    @GetMapping("/chat")
    public String inventorySearch(String query, String memberId, String chatChannel) {
        System.out.println("chatChannel = " + chatChannel);
        // 組裝chatId
        if (StringUtils.isBlank(chatChannel)) {
            chatChannel = ChatUtil.genChannelNum();
        }
        String chatId = BING_BAO_PROJECT_NAME + "_" + memberId + "_" + chatChannel;
        System.out.println("chatId = " + chatId);

        List<ChatMemory> inventoryList = messageService.findInventoryByType(BING_BAO_INVENTORY_METADATA_TYPE);
        final List<MessageText> messageInventoryList = inventoryList.stream()
                .map(chatMemory -> {
                    MessageText inventory = new MessageText();
                    inventory.setText(chatMemory.getText());
                    return inventory;
                }).toList();

        String inventoryData = messageInventoryList.stream()
                .map(MessageText::getText)
                .collect(Collectors.joining("\n"));
        inventoryData = String.format("以下是食材庫存資料：\n%s", inventoryData);
        System.out.println("庫存資料 = " + inventoryData);

        return chatClient.prompt()
                .advisors(new MyVectorStoreChatMemoryAdvisor(vectorStore, chatId, 20), new TokenUsageLogAdvisor())
                .advisors(context -> {
                    context.param("chatId", chatId);
                    context.param("lastN", 20);
                })
                .system(inventoryData)
                .user(query)
                .functions("currentDateTime")
                .call()
                .content();
    }
}
