package com.yfckevin.chatbot.message;

import com.yfckevin.chatbot.message.dto.ChatMemory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService{
    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public List<ChatMemory> findInventoryByType(String metadataType) {
        return messageRepository.findInventoryByType(metadataType);
    }

    @Override
    public List<ChatMemory> findByProjectNameAndMemberIdAndChatChannel(String projectName, String memberId, String chatChannel) {
        return messageRepository.findByProjectNameAndMemberIdAndChatChannel(projectName, memberId, chatChannel);
    }

    @Override
    public List<ChatMemory> findByProjectNameAndMemberId(String projectName, String memberId) {
        return messageRepository.findByProjectNameAndMemberId(projectName, memberId);
    }

    @Override
    public List<ChatMemory> findPostByType(String metadataType) {
        return messageRepository.findPostByType(metadataType);
    }
}
