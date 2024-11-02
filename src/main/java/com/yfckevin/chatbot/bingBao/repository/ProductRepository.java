package com.yfckevin.chatbot.bingBao.repository;

import com.yfckevin.chatbot.bingBao.entity.Product;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends Neo4jRepository<Product, Long> {
    @Query("MATCH (p:Product) WHERE p.productIds IN $productIds RETURN p")
    List<Product> findByProductIds(@Param(value = "productIds") Set<String> productIds);
    @Query("MATCH (p:Product) WHERE p.productId = $productId " +
            "OPTIONAL MATCH (p)-[:HAS_INVENTORY]->(i:Inventory) " +
            "RETURN p, collect(i) as inventoryList")
    Optional<Product> findByProductId(@Param(value = "productId") String productId);
}
