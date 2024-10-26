package com.yfckevin.chatbot.message.dto;

public class MessageText {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "MessageText{" +
                "text='" + text + '\'' +
                '}';
    }
}
