package com.example.msi.connect;

import android.graphics.Bitmap;

public class OrderItem {

    private String pd_title;
    private String buyer;
    private String phone;
    private String price;
    private String date;
    private String seller;
    private String serviceable;
    private int num;



    public OrderItem(String pd_title, String buyer, String phone, String price, String date, String seller, String serviceable, int num){
        this.pd_title = pd_title;
        this.buyer = buyer;
        this.phone = phone;
        this.price = price;
        this.date = date;
        this.seller = seller;
        this.serviceable = serviceable;
        this.num = num;
    }

    public String getpd_title() {
        return pd_title;
    }
    public String getbuyer() {
        return buyer;
    }
    public String getphone() {
        return phone;
    }
    public String getprice(){
        return price;
    }
    public String getdate(){
        return date;
    }
    public String getseller(){
        return seller;
    }
    public String getserviceable(){
        return serviceable;
    }
    public int getnum(){
        return num;
    }
}
