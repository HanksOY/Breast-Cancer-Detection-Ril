package com.example.afinal.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message {
    public static final int TYPE_USER = 1;
    public static final int TYPE_ASSISTANT = 2;
    
    private String content;
    private int type;
    private String timestamp;
    
    public Message(String content, int type) {
        this.content = content;
        this.type = type;
        this.timestamp = getCurrentTime();
    }
    
    public String getContent() {
        return content;
    }
    
    public void updateContent(String content) {
        this.content = content;
    }
    
    public int getType() {
        return type;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }
} 