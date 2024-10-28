package com.yfckevin.chatbot.bingBao.controller;

import com.yfckevin.chatbot.bingBao.service.ProductService;
import com.yfckevin.chatbot.utils.ChatUtil;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.yfckevin.chatbot.GlobalConstants.BING_BAO_PRODUCT_METADATA_TYPE;

@RestController
@RequestMapping("/ai/bingBao/product")
public class ProductController {
    private final ProductService productService;
    private final VectorStore vectorStore;
    private final ChatUtil chatUtil;

    public ProductController(ProductService productService, VectorStore vectorStore, ChatUtil chatUtil) {
        this.productService = productService;
        this.vectorStore = vectorStore;
        this.chatUtil = chatUtil;
    }

    @GetMapping("/readProduct")
    public List<Document> readProduct (){
        return productService.dailyImportProducts();
    }

    @GetMapping("/importProduct")
//    @Scheduled(cron = "0 0 */4 * * ?")
    public ResponseEntity<?> importProduct (){
        vectorStore.write(chatUtil.keywordDocuments(productService.dailyImportProducts()));
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/productSearch")
    public List<Document> productSearch(String query) {
        Filter.Expression filterProjectType = new Filter.Expression(
                Filter.ExpressionType.EQ,
                new Filter.Key("type"),
                new Filter.Value(BING_BAO_PRODUCT_METADATA_TYPE)
        );

        SearchRequest request = SearchRequest.query(query)
                .withTopK(50)  // 可選，設置回傳的資料數量
                .withFilterExpression(filterProjectType);

        List<Document> results = vectorStore.similaritySearch(request);

        results = results.stream()
                .filter(doc -> {
                    Float distance = (Float) doc.getMetadata().get("distance");
                    return distance != null && distance < 0.3;
                })
                .toList();

        return results;
    }
}
