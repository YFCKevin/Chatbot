package com.yfckevin.chatbot.badminton.controller;

import com.yfckevin.chatbot.Advisors.MyVectorStoreChatMemoryAdvisor;
import com.yfckevin.chatbot.Advisors.TokenUsageLogAdvisor;
import com.yfckevin.chatbot.badminton.service.PostService;
import com.yfckevin.chatbot.message.MessageService;
import com.yfckevin.chatbot.message.dto.ChatMemory;
import com.yfckevin.chatbot.message.dto.MessageText;
import com.yfckevin.chatbot.utils.ChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

import static com.yfckevin.chatbot.GlobalConstants.*;

@RestController
@RequestMapping("/ai/badminton/post")
public class PostController {
    private final PostService postService;
    private final VectorStore vectorStore;
    private final MessageService messageService;
    private final ChatClient chatClient;
    private final ChatUtil chatUtil;

    public PostController(PostService postService, VectorStore vectorStore, MessageService messageService, ChatClient.Builder chatClientBuilder, ChatUtil chatUtil) {
        this.postService = postService;
        this.vectorStore = vectorStore;
        this.messageService = messageService;
        this.chatClient = chatClientBuilder.build();
        this.chatUtil = chatUtil;
    }

    @GetMapping("/readPost")
    public List<Document> readPost() {
        return postService.dailyImportPosts();
    }

    @GetMapping("/importPost")
    @Scheduled(cron = "0 0 0 * * ?")
    public ResponseEntity<?> importPost() {
        vectorStore.write(chatUtil.keywordDocuments(postService.dailyImportPosts()));
        return ResponseEntity.ok("ok");
    }

    /**
     * 記憶式詢問零打資訊對話
     *
     * @param query
     * @param memberId
     * @param chatChannel
     * @return
     */
    @GetMapping("/chat")
    public String postChat(String query, String memberId, String chatChannel) {
        // 組裝chatId
        if (StringUtils.isBlank(chatChannel)) {
            chatChannel = ChatUtil.genChannelNum();
        }
        String chatId = BADMINTON_PROJECT_NAME + "_" + memberId + "_" + chatChannel;

        List<ChatMemory> postList = messageService.findPostByType(BADMINTON_POST_METADATA_TYPE);
        final List<MessageText> messagePostList = postList.stream()
                .map(chatMemory -> {
                    MessageText post = new MessageText();
                    post.setText(chatMemory.getText());
                    return post;
                }).toList();

        String postData = messagePostList.stream()
                .map(MessageText::getText)
                .collect(Collectors.joining("\n"));
        postData = String.format("以下是打羽球的資訊：\n%s", postData);
        System.out.println("零打資料 = " + postData);

        return chatClient.prompt()
                .advisors(new MyVectorStoreChatMemoryAdvisor(vectorStore, chatId, 20), new TokenUsageLogAdvisor())
                .advisors(context -> {
                    context.param("chatId", chatId);
                    context.param("lastN", 20);
                })
                .system(postData)
                .user(query)
                .functions("currentDateTime")
                .call()
                .content();
    }
}
