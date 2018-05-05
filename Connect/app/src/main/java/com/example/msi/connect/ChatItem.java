package com.example.msi.connect;

public class ChatItem {
     private String chatid;
     private String chatmessage;
     private String chattime;


    public ChatItem(String chatid, String chatmessage, String chattime){
        this.chatid = chatid;
        this.chatmessage = chatmessage;
        this.chattime = chattime;


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
}