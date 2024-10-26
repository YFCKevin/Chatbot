package com.yfckevin.chatbot.message;

import com.yfckevin.chatbot.message.dto.ChatMemory;

import java.util.List;

public interface MessageService {
    List<ChatMemory> findInventoryByType(String metadataType);

    List<ChatMemory> findByProjectNameAndMemberIdAndChatChannel(String projectName, String memberId, String chatChannel);

    List<ChatMemory> findByProjectNameAndMemberId(String projectName, String memberId);

    List<ChatMemory> findPostByType(String badmintonPostMetadataType);
}
