package com.yfckevin.chatbot.message;

import com.yfckevin.chatbot.entity.Chat;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService{
    private final ChatRepository chatRepository;

    public ChatServiceImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public Optional<Chat> findFirstByMemberIdAndProjectNameOrderByCreationDateDesc(String memberId, String projectName) {
        return chatRepository.findFirstByMemberIdAndProjectNameOrderByCreationDateDesc(memberId, projectName);
    }

    @Override
    public void save(Chat chat) {
        chatRepository.save(chat);
    }
}
