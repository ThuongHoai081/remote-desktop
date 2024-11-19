package com.remote.client.model;

public class Message {
    private String content;
    private String senderType;

    public Message(String content, String ipSender) {
        this.content = content;
        this.senderType = ipSender;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String ipSender) {
        this.senderType = ipSender;
    }

    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return senderType + ": " + content;
    }
}
