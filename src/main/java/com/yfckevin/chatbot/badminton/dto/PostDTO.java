package com.yfckevin.chatbot.badminton.dto;

public class PostDTO {
    private String id;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "PostDTO{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
