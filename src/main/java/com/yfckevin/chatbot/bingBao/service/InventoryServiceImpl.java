package com.yfckevin.chatbot.bingBao.service;

import com.yfckevin.chatbot.bingBao.entity.Inventory;
import com.yfckevin.chatbot.bingBao.entity.Product;
import com.yfckevin.chatbot.bingBao.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    @Override
    public List<Inventory> dailyImportInventories(List<Map<String, String>> invnetoryList) {

        //取出所有要更新的inventory
        Set<String> inventoryIds = new HashSet<>();
        invnetoryList.forEach(im -> inventoryIds.add(im.get("id")));
        List<Inventory> inventories = inventoryRepository.findByInventoryIds(inventoryIds);
        Map<String, Inventory> inventoryMapFromDB = inventories.stream()
                .collect(Collectors.toMap(Inventory::getInventoryId, inventory -> inventory, (existing, replacement) -> existing));

        //新增or更新inventory
        List<Inventory> finalInventoryList = new ArrayList<>();
        if (inventoryMapFromDB.isEmpty()) {
            invnetoryList.forEach(inventoryMap -> {
                Inventory newInventory = new Inventory();
                constructInventory(newInventory, inventoryMap, inventoryMap.get("id"));
                finalInventoryList.add(newInventory);
            });
        } else {
            invnetoryList.forEach(inventoryMap -> {
                final String inventoryId = inventoryMap.get("id");
                Inventory inventory = inventoryMapFromDB.get(inventoryId);
                if (inventory != null) {
                    constructInventory(inventory, inventoryMap, inventoryId);
                    finalInventoryList.add(inventory);
                } else {
                    Inventory newInventory = new Inventory();
                    constructInventory(newInventory, inventoryMap, inventoryId);
                    finalInventoryList.add(newInventory);
                }
            });
        }
        inventoryRepository.saveAll(finalInventoryList);
        //更新其餘inventory的validStr狀態
        return updateExpireStatus();
    }

    private static void constructInventory(Inventory newInventory, Map<String, String> inventoryMap, String inventoryId) {
        newInventory.setStorePlace(inventoryMap.getOrDefault("storePlace", null));
        newInventory.setUsedDate(inventoryMap.getOrDefault("usedDate", null));
        newInventory.setDeletionDate(inventoryMap.getOrDefault("deletionDate", null));

        final String expiryDate = inventoryMap.getOrDefault("expiryDate", null);
        String validStr = "在有效期限內"; // 預設為在有效期限內
        if (expiryDate != null) {
            LocalDate expiry = LocalDate.parse(expiryDate);
            boolean isValid = expiry.isAfter(LocalDate.now()) || expiry.isEqual(LocalDate.now());
            validStr = isValid ? "在有效期限內" : "已過期";
        }
        newInventory.setValidStr(validStr);
        newInventory.setProductId(inventoryMap.getOrDefault("productId", null));
        newInventory.setCreator(inventoryMap.getOrDefault("creator", null));
        newInventory.setCreationDate(inventoryMap.getOrDefault("creationDate", null));
        newInventory.setInventoryId(inventoryId); // 使用 id 作為新 inventory 的 ID
        newInventory.setExpiryDate(expiryDate);
        newInventory.setSupplierId(inventoryMap.getOrDefault("supplierId", null));
    }

    private List<Inventory> updateExpireStatus (){
        List<Inventory> inventoryList = inventoryRepository.findByValidStr("在有效期限內");
        System.out.println("inventoryList = " + inventoryList);
        inventoryList = inventoryList.stream()
                .peek(inventory -> {
                    LocalDate expiry = LocalDate.parse(inventory.getExpiryDate());
                    boolean isValid = expiry.isAfter(LocalDate.now()) || expiry.isEqual(LocalDate.now());
                    String validStr = isValid ? "在有效期限內" : "已過期";
                    inventory.setValidStr(validStr);
                }).toList();
        return inventoryRepository.saveAll(inventoryList);
    }
}
