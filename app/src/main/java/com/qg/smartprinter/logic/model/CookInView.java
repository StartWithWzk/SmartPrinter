package com.qg.smartprinter.logic.model;

import java.io.Serializable;

/**
 * Created by TZH on 2016/7/25.
 */
public class CookInView implements Serializable{
    public int picture;
    public String name;
    public String description;
    public int count;
    public int price;

    public CookInView(int picture, String name, int price) {
        this(picture, name, null, 0, price);
    }

    public CookInView(int picture, String name, String description, int count, int price) {
        this.picture = picture;
        this.name = name;
        this.description = description;
        this.count = count;
        this.price = price;
    }

}
