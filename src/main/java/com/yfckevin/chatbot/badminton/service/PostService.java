package com.yfckevin.chatbot.badminton.service;

import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PostService {
    List<Document> dailyImportPosts();
}
