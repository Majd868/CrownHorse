package com.crownhorse.app.trainers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crownhorse.app.R;
import com.crownhorse.app.models.Trainer;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.ViewHolder> {

    public interface OnTrainerClickListener { void onClick(Trainer trainer); }

    private final List<Trainer> trainers;
    private final OnTrainerClickListener listener;

    public TrainerAdapter(List<Trainer> trainers, OnTrainerClickListener listener) {
        this.trainers = trainers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_horse, parent, false); // reuse item_horse layout
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trainer trainer = trainers.get(position);
        holder.tvName.setText(trainer.getName());
        holder.tvType.setText(trainer.getSpecialization() != null ? trainer.getSpecialization() : "");
        holder.tvAge.setText(holder.itemView.getContext()
                .getString(R.string.years_experience, trainer.getYearsOfExperience()));

        if (trainer.getPhotoUrl() != null && !trainer.getPhotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(trainer.getPhotoUrl())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(holder.ivPhoto);
        } else {
            holder.ivPhoto.setImageResource(R.drawable.ic_profile_placeholder);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(trainer));
    }

    @Override
    public int getItemCount() { return trainers.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivPhoto;
        TextView tvName, tvType, tvAge;

        ViewHolder(View view) {
            super(view);
            ivPhoto = view.findViewById(R.id.ivPhoto);
            tvName = view.findViewById(R.id.tvName);
            tvType = view.findViewById(R.id.tvType);
            tvAge = view.findViewById(R.id.tvAge);
        }
    }
}
