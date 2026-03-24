package com.crownhorse.app.models;

import java.util.List;
import java.util.Map;

public class Conversation {
    private String conversationId;
    private List<String> memberIds;
    private String lastMessage;
    private long lastMessageAt;
    private Map<String, Integer> unreadCounts;

    public Conversation() {}

    public Conversation(String conversationId, List<String> memberIds, String lastMessage,
                        long lastMessageAt, Map<String, Integer> unreadCounts) {
        this.conversationId = conversationId;
        this.memberIds = memberIds;
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
        this.unreadCounts = unreadCounts;
    }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public List<String> getMemberIds() { return memberIds; }
    public void setMemberIds(List<String> memberIds) { this.memberIds = memberIds; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public long getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(long lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public Map<String, Integer> getUnreadCounts() { return unreadCounts; }
    public void setUnreadCounts(Map<String, Integer> unreadCounts) { this.unreadCounts = unreadCounts; }
}
