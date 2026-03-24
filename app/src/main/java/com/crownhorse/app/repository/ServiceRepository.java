package com.crownhorse.app.repository;

import com.crownhorse.app.models.Service;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ServiceRepository {
    private static final String COLLECTION = "services";
    private final FirebaseFirestore db;

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public ServiceRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void addService(Service service, Callback<String> callback) {
        String id = db.collection(COLLECTION).document().getId();
        service.setServiceId(id);
        db.collection(COLLECTION).document(id).set(service)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(id);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void updateService(Service service, Callback<Void> callback) {
        db.collection(COLLECTION).document(service.getServiceId()).set(service)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void deleteService(String serviceId, Callback<Void> callback) {
        db.collection(COLLECTION).document(serviceId).delete()
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void getAllServices(Callback<List<Service>> callback) {
        db.collection(COLLECTION).orderBy("createdAt").get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Service> services = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        Service s = doc.toObject(Service.class);
                        if (s != null) services.add(s);
                    }
                    if (callback != null) callback.onSuccess(services);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void getServicesByProvider(String providerId, Callback<List<Service>> callback) {
        db.collection(COLLECTION).whereEqualTo("providerId", providerId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Service> services = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        Service s = doc.toObject(Service.class);
                        if (s != null) services.add(s);
                    }
                    if (callback != null) callback.onSuccess(services);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }
}
