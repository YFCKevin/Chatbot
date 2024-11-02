package com.yfckevin.chatbot.bingBao.entity;

import com.yfckevin.chatbot.bingBao.enums.PackageForm;
import com.yfckevin.chatbot.bingBao.enums.PackageUnit;
import com.yfckevin.chatbot.bingBao.enums.StorePlace;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node(labels = "Inventory")
public class Inventory {
    @Id
    @GeneratedValue
    private Long id;
    private String inventoryId;
    private String usedDate;    //用食材日期
    private String expiryDate;   //有效日期
    private String validStr;
    private String storePlace;
    private String productId;
    private String supplierId;
    private String creationDate;
    private String deletionDate;
    private String creator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(String usedDate) {
        this.usedDate = usedDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStorePlace() {
        return storePlace;
    }

    public void setStorePlace(String storePlace) {
        this.storePlace = storePlace;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(String deletionDate) {
        this.deletionDate = deletionDate;
    }

    public String getValidStr() {
        return validStr;
    }

    public void setValidStr(String validStr) {
        this.validStr = validStr;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", inventoryId='" + inventoryId + '\'' +
                ", usedDate='" + usedDate + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", validStr='" + validStr + '\'' +
                ", storePlace='" + storePlace + '\'' +
                ", productId='" + productId + '\'' +
                ", supplierId='" + supplierId + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", deletionDate='" + deletionDate + '\'' +
                ", creator='" + creator + '\'' +
                '}';
    }
}

