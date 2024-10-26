package com.yfckevin.chatbot.enums;

public enum Project {
    BadmintonPairing(1,"羽球配對"),BingBao(2,"冰寶"), InkCLoud(3, "一朵墨");

    private Project(){
    }

    private int value;
    private String label;
    private Project(int value,String label){
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

