package com.yfckevin.chatbot.badminton.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface PostService {
    List<Document> dailyImportPosts();
}
