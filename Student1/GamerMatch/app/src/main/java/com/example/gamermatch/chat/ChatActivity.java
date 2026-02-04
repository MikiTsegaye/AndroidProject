package com.example.gamermatch.chat;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamermatch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;

    private FirebaseFirestore db;
    private ListenerRegistration listener;

    private String chatId;
    private String otherUid;   // רק ב-DM
    private String currentUid;

    private boolean isGroup;
    private String chatTitle;

    private MessageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Init Firebase
        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();

        // Intent extras
        chatId = getIntent().getStringExtra("chatId");
        otherUid = getIntent().getStringExtra("otherUid"); // יכול להיות null בקבוצה
        isGroup = getIntent().getBooleanExtra("isGroup", false);
        chatTitle = getIntent().getStringExtra("chatTitle");

        // Validate
        if (currentUid == null || chatId == null) {
            finish();
            return;
        }
        // ב-DM חייב otherUid
        if (!isGroup && (otherUid == null || otherUid.isEmpty())) {
            finish();
            return;
        }

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (isGroup) {
                getSupportActionBar().setTitle(chatTitle != null ? chatTitle : "Game Group");
            } else {
                String otherName = getIntent().getStringExtra("otherName");
                getSupportActionBar().setTitle(otherName != null ? otherName : otherUid);
            }
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Fetch display name (רק ב-DM)
        if (!isGroup) {
            db.collection("users")
                    .document(otherUid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String name = doc.getString("name");
                            if (name != null && getSupportActionBar() != null) {
                                getSupportActionBar().setTitle(name);
                            }
                        }
                    });
        }

        // Views
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // RecyclerView
        adapter = new MessageAdapter(currentUid);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        rvMessages.setLayoutManager(lm);
        rvMessages.setAdapter(adapter);

        // Listen
        listenToMessages();

        // Send
        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (text.isEmpty()) return;
            sendMessage(text);
            etMessage.setText("");
        });
    }

    private void listenToMessages() {
        adapter.clearAll();

        listener = db.collection("chats").document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;

                    boolean wasAtBottom = isAtBottom();
                    boolean anyInserted = false;

                    for (com.google.firebase.firestore.DocumentChange dc : snap.getDocumentChanges()) {
                        if (dc.getType() == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                            String docId = dc.getDocument().getId();
                            Message m = dc.getDocument().toObject(Message.class);

                            boolean inserted = adapter.addMessageIfNew(docId, m);
                            anyInserted = anyInserted || inserted;
                        }
                    }

                    if (anyInserted && wasAtBottom) {
                        rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                    }
                });
    }

    private boolean isAtBottom() {
        RecyclerView.LayoutManager lm = rvMessages.getLayoutManager();
        if (!(lm instanceof LinearLayoutManager)) return true;

        LinearLayoutManager llm = (LinearLayoutManager) lm;
        int lastVisible = llm.findLastVisibleItemPosition();
        int total = adapter.getItemCount();

        if (total == 0) return true;
        return lastVisible >= total - 2;
    }

    private void sendMessage(String text) {
        // message doc
        Map<String, Object> msg = new HashMap<>();
        msg.put("senderId", currentUid);
        msg.put("text", text);
        msg.put("timestamp", FieldValue.serverTimestamp());

        db.collection("chats").document(chatId)
                .collection("messages")
                .add(msg);

        // conversation summary update
        Map<String, Object> chatUpdate = new HashMap<>();
        chatUpdate.put("lastMessage", text);
        chatUpdate.put("lastSenderId", currentUid);
        chatUpdate.put("lastTimestamp", FieldValue.serverTimestamp());

        // חשוב: ב-DM אנחנו עדיין שומרים participants של 2 אנשים
        // בקבוצה - לא נוגעים ב-participants כדי לא לדרוס!
        if (!isGroup) {
            chatUpdate.put("type", "dm");
            chatUpdate.put("participants", java.util.Arrays.asList(currentUid, otherUid));
        }

        db.collection("chats").document(chatId)
                .set(chatUpdate, com.google.firebase.firestore.SetOptions.merge());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) listener.remove();
    }
}
