package com.example.gamermatch.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamermatch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxActivity extends AppCompatActivity {

    private RecyclerView rvChats;
    private TextView tvEmpty;

    private InboxAdapter adapter;
    private ListenerRegistration listener;

    private FirebaseFirestore db;
    private String currentUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        rvChats = findViewById(R.id.rvChats);
        tvEmpty = findViewById(R.id.tvEmpty);

        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();

        findViewById(R.id.btnNewChat).setOnClickListener(v -> {
            showCreateChatDialog();
        });

        if (currentUid == null) {
            tvEmpty.setText("Not logged in");
            tvEmpty.setVisibility(View.VISIBLE);
            rvChats.setVisibility(View.GONE);
            return;
        }

        adapter = new InboxAdapter(currentUid, (chatId, otherUid) -> {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("chatId", chatId);
            i.putExtra("otherUid", otherUid);
            startActivity(i);
        });

        rvChats.setLayoutManager(new LinearLayoutManager(this));
        rvChats.setAdapter(adapter);

        listenToChats();
    }

    private void showCreateChatDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter other user UID");

        new AlertDialog.Builder(this)
                .setTitle("Create new chat")
                .setView(input)
                .setPositiveButton("Create", (d, which) -> {
                    String otherUid = input.getText().toString().trim();
                    if (otherUid.isEmpty() || otherUid.equals(currentUid)) return;

                    createChatAndOpen(otherUid);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void createChatAndOpen(String otherUid) {
        String chatId = currentUid.compareTo(otherUid) < 0
                ? currentUid + "_" + otherUid
                : otherUid + "_" + currentUid;

        Map<String, Object> chat = new HashMap<>();
        chat.put("participants", Arrays.asList(currentUid, otherUid));
        chat.put("lastMessage", "");
        chat.put("lastTimestamp", FieldValue.serverTimestamp());

        db.collection("chats").document(chatId).set(chat)
                .addOnSuccessListener(v -> {
                    Intent i = new Intent(this, ChatActivity.class);
                    i.putExtra("chatId", chatId);
                    i.putExtra("otherUid", otherUid);
                    startActivity(i);
                });
    }

    private void listenToChats() {
        listener = db.collection("chats")
                .whereArrayContains("participants", currentUid)
                .orderBy("lastTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) return;

                    List<Chat> chats = snapshot.toObjects(Chat.class);
                    adapter.setChats(chats);

                    boolean empty = (chats == null || chats.isEmpty());
                    tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                    rvChats.setVisibility(empty ? View.GONE : View.VISIBLE);
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) listener.remove();
    }
}
