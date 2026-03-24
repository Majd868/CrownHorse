package com.crownhorse.app.bookings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crownhorse.app.R;
import com.crownhorse.app.models.Booking;
import com.crownhorse.app.repository.BookingRepository;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BookingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<Booking> bookings = new ArrayList<>();
    private TextView tvEmpty;
    private View progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookingAdapter(bookings, booking -> {
            Intent intent = new Intent(getContext(), BookingDetailActivity.class);
            intent.putExtra("bookingId", booking.getBookingId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadBookings();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookings();
    }

    private void loadBookings() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        SharedPreferences prefs = requireActivity().getSharedPreferences("crownhorse_prefs", MODE_PRIVATE);
        String role = prefs.getString("userRole", "owner");

        progressBar.setVisibility(View.VISIBLE);
        BookingRepository.Callback<List<Booking>> cb = new BookingRepository.Callback<>() {
            @Override
            public void onSuccess(List<Booking> result) {
                progressBar.setVisibility(View.GONE);
                bookings.clear();
                if (result != null) bookings.addAll(result);
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(bookings.isEmpty() ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(bookings.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
                if (getView() != null)
                    Snackbar.make(getView(), e.getMessage() != null ? e.getMessage() : "Error",
                            Snackbar.LENGTH_SHORT).show();
            }
        };

        BookingRepository repo = new BookingRepository();
        if ("provider".equals(role)) {
            repo.getBookingsByProvider(uid, cb);
        } else {
            repo.getBookingsByOwner(uid, cb);
        }
    }
}
