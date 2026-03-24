package com.crownhorse.app.bookings;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crownhorse.app.R;
import com.crownhorse.app.models.Booking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    public interface OnBookingClickListener { void onClick(Booking booking); }

    private final List<Booking> bookings;
    private final OnBookingClickListener listener;

    public BookingAdapter(List<Booking> bookings, OnBookingClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.tvServiceId.setText(booking.getServiceId());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvDateTime.setText(sdf.format(new Date(booking.getDatetime())));
        holder.tvStatus.setText(booking.getStatus());

        int color;
        switch (booking.getStatus() != null ? booking.getStatus() : "") {
            case "confirmed": color = Color.parseColor("#4CAF50"); break;
            case "rejected": color = Color.parseColor("#F44336"); break;
            case "completed": color = Color.parseColor("#2196F3"); break;
            default: color = Color.parseColor("#FF9800"); break; // pending
        }
        holder.tvStatus.setTextColor(color);
        holder.itemView.setOnClickListener(v -> listener.onClick(booking));
    }

    @Override
    public int getItemCount() { return bookings.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceId, tvDateTime, tvStatus;

        ViewHolder(View view) {
            super(view);
            tvServiceId = view.findViewById(R.id.tvServiceId);
            tvDateTime = view.findViewById(R.id.tvDateTime);
            tvStatus = view.findViewById(R.id.tvStatus);
        }
    }
}
