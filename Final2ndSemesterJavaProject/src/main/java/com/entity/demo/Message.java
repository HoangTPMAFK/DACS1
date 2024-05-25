package com.entity.demo;

public class Message {
    private String username;
    private String messageContent;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getMessageContent() {
        return messageContent;
    }
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
    public Message(String username, String messageContent) {
        this.username = username;
        this.messageContent = messageContent;
    }
}
