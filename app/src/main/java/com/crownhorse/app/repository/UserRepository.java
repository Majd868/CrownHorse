package com.crownhorse.app.repository;

import com.crownhorse.app.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final String COLLECTION = "users";
    private final FirebaseFirestore db;

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void saveUser(User user, Callback<Void> callback) {
        db.collection(COLLECTION).document(user.getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void getUser(String uid, Callback<User> callback) {
        db.collection(COLLECTION).document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        if (callback != null) callback.onSuccess(user);
                    } else {
                        if (callback != null) callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void updatePresence(String uid, boolean isOnline) {
        Map<String, Object> data = new HashMap<>();
        data.put("online", isOnline);
        data.put("lastSeen", System.currentTimeMillis());
        db.collection(COLLECTION).document(uid).update(data);
    }

    public void saveFcmToken(String uid, String token) {
        Map<String, Object> data = new HashMap<>();
        data.put("fcmToken", token);
        db.collection(COLLECTION).document(uid).update(data);
    }
}
