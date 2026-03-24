package com.crownhorse.app.repository;

import android.net.Uri;

import com.crownhorse.app.models.Conversation;
import com.crownhorse.app.models.Message;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRepository {
    private static final String CONVERSATIONS = "conversations";
    private static final String MESSAGES = "messages";
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public ChatRepository() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void getOrCreateConversation(String uid1, String uid2, Callback<String> callback) {
        db.collection(CONVERSATIONS)
                .whereArrayContains("memberIds", uid1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (var doc : querySnapshot.getDocuments()) {
                        Conversation conv = doc.toObject(Conversation.class);
                        if (conv != null && conv.getMemberIds() != null
                                && conv.getMemberIds().contains(uid2)) {
                            if (callback != null) callback.onSuccess(doc.getId());
                            return;
                        }
                    }
                    // Create new conversation
                    String convId = db.collection(CONVERSATIONS).document().getId();
                    Map<String, Object> convData = new HashMap<>();
                    convData.put("conversationId", convId);
                    convData.put("memberIds", Arrays.asList(uid1, uid2));
                    convData.put("lastMessage", "");
                    convData.put("lastMessageAt", System.currentTimeMillis());
                    Map<String, Integer> unread = new HashMap<>();
                    unread.put(uid1, 0);
                    unread.put(uid2, 0);
                    convData.put("unreadCounts", unread);
                    db.collection(CONVERSATIONS).document(convId).set(convData)
                            .addOnSuccessListener(aVoid -> {
                                if (callback != null) callback.onSuccess(convId);
                            })
                            .addOnFailureListener(e -> {
                                if (callback != null) callback.onFailure(e);
                            });
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void sendMessage(String conversationId, Message message, Callback<Void> callback) {
        String msgId = db.collection(CONVERSATIONS).document(conversationId)
                .collection(MESSAGES).document().getId();
        message.setMessageId(msgId);
        db.collection(CONVERSATIONS).document(conversationId)
                .collection(MESSAGES).document(msgId).set(message)
                .addOnSuccessListener(aVoid -> {
                    Map<String, Object> update = new HashMap<>();
                    update.put("lastMessage", message.getType().equals("image") ? "📷 Image" : message.getText());
                    update.put("lastMessageAt", message.getSentAt());
                    db.collection(CONVERSATIONS).document(conversationId).update(update);
                    if (callback != null) callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public ListenerRegistration getMessages(String conversationId, Callback<List<Message>> callback) {
        return db.collection(CONVERSATIONS).document(conversationId)
                .collection(MESSAGES)
                .orderBy("sentAt", Query.Direction.ASCENDING)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        if (callback != null) callback.onFailure(e);
                        return;
                    }
                    List<Message> messages = new ArrayList<>();
                    if (querySnapshot != null) {
                        for (var doc : querySnapshot.getDocuments()) {
                            Message m = doc.toObject(Message.class);
                            if (m != null) messages.add(m);
                        }
                    }
                    if (callback != null) callback.onSuccess(messages);
                });
    }

    public void markMessagesSeen(String conversationId, String userId) {
        db.collection(CONVERSATIONS).document(conversationId)
                .collection(MESSAGES)
                .whereNotEqualTo("senderId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (var doc : querySnapshot.getDocuments()) {
                        Message m = doc.toObject(Message.class);
                        if (m != null && (m.getSeenBy() == null || !m.getSeenBy().contains(userId))) {
                            doc.getReference().update("seenBy",
                                    com.google.firebase.firestore.FieldValue.arrayUnion(userId));
                        }
                    }
                    Map<String, Object> update = new HashMap<>();
                    update.put("unreadCounts." + userId, 0);
                    db.collection(CONVERSATIONS).document(conversationId).update(update);
                });
    }

    public ListenerRegistration getConversations(String userId, Callback<List<Conversation>> callback) {
        return db.collection(CONVERSATIONS)
                .whereArrayContains("memberIds", userId)
                .orderBy("lastMessageAt", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        if (callback != null) callback.onFailure(e);
                        return;
                    }
                    List<Conversation> conversations = new ArrayList<>();
                    if (querySnapshot != null) {
                        for (var doc : querySnapshot.getDocuments()) {
                            Conversation c = doc.toObject(Conversation.class);
                            if (c != null) conversations.add(c);
                        }
                    }
                    if (callback != null) callback.onSuccess(conversations);
                });
    }

    public void uploadChatImage(Uri imageUri, String conversationId, Callback<String> callback) {
        String fileName = "chat/" + conversationId + "/" + System.currentTimeMillis() + ".jpg";
        StorageReference ref = storage.getReference().child(fileName);
        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            if (callback != null) callback.onSuccess(uri.toString());
                        })
                        .addOnFailureListener(e -> {
                            if (callback != null) callback.onFailure(e);
                        }))
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }
}
