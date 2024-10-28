package com.yfckevin.chatbot.bingBao.entity;

import com.yfckevin.chatbot.bingBao.enums.PackageForm;
import com.yfckevin.chatbot.bingBao.enums.PackageUnit;
import com.yfckevin.chatbot.bingBao.enums.StorePlace;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inventory")
public class Inventory {
    @Id
    private String id;
    private String name;    //食材名稱
    private String serialNumber;    //食材序號
    private String receiveFormId; //收貨唯一編號
    private String receiveFormNumber; //收貨編號
    private String receiveItemId; //收貨明細唯一編號
    private String usedDate;    //用食材日期
    private String storeDate;   //入庫日期
    private String storeNumber; //入庫批號
    private String expiryDate;   //有效日期
    private StorePlace storePlace;
    private PackageForm packageForm;
    private PackageUnit packageUnit;
    private String packageQuantity;
    private String packageNumber;
    private String overdueNotice;   //通知過期天數
    private String productId;
    private String supplierId;
    private boolean addShoppingList;    //庫存是否放進回購清單，用來避免重複加入
    private String creationDate;
    private String modificationDate;
    private String deletionDate;
    private String creator;
    private String modifier;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceiveFormId() {
        return receiveFormId;
    }

    public void setReceiveFormId(String receiveFormId) {
        this.receiveFormId = receiveFormId;
    }

    public String getReceiveFormNumber() {
        return receiveFormNumber;
    }

    public void setReceiveFormNumber(String receiveFormNumber) {
        this.receiveFormNumber = receiveFormNumber;
    }

    public String getReceiveItemId() {
        return receiveItemId;
    }

    public void setReceiveItemId(String receiveItemId) {
        this.receiveItemId = receiveItemId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(String usedDate) {
        this.usedDate = usedDate;
    }

    public String getStoreDate() {
        return storeDate;
    }

    public void setStoreDate(String storeDate) {
        this.storeDate = storeDate;
    }

    public String getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getOverdueNotice() {
        return overdueNotice;
    }

    public void setOverdueNotice(String overdueNotice) {
        this.overdueNotice = overdueNotice;
    }

    public PackageForm getPackageForm() {
        return packageForm;
    }

    public void setPackageForm(PackageForm packageForm) {
        this.packageForm = packageForm;
    }

    public PackageUnit getPackageUnit() {
        return packageUnit;
    }

    public void setPackageUnit(PackageUnit packageUnit) {
        this.packageUnit = packageUnit;
    }

    public String getPackageQuantity() {
        return packageQuantity;
    }

    public void setPackageQuantity(String packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

    public String getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(String packageNumber) {
        this.packageNumber = packageNumber;
    }

    public StorePlace getStorePlace() {
        return storePlace;
    }

    public void setStorePlace(StorePlace storePlace) {
        this.storePlace = storePlace;
    }

    public boolean isAddShoppingList() {
        return addShoppingList;
    }

    public void setAddShoppingList(boolean addShoppingList) {
        this.addShoppingList = addShoppingList;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
}

