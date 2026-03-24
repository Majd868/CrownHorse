package com.crownhorse.app.repository;

import android.net.Uri;

import com.crownhorse.app.models.Horse;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HorseRepository {
    private static final String COLLECTION = "horses";
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public HorseRepository() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public void addHorse(Horse horse, Callback<String> callback) {
        String id = db.collection(COLLECTION).document().getId();
        horse.setHorseId(id);
        db.collection(COLLECTION).document(id).set(horse)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(id);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void updateHorse(Horse horse, Callback<Void> callback) {
        db.collection(COLLECTION).document(horse.getHorseId()).set(horse)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void deleteHorse(String horseId, Callback<Void> callback) {
        db.collection(COLLECTION).document(horseId).delete()
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void getHorsesByOwner(String ownerId, Callback<List<Horse>> callback) {
        db.collection(COLLECTION).whereEqualTo("ownerId", ownerId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Horse> horses = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        Horse h = doc.toObject(Horse.class);
                        if (h != null) horses.add(h);
                    }
                    if (callback != null) callback.onSuccess(horses);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void uploadHorsePhoto(Uri imageUri, String horseId, Callback<String> callback) {
        StorageReference ref = storage.getReference().child("horses/" + horseId + ".jpg");
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
