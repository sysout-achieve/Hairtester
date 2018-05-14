package com.example.msi.connect;

public class ChatItem {
     private String chatid;
     private String chatmessage;
     private String chattime;
     private int check_send;


    public ChatItem(String chatid, String chatmessage, String chattime, int check_send){
        this.chatid = chatid;
        this.chatmessage = chatmessage;
        this.chattime = chattime;
        this.check_send = check_send;


    }

    public String getchatid() {
        return chatid;
    }
    public String getchatmessage() {
        return chatmessage;
    }
    public String getchattime() {
        return chattime;
    }
    public int getCheck_send(){
        return check_send;
    }
}