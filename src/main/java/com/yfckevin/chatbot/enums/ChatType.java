package com.yfckevin.chatbot.enums;

public enum ChatType {
    Chat(1,"聊天"),
    Join(2,"加入"),
    Leave(3, "離開");

    private ChatType(){
    }

    private int value;
    private String label;
    private ChatType(int value,String label){
        this.value = value;
        this.label = label;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
}

