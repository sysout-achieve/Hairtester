package com.example.msi.connect;

import android.graphics.Bitmap;

public class ShowroomItem {

     private String text;
     private String img;
     private String date;
     private int heart;
     private int num;

    public ShowroomItem(String text, String img, String date, int heart, int num){
        this.text = text;
        this.img = img;
        this.date = date;
        this.heart = heart;
        this.num = num;
    }

    public String gettext() {
        return text;
    }
    public String getimg() {
        return img;
    }
    public String getdate() {
        return date;
    }
    public int getheart() {
        return heart;
    }
    public int getnum() {
        return num;
    }

}