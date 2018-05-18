package com.example.msi.connect;

public class Chatroomitem {
     private String chatroomid;
     private String chatroom_in;
     private String recentmsg;
     private String timestamp;
     private int readchk;


    public Chatroomitem(String chatroomid, String chatroom_in, String recentmsg, String timestamp,int readchk){
        this.chatroomid = chatroomid;
        this.chatroom_in = chatroom_in;
        this.recentmsg = recentmsg;
        this.readchk = readchk;
        this.timestamp = timestamp;
    }

    public String getchatroomid() {
        return chatroomid;
    }
    public String getchatroom_in() {
        return chatroom_in;
    }
    public String getrecentmsg() {
        return recentmsg;
    }
    public String gettimestamp() {
        return timestamp;
    }
    public int getreadchk() {
        return readchk;
    }


}