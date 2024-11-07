package com.yfckevin.chatbot.bingBao.controller;

import com.yfckevin.chatbot.advisors.MyVectorStoreChatMemoryAdvisor;
import com.yfckevin.chatbot.advisors.TokenUsageLogAdvisor;
import com.yfckevin.chatbot.bingBao.entity.Inventory;
import com.yfckevin.chatbot.bingBao.service.InventoryService;
import com.yfckevin.chatbot.message.dto.ChatMemory;
import com.yfckevin.chatbot.message.MessageService;
import com.yfckevin.chatbot.message.dto.ChatMessageDTO;
import com.yfckevin.chatbot.message.dto.MessageText;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

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

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public InventoryController(InventoryService inventoryService, VectorStore vectorStore, MessageService messageService, ChatClient.Builder chatClientBuilder, RedisTemplate<String, String> redisTemplate) {
        this.inventoryService = inventoryService;
        this.vectorStore = vectorStore;
        this.messageService = messageService;
        this.chatClient = chatClientBuilder.build();
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/readInventory")
    public List<Inventory> readInventory() {
        final List<Inventory> documents = inventoryService.dailyImportInventories(fetchRefreshData());
        cleanRedisIndex();
        return documents;
    }

    @GetMapping("/importInventory")
    @Scheduled(cron = "0 0 0 * * ?")
    public ResponseEntity<?> importInventory() {
        final List<Inventory> documents = inventoryService.dailyImportInventories(fetchRefreshData());
        cleanRedisIndex();
        return ResponseEntity.ok(documents.size());
    }

    /**
     * 記憶式詢問食材庫存對話
     * @return
     */
    @PostMapping("/chat")
    public String inventorySearch(@RequestBody ChatMessageDTO dto) {
        final String query = dto.getQuery();
        final String memberId = dto.getMemberId();
        final String chatChannel = dto.getChatChannel();
        // 組裝chatId
        String chatId = BING_BAO_PROJECT_NAME + "_" + memberId + "_" + chatChannel;
        log.info("chatId = " + chatId);

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

        if (StringUtils.isBlank(inventoryData)) return "查無資料";

        inventoryData = String.format("以下是食材庫存資料：\n%s", inventoryData);
        log.info("庫存資料 = " + inventoryData);

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

    private List<Map<String, String>> fetchRefreshData() {
        List<Map<String, String>> data = new ArrayList<>();
        Optional.ofNullable(redisTemplate.opsForSet().members(BING_BAO_INVENTORY_KEY_PREFIX))   //get inventoryId index
                .ifPresent(inventoryIds -> inventoryIds.forEach(inventoryId -> {
                    final Map<String, String> entries = hashOperations.entries(inventoryId);
                    data.add(entries);
                }));
        return data;
    }

    private void cleanRedisIndex() {
        List<String> keysToDelete = new ArrayList<>();
        keysToDelete.add(BING_BAO_INVENTORY_KEY_PREFIX);
        Optional.ofNullable(redisTemplate.opsForSet().members(BING_BAO_INVENTORY_KEY_PREFIX))
                .ifPresent(keysToDelete::addAll);
        redisTemplate.delete(keysToDelete);    //delete index (Set) and delete all inventory data (Hash)
    }
}
