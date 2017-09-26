package com.qg.smartprinter.logic.model;

import java.io.Serializable;

/**
 * @author TZH
 * @version 1.0
 */
public class Business implements Serializable{
    private String name;
    private String address;
    private String phone;
    private String advertisement; // 广告语
    private byte[] picture; // 图片
    private String url; // URL -- 用于生成二维码

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(String advertisement) {
        this.advertisement = advertisement;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
