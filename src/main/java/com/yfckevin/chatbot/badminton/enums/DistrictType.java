package com.yfckevin.chatbot.badminton.enums;

public enum DistrictType {
    Renai(1, "仁愛"),
    Yonghe(2, "永和"),
    Xizhi(3, "汐止"),
    Xinyi(4, "信義"),
    Neihu(5, "內湖"),
    Datong(6, "大同"),
    Longtan(7, "龍潭"),
    Jinshan(8, "金山"),
    Pingzhen(9, "平鎮"),
    Tucheng(10, "土城"),
    Zhongzheng(11, "中正"),
    Banqiao(12, "板橋"),
    Xinwu(13, "新屋"),
    Beitou(14, "北投"),
    Nuannuan(15, "暖暖"),
    Luzhu(16, "蘆竹"),
    Wenshan(17, "文山"),
    Songshan(18, "松山"),
    Yingge(19, "鶯歌"),
    Xinzhuang(20, "新莊"),
    Yangmei(21, "楊梅"),
    Xindian(22, "新店"),
    Shulin(23, "樹林"),
    Nangang(24, "南港"),
    Tamsui(25, "淡水"),
    Luzhou(26, "蘆洲"),
    Daan(27, "大安"),
    Linkou(28, "林口"),
    Taoyuan(29, "桃園"),
    Daxi(30, "大溪"),
    Zhongshan(31, "中山"),
    Zhonghe(32, "中和"),
    Zhongli(33, "中壢"),
    Wanhua(34, "萬華"),
    Shilin(35, "士林"),
    Taishan(36, "泰山"),
    Sanchong(37, "三重"),
    Sanxia(38, "三峽"),
    Bade(39, "八德");

    private int value;
    private String label;

    private DistrictType() {
    }

    private DistrictType(int value, String label) {
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