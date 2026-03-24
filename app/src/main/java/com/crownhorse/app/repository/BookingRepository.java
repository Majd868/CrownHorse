package com.crownhorse.app.repository;

import com.crownhorse.app.models.Booking;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    private static final String COLLECTION = "bookings";
    private final FirebaseFirestore db;

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public BookingRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void createBooking(Booking booking, Callback<String> callback) {
        String id = db.collection(COLLECTION).document().getId();
        booking.setBookingId(id);
        db.collection(COLLECTION).document(id).set(booking)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(id);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void getBookingsByOwner(String ownerId, Callback<List<Booking>> callback) {
        db.collection(COLLECTION).whereEqualTo("ownerId", ownerId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Booking> bookings = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        Booking b = doc.toObject(Booking.class);
                        if (b != null) bookings.add(b);
                    }
                    if (callback != null) callback.onSuccess(bookings);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void getBookingsByProvider(String providerId, Callback<List<Booking>> callback) {
        db.collection(COLLECTION).whereEqualTo("providerId", providerId).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Booking> bookings = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        Booking b = doc.toObject(Booking.class);
                        if (b != null) bookings.add(b);
                    }
                    if (callback != null) callback.onSuccess(bookings);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

    public void updateBookingStatus(String bookingId, String status, Callback<Void> callback) {
        db.collection(COLLECTION).document(bookingId).update("status", status)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }
}
