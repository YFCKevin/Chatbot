package com.yfckevin.chatbot.badminton.entity;

import com.yfckevin.chatbot.badminton.enums.CityType;
import com.yfckevin.chatbot.badminton.enums.DistrictType;

public class Court {
    private String id;
    private String name;    //球館名稱
    private String address; //地理位置地址
    private CityType city;      // 城市
    private DistrictType district;  // 區域
    private double latitude;    //緯度
    private double longitude;   //經度
    private String postId;  //貼文編號，用逗號連接
    private String creationDate;

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public CityType getCity() {
        return city;
    }

    public void setCity(CityType city) {
        this.city = city;
    }

    public DistrictType getDistrict() {
        return district;
    }

    public void setDistrict(DistrictType district) {
        this.district = district;
    }
}
