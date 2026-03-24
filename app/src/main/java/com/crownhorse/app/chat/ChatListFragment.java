package com.crownhorse.app.chat;

import android.content.Intent;
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
import com.crownhorse.app.models.Conversation;
import com.crownhorse.app.repository.ChatRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    private List<Conversation> conversations = new ArrayList<>();
    private TextView tvEmpty;
    private View progressBar;
    private ListenerRegistration listenerReg;
    private final ChatRepository chatRepository = new ChatRepository();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ConversationAdapter(conversations,
                FirebaseAuth.getInstance().getUid(),
                conv -> {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("conversationId", conv.getConversationId());
                    startActivity(intent);
                });
        recyclerView.setAdapter(adapter);

        listenConversations();
    }

    private void listenConversations() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        progressBar.setVisibility(View.VISIBLE);
        listenerReg = chatRepository.getConversations(uid, new ChatRepository.Callback<>() {
            @Override
            public void onSuccess(List<Conversation> result) {
                progressBar.setVisibility(View.GONE);
                conversations.clear();
                if (result != null) conversations.addAll(result);
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(conversations.isEmpty() ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(conversations.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onFailure(Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerReg != null) listenerReg.remove();
    }
}
