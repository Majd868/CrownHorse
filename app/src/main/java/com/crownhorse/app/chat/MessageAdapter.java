package com.crownhorse.app.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crownhorse.app.R;
import com.crownhorse.app.models.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private final List<Message> messages;
    private final String currentUid;

    public MessageAdapter(List<Message> messages, String currentUid) {
        this.messages = messages;
        this.currentUid = currentUid;
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        return currentUid.equals(msg.getSenderId()) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new MessageViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new MessageViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messages.get(position);
        MessageViewHolder vh = (MessageViewHolder) holder;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        vh.tvTime.setText(sdf.format(new Date(msg.getSentAt())));

        if ("image".equals(msg.getType())) {
            vh.tvText.setVisibility(View.GONE);
            vh.ivImage.setVisibility(View.VISIBLE);
            Glide.with(vh.itemView.getContext()).load(msg.getImageUrl()).into(vh.ivImage);
        } else {
            vh.tvText.setVisibility(View.VISIBLE);
            vh.ivImage.setVisibility(View.GONE);
            vh.tvText.setText(msg.getText());
        }
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvText, tvTime;
        ImageView ivImage;

        MessageViewHolder(View view) {
            super(view);
            tvText = view.findViewById(R.id.tvText);
            tvTime = view.findViewById(R.id.tvTime);
            ivImage = view.findViewById(R.id.ivImage);
        }
    }
}
