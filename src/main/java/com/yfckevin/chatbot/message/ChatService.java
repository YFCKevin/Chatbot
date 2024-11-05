package com.yfckevin.chatbot.message;

import com.yfckevin.chatbot.entity.Chat;

import java.util.List;
import java.util.Optional;

public interface ChatService {
    Optional<Chat> findFirstByMemberIdAndProjectNameOrderByCreationDateDesc(String memberId, String projectName);

    void save(Chat chat);
}
