package com.yfckevin.chatbot.badminton.enums;

public enum CityType {
    Keelung(1, "基隆"),
    Taipei(2, "台北"),
    NewTaipei(3, "新北"),
    Taoyuan(4, "桃園");

    private int value;
    private String label;

    private CityType() {
    }

    private CityType(int value, String label) {
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