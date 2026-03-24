package com.crownhorse.app.chat;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crownhorse.app.R;
import com.crownhorse.app.models.Message;
import com.crownhorse.app.repository.ChatRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private EditText etMessage;
    private ImageButton btnSend, btnImage;
    private View progressBar;
    private String conversationId;
    private String currentUid;
    private ListenerRegistration listenerReg;
    private final ChatRepository chatRepository = new ChatRepository();

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) sendImageMessage(uri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        conversationId = getIntent().getStringExtra("conversationId");
        currentUid = FirebaseAuth.getInstance().getUid();
        if (conversationId == null || currentUid == null) { finish(); return; }

        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnImage = findViewById(R.id.btnImage);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messages, currentUid);
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendTextMessage());
        btnImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        loadOtherUserName();
        listenMessages();
        chatRepository.markMessagesSeen(conversationId, currentUid);
    }

    private void loadOtherUserName() {
        FirebaseFirestore.getInstance().collection("conversations")
                .document(conversationId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        List<String> members = (List<String>) doc.get("memberIds");
                        if (members != null) {
                            String otherId = null;
                            for (String id : members) {
                                if (!id.equals(currentUid)) { otherId = id; break; }
                            }
                            if (otherId != null) {
                                String finalOtherId = otherId;
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(otherId).get()
                                        .addOnSuccessListener(userDoc -> {
                                            String name = userDoc.getString("name");
                                            if (getSupportActionBar() != null)
                                                getSupportActionBar().setTitle(name != null ? name : "Chat");
                                        });
                            }
                        }
                    }
                });
    }

    private void listenMessages() {
        listenerReg = chatRepository.getMessages(conversationId, new ChatRepository.Callback<>() {
            @Override
            public void onSuccess(List<Message> result) {
                messages.clear();
                if (result != null) messages.addAll(result);
                adapter.notifyDataSetChanged();
                if (!messages.isEmpty())
                    recyclerView.scrollToPosition(messages.size() - 1);
                chatRepository.markMessagesSeen(conversationId, currentUid);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendTextMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        Message msg = new Message();
        msg.setSenderId(currentUid);
        msg.setType("text");
        msg.setText(text);
        msg.setSentAt(System.currentTimeMillis());
        msg.setSeenBy(new ArrayList<>(Collections.singletonList(currentUid)));

        etMessage.setText("");
        chatRepository.sendMessage(conversationId, msg, new ChatRepository.Callback<>() {
            @Override public void onSuccess(Void r) {}
            @Override public void onFailure(Exception e) {
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendImageMessage(Uri uri) {
        chatRepository.uploadChatImage(uri, conversationId, new ChatRepository.Callback<>() {
            @Override
            public void onSuccess(String url) {
                Message msg = new Message();
                msg.setSenderId(currentUid);
                msg.setType("image");
                msg.setImageUrl(url);
                msg.setSentAt(System.currentTimeMillis());
                msg.setSeenBy(new ArrayList<>(Collections.singletonList(currentUid)));
                chatRepository.sendMessage(conversationId, msg, new ChatRepository.Callback<>() {
                    @Override public void onSuccess(Void r) {}
                    @Override public void onFailure(Exception e) {
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerReg != null) listenerReg.remove();
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
