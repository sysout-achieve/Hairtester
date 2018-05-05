package com.example.msi.connect;

import java.util.Date;

public class Chatmessage {
    private String messageTxt;
    private String messageUser;
    private long messageTime;

    public Chatmessage(String messageTxt, String messageUser){
        this.messageTxt = messageTxt;
        this.messageUser = messageUser;

        messageTime = new Date().getTime();
    }

    public Chatmessage(String messageTxt) {
        this.messageTxt = messageTxt;
    }

    public String getMessageTxt() {
        return messageTxt;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }
}
