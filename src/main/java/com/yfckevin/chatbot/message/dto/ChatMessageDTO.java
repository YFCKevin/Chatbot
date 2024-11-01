package com.yfckevin.chatbot.message.dto;

import com.yfckevin.chatbot.enums.ChatType;

public class ChatMessageDTO {
    private String query;
    private String memberId;
    private String chatChannel;
    private ChatType chatType;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getChatChannel() {
        return chatChannel;
    }

    public void setChatChannel(String chatChannel) {
        this.chatChannel = chatChannel;
    }

    public ChatType getChatType() {
        return chatType;
    }

    public void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }
}
