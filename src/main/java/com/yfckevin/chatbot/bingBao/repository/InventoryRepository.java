package com.yfckevin.chatbot.bingBao.repository;

import com.yfckevin.chatbot.bingBao.entity.Inventory;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface InventoryRepository extends Neo4jRepository<Inventory, Long> {
    @Query("MATCH (inv:Inventory) WHERE inv.inventoryId IN $inventoryIds RETURN inv")
    List<Inventory> findByInventoryIds(@Param(value = "inventoryIds") Set<String> inventoryIds);

    @Query("MATCH (inv:Inventory) WHERE inv.validStr = $validStr RETURN inv")
    List<Inventory> findByValidStr(@Param(value = "validStr") String validStr);
}
