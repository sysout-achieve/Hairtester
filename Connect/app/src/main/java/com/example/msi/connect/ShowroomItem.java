package com.example.msi.connect;

import android.graphics.Bitmap;

public class ShowroomItem {

     private String text;
     private String img;
     private String date;

    public ShowroomItem(String text, String img, String date){
        this.text = text;
        this.img = img;
        this.date = date;
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

}