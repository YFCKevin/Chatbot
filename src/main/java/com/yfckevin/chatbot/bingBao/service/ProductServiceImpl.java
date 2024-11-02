package com.yfckevin.chatbot.bingBao.service;

import com.yfckevin.chatbot.bingBao.entity.Inventory;
import com.yfckevin.chatbot.bingBao.entity.Product;
import com.yfckevin.chatbot.bingBao.repository.InventoryRepository;
import com.yfckevin.chatbot.bingBao.repository.ProductRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.yfckevin.chatbot.GlobalConstants.*;

@Service
public class ProductServiceImpl implements ProductService {
    private final EmbeddingModel embeddingModel;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductServiceImpl(EmbeddingModel embeddingModel, ProductRepository productRepository,
                              InventoryRepository inventoryRepository) {
        this.embeddingModel = embeddingModel;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    @Override
    public List<Document> dailyImportProducts(List<Map<String, String>> productList) {

        //取出所有要更新的product
        Set<String> productIds = new HashSet<>();
        productList.forEach(p -> productIds.add(p.get("id")));
        List<Product> products = productRepository.findByProductIds(productIds);
        Map<String, Product> productMapFromDB = products.stream()
                .collect(Collectors.toMap(Product::getProductId, product -> product, (existing, replacement) -> existing));

        //取出所有的inventory
        final List<Inventory> inventoryList = inventoryRepository.findAll();
        final Map<String, List<Inventory>> inventoryMap = inventoryList.stream()
                .collect(Collectors.groupingBy(Inventory::getProductId));

        List<Product> finalProductList = new ArrayList<>();
        List<Document> productDocs = new ArrayList<>();

        //新增or更新product
        if (productMapFromDB.isEmpty()) {
            productList.forEach(productMap -> {
                Product newProduct = new Product();
                constructProduct(newProduct, productMap, productMap.get("id"), inventoryMap);
                finalProductList.add(newProduct);
                Document document = createDocument(newProduct);
                productDocs.add(document);
            });
        } else {
            productList.forEach(productMap -> {
                final String productId = productMap.get("id");
                Product product = productMapFromDB.get(productId);
                if (product != null) {
                    constructProduct(product, productMap, productId, inventoryMap);
                    finalProductList.add(product);
                    Document document = createDocument(product);
                    productDocs.add(document);
                } else {
                    Product newProduct = new Product();
                    constructProduct(newProduct, productMap, productId, inventoryMap);
                    finalProductList.add(newProduct);
                    Document document = createDocument(newProduct);
                    productDocs.add(document);
                }
            });
        }

        productRepository.saveAll(finalProductList);

        return productDocs;
    }

    @Override
    public Optional<Product> findByProductId(String productId) {
        return productRepository.findByProductId(productId);
    }

    private void constructProduct(Product newProduct, Map<String, String> productMap, String productId, Map<String, List<Inventory>> inventoryMap) {
        newProduct.setCreator(productMap.getOrDefault("creator", null));
        newProduct.setDescription(productMap.getOrDefault("description", null));
        newProduct.setProductId(productId);
        newProduct.setCoverName(productMap.getOrDefault("coverName", null));
        newProduct.setAddShoppingList(Boolean.parseBoolean(productMap.getOrDefault("addShoppingList", null)));
        newProduct.setCreationDate(productMap.getOrDefault("creationDate", null));
        newProduct.setDeletionDate(productMap.getOrDefault("deletionDate", null));
        newProduct.setInventoryAlert(productMap.getOrDefault("inventoryAlert", null));
        newProduct.setMainCategory(productMap.getOrDefault("mainCategory", null));
        newProduct.setModificationDate(productMap.getOrDefault("modificationDate", null));
        newProduct.setModifier(productMap.getOrDefault("modifier", null));
        newProduct.setName(productMap.getOrDefault("name", null));
        newProduct.setOverdueNotice(productMap.getOrDefault("overdueNotice", null));
        newProduct.setPackageForm(productMap.getOrDefault("packageForm", null));
        newProduct.setPackageNumber(productMap.getOrDefault("packageNumber", null));
        newProduct.setPackageQuantity(productMap.getOrDefault("packageQuantity", null));
        newProduct.setPackageUnit(productMap.getOrDefault("packageUnit", null));
        newProduct.setPrice(Integer.parseInt(productMap.getOrDefault("price", "0")));
        newProduct.setSerialNumber(productMap.getOrDefault("serialNumber", null));
        newProduct.setSubCategory(productMap.getOrDefault("subCategory", null));
        newProduct.setInventoryList(inventoryMap.getOrDefault(productId, new ArrayList<>()));
    }

    private Document createDocument(Product product) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", BING_BAO_PRODUCT_METADATA_TYPE);
        metadata.put("import_date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        metadata.put("product_id", product.getProductId());
        // Create the Document and embed it
        final Document document = new Document(product.getName(), metadata);
        final float[] embedded = embeddingModel.embed(document);
        document.setEmbedding(embedded);
        return document;
    }
}
