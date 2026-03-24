package com.crownhorse.app.horses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crownhorse.app.R;
import com.crownhorse.app.models.Horse;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HorseAdapter extends RecyclerView.Adapter<HorseAdapter.ViewHolder> {

    public interface OnHorseClickListener { void onClick(Horse horse); }
    public interface OnHorseLongClickListener { void onLongClick(Horse horse); }

    private final List<Horse> horses;
    private final OnHorseClickListener clickListener;
    private final OnHorseLongClickListener longClickListener;

    public HorseAdapter(List<Horse> horses, OnHorseClickListener click,
                        OnHorseLongClickListener longClick) {
        this.horses = horses;
        this.clickListener = click;
        this.longClickListener = longClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_horse, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Horse horse = horses.get(position);
        holder.tvName.setText(horse.getName());
        holder.tvType.setText(horse.getType() != null ? horse.getType() : "");
        holder.tvAge.setText(holder.itemView.getContext()
                .getString(R.string.years_old, horse.getAge()));

        if (horse.getPhotoUrl() != null && !horse.getPhotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(horse.getPhotoUrl())
                    .placeholder(R.drawable.ic_horse_placeholder)
                    .into(holder.ivPhoto);
        } else {
            holder.ivPhoto.setImageResource(R.drawable.ic_horse_placeholder);
        }

        holder.itemView.setOnClickListener(v -> clickListener.onClick(horse));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onLongClick(horse);
            return true;
        });
    }

    @Override
    public int getItemCount() { return horses.size(); }

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
