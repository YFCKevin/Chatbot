package com.yfckevin.chatbot.bingBao.entity;

import org.springframework.data.neo4j.core.schema.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Node(labels = "Product")
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    private String productId;
    private String serialNumber; //序號
    private String name;    //名稱
    private String description; //食材描述
    private String coverName;   //圖片檔名
    private int price;
    private String mainCategory;  //食材種類 (水果、青菜、海鮮、藥品等等)
    private String subCategory;    //食材副總類
    private String packageForm;    //包裝形式 (完整包裝、散裝)
    private String packageUnit;    //包裝單位 (包、瓶、個等等)
    private String packageQuantity;     //包裝數量
    private String overdueNotice;   //通知過期天數
    private String packageNumber;   //批次建檔編號
    private boolean addShoppingList;    //是否加入購物清單
    private String inventoryAlert;  //通知購物的庫存量警報
    private String creationDate;
    private String modificationDate;
    private String deletionDate;
    private String creator;
    private String modifier;
    @Relationship(type = "HAS_INVENTORY", direction = Relationship.Direction.OUTGOING)
    private List<Inventory> inventoryList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Inventory> getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(List<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverName() {
        return coverName;
    }

    public void setCoverName(String coverName) {
        this.coverName = coverName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getPackageForm() {
        return packageForm;
    }

    public void setPackageForm(String packageForm) {
        this.packageForm = packageForm;
    }

    public String getPackageUnit() {
        return packageUnit;
    }

    public void setPackageUnit(String packageUnit) {
        this.packageUnit = packageUnit;
    }

    public String getPackageQuantity() {
        return packageQuantity;
    }

    public void setPackageQuantity(String packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

    public String getOverdueNotice() {
        return overdueNotice;
    }

    public void setOverdueNotice(String overdueNotice) {
        this.overdueNotice = overdueNotice;
    }

    public String getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(String packageNumber) {
        this.packageNumber = packageNumber;
    }

    public boolean isAddShoppingList() {
        return addShoppingList;
    }

    public void setAddShoppingList(boolean addShoppingList) {
        this.addShoppingList = addShoppingList;
    }

    public String getInventoryAlert() {
        return inventoryAlert;
    }

    public void setInventoryAlert(String inventoryAlert) {
        this.inventoryAlert = inventoryAlert;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(String deletionDate) {
        this.deletionDate = deletionDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productId='" + productId + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", coverName='" + coverName + '\'' +
                ", price=" + price +
                ", mainCategory='" + mainCategory + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", packageForm='" + packageForm + '\'' +
                ", packageUnit='" + packageUnit + '\'' +
                ", packageQuantity='" + packageQuantity + '\'' +
                ", overdueNotice='" + overdueNotice + '\'' +
                ", packageNumber='" + packageNumber + '\'' +
                ", addShoppingList=" + addShoppingList +
                ", inventoryAlert='" + inventoryAlert + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", modificationDate='" + modificationDate + '\'' +
                ", deletionDate='" + deletionDate + '\'' +
                ", creator='" + creator + '\'' +
                ", modifier='" + modifier + '\'' +
                ", inventoryList=" + inventoryList +
                '}';
    }
}
