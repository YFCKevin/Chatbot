package com.yfckevin.chatbot.utils;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class ChatUtil {
    private final ChatModel chatModel;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    public ChatUtil(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public static String genChannelNum() {
        StringBuilder channel = new StringBuilder("C-");

        // 產生8位的隨機字母和數字
        for (int i = 0; i < 8; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            channel.append(CHARACTERS.charAt(randomIndex));
        }

        return channel.toString();
    }

    public List<Document> keywordDocuments(List<Document> documents) {
        documents = documents.stream().map(document -> {
            PromptTemplate template = new PromptTemplate(String.format("{context_str}. Give %s unique keywords for this\ndocument. Format as comma separated. Keywords:", 3));
            Prompt prompt = template.create(Map.of("context_str", document.getContent()));
            String keywords = this.chatModel.call(prompt).getResult().getOutput().getContent();

            Map<String, Object> newMetadata = new HashMap<>(document.getMetadata());
            String cleanedKeywords = keywords.replaceAll("Keywords: ", "").trim();
            newMetadata.put("keywords", cleanedKeywords);

            return new Document(document.getId(), document.getContent(), newMetadata);
        }).toList();
        return documents;
    }
}
