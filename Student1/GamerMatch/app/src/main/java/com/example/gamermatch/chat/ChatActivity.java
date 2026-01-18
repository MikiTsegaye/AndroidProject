package com.example.gamermatch.chat;

import android.os.Bundle;
import android.view.View;
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
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSend;

    private FirebaseFirestore db;
    private ListenerRegistration listener;

    private String chatId;
    private String otherUid;
    private String currentUid;

    private MessageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Init Firebase
        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();

        // Get intent extras
        chatId = getIntent().getStringExtra("chatId");
        otherUid = getIntent().getStringExtra("otherUid");

        // Validate
        if (currentUid == null || chatId == null || otherUid == null) {
            finish();
            return;
        }

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String otherName = getIntent().getStringExtra("otherName");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // זמני עד שמביאים שם אמיתי
            if (otherName != null) {
                getSupportActionBar().setTitle(otherName);
            }
            else{
                getSupportActionBar().setTitle(otherUid);
            }


        }
        toolbar.setNavigationOnClickListener(v -> finish());


        // Fetch display name (optional)
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

        // Views
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // RecyclerView
        adapter = new MessageAdapter(currentUid);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true); // להרגיש כמו וואטסאפ
        rvMessages.setLayoutManager(lm);
        rvMessages.setAdapter(adapter);

        // Listen
        listenToMessages();

        // Send button
        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (text.isEmpty()) return;
            sendMessage(text);
            etMessage.setText("");
        });
    }

    //old function before check
//    private void listenToMessages() {
//        listener = db.collection("chats").document(chatId)
//                .collection("messages")
//                .orderBy("timestamp", Query.Direction.ASCENDING)
//                .addSnapshotListener((snap, e) -> {
//                    if (e != null || snap == null) return;
//                    List<Message> list = snap.toObjects(Message.class);
//                    adapter.setMessages(list);
//                    if (list != null && !list.isEmpty()) {
//                        rvMessages.scrollToPosition(list.size() - 1);
//                    }
//                });
//    }

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

                    // Scroll once at the end (only if the user was at the bottom)
                    if (anyInserted && wasAtBottom) {
                        rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                    }
                });
    }

    private boolean isAtBottom() {
        RecyclerView.LayoutManager lm = rvMessages.getLayoutManager();
        if (!(lm instanceof LinearLayoutManager)) return true;

        LinearLayoutManager llm = (LinearLayoutManager) lm;
        int lastVisible = llm.findLastVisibleItemPosition(); // לא completely
        int total = adapter.getItemCount();

        if (total == 0) return true;

        // If I am within 1-2 messages of the end, I am considered "at the bottom"
        return lastVisible >= total - 2;
    }

    private void sendMessage(String text) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("senderId", currentUid);
        msg.put("text", text);
        msg.put("timestamp", FieldValue.serverTimestamp());

        // messages
        db.collection("chats").document(chatId)
                .collection("messages")
                .add(msg);

        // Update conversation summary
        Map<String, Object> chatUpdate = new HashMap<>();
        chatUpdate.put("participants", java.util.Arrays.asList(currentUid, otherUid));
        chatUpdate.put("lastMessage", text);
        chatUpdate.put("lastSenderId", currentUid);
        chatUpdate.put("lastTimestamp", FieldValue.serverTimestamp());

        db.collection("chats").document(chatId)
                .set(chatUpdate, com.google.firebase.firestore.SetOptions.merge());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) listener.remove();
    }
}
