package com.example.msi.connect;

public class Stylelineitem {

    private String text;
    private String img;
    private String id;
    private String date;
    private int heart;
    private int num;
    private int chk_heart;

    public Stylelineitem(String id, String text, String img, String date, int heart, int num, int chk_heart){
        this.id = id;
        this.text = text;
        this.img = img;
        this.date = date;
        this.heart = heart;
        this.num = num;
        this.chk_heart = chk_heart;
    }

    public String gettext() {
        return text;
    }
    public String getid() {
        return id;
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
    public int getChk_heart() {
        return chk_heart;
    }

}