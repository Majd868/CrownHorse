package com.crownhorse.app.bookings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.crownhorse.app.R;
import com.crownhorse.app.models.Booking;
import com.crownhorse.app.repository.BookingRepository;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingDetailActivity extends AppCompatActivity {

    private TextView tvStatus, tvDateTime, tvServiceId, tvHorseId;
    private Button btnConfirm, btnReject;
    private View progressBar;
    private Booking booking;
    private final BookingRepository repository = new BookingRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvStatus = findViewById(R.id.tvStatus);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvServiceId = findViewById(R.id.tvServiceId);
        tvHorseId = findViewById(R.id.tvHorseId);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnReject = findViewById(R.id.btnReject);
        progressBar = findViewById(R.id.progressBar);

        String bookingId = getIntent().getStringExtra("bookingId");
        if (bookingId == null) { finish(); return; }

        loadBooking(bookingId);
    }

    private void loadBooking(String bookingId) {
        progressBar.setVisibility(View.VISIBLE);
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("bookings").document(bookingId).get()
                .addOnSuccessListener(doc -> {
                    progressBar.setVisibility(View.GONE);
                    booking = doc.toObject(Booking.class);
                    if (booking == null) { finish(); return; }
                    populateView();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(findViewById(android.R.id.content),
                            e.getMessage() != null ? e.getMessage() : "Error",
                            Snackbar.LENGTH_SHORT).show();
                });
    }

    private void populateView() {
        tvStatus.setText(booking.getStatus());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        tvDateTime.setText(sdf.format(new Date(booking.getDatetime())));
        tvServiceId.setText(booking.getServiceId());
        tvHorseId.setText(booking.getHorseId());

        SharedPreferences prefs = getSharedPreferences("crownhorse_prefs", MODE_PRIVATE);
        String role = prefs.getString("userRole", "owner");
        boolean isProvider = "provider".equals(role);
        boolean isPending = "pending".equals(booking.getStatus());

        btnConfirm.setVisibility(isProvider && isPending ? View.VISIBLE : View.GONE);
        btnReject.setVisibility(isProvider && isPending ? View.VISIBLE : View.GONE);

        btnConfirm.setOnClickListener(v -> updateStatus("confirmed"));
        btnReject.setOnClickListener(v -> updateStatus("rejected"));
    }

    private void updateStatus(String status) {
        progressBar.setVisibility(View.VISIBLE);
        repository.updateBookingStatus(booking.getBookingId(), status, new BookingRepository.Callback<>() {
            @Override
            public void onSuccess(Void result) {
                progressBar.setVisibility(View.GONE);
                booking.setStatus(status);
                tvStatus.setText(status);
                btnConfirm.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(tvStatus, e.getMessage() != null ? e.getMessage() : "Error",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
