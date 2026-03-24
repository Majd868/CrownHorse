package com.crownhorse.app.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crownhorse.app.R;
import com.crownhorse.app.models.Conversation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    public interface OnConversationClickListener { void onClick(Conversation conv); }

    private final List<Conversation> conversations;
    private final String currentUid;
    private final OnConversationClickListener listener;

    public ConversationAdapter(List<Conversation> conversations, String currentUid,
                               OnConversationClickListener listener) {
        this.conversations = conversations;
        this.currentUid = currentUid;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation conv = conversations.get(position);
        holder.tvLastMessage.setText(conv.getLastMessage() != null ? conv.getLastMessage() : "");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.tvTime.setText(conv.getLastMessageAt() > 0
                ? sdf.format(new Date(conv.getLastMessageAt())) : "");

        // Unread count badge
        int unread = 0;
        if (conv.getUnreadCounts() != null && currentUid != null
                && conv.getUnreadCounts().containsKey(currentUid)) {
            Integer count = conv.getUnreadCounts().get(currentUid);
            if (count != null) unread = count;
        }
        holder.tvUnread.setText(unread > 0 ? String.valueOf(unread) : "");
        holder.tvUnread.setVisibility(unread > 0 ? View.VISIBLE : View.GONE);

        // Show other member's name placeholder - will resolve in adapter
        if (conv.getMemberIds() != null) {
            String otherId = "";
            for (String id : conv.getMemberIds()) {
                if (!id.equals(currentUid)) { otherId = id; break; }
            }
            holder.tvName.setText(otherId); // Will be resolved by caller if needed
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(conv));
    }

    @Override
    public int getItemCount() { return conversations.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMessage, tvTime, tvUnread;

        ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvLastMessage = view.findViewById(R.id.tvLastMessage);
            tvTime = view.findViewById(R.id.tvTime);
            tvUnread = view.findViewById(R.id.tvUnread);
        }
    }
}
