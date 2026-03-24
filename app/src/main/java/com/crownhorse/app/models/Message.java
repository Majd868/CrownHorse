package com.crownhorse.app.models;

import java.util.List;

public class Message {
    private String messageId;
    private String senderId;
    private String type;
    private String text;
    private String imageUrl;
    private long sentAt;
    private List<String> seenBy;

    public Message() {}

    public Message(String messageId, String senderId, String type, String text,
                   String imageUrl, long sentAt, List<String> seenBy) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.type = type;
        this.text = text;
        this.imageUrl = imageUrl;
        this.sentAt = sentAt;
        this.seenBy = seenBy;
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getSentAt() { return sentAt; }
    public void setSentAt(long sentAt) { this.sentAt = sentAt; }

    public List<String> getSeenBy() { return seenBy; }
    public void setSeenBy(List<String> seenBy) { this.seenBy = seenBy; }
}
