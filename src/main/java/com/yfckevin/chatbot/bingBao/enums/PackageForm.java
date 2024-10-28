package com.yfckevin.chatbot.bingBao.enums;

public enum PackageForm {
    COMPLETE(1,"完整包裝"),BULK(2,"散裝");

    private PackageForm(){
    }

    private int value;
    private String label;
    private PackageForm(int value,String label){
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
