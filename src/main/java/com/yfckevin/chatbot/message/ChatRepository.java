package com.yfckevin.chatbot.message;

import com.yfckevin.chatbot.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, String> {
    Optional<Chat> findFirstByMemberIdAndProjectNameOrderByCreationDateDesc(String memberId, String projectName);
}
