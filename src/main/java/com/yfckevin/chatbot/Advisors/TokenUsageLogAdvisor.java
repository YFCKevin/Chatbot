package com.yfckevin.chatbot.Advisors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.AdvisedRequest;
import org.springframework.ai.chat.client.RequestResponseAdvisor;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.Map;

@Slf4j
public class TokenUsageLogAdvisor implements RequestResponseAdvisor {
    @Override
    public AdvisedRequest adviseRequest(AdvisedRequest request, Map<String, Object> context) {
        log.info("chatId: {}, prompt: {}", context.get("chatId"), request.userText());
        return RequestResponseAdvisor.super.adviseRequest(request, context);
    }

    @Override
    public ChatResponse adviseResponse(ChatResponse response, Map<String, Object> context) {
        log.info("chatId: {}, response msg: {}", context.get("chatId"), response.getResult().getOutput().getContent());
        log.info("promptToken: {}", response.getMetadata().getUsage().getPromptTokens());
        log.info("genToken: {}", response.getMetadata().getUsage().getGenerationTokens());
        log.info("totalToken: {}", response.getMetadata().getUsage().getTotalTokens());
        return RequestResponseAdvisor.super.adviseResponse(response, context);
    }
}
