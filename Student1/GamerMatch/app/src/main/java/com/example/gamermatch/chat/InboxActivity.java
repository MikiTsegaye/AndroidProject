package com.example.gamermatch.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
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

        // Toolbar + Back arrow
        Toolbar toolbar = findViewById(R.id.toolbarInbox);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chats");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Views
        rvChats = findViewById(R.id.rvChats);
        tvEmpty = findViewById(R.id.tvEmpty);

        // Firebase
        db = FirebaseFirestore.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();

        if (currentUid == null) {
            tvEmpty.setText("Not logged in");
            tvEmpty.setVisibility(View.VISIBLE);
            rvChats.setVisibility(View.GONE);
            return;
        }

        // Adapter
        adapter = new InboxAdapter(currentUid, (chatId, isGroup, title, otherUid) -> {
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("chatId", chatId);
            i.putExtra("isGroup", isGroup);
            if (isGroup) {
                i.putExtra("chatTitle", title);
            } else {
                i.putExtra("otherUid", otherUid);
            }
            startActivity(i);
        });


        rvChats.setLayoutManager(new LinearLayoutManager(this));
        rvChats.setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvChats.addItemDecoration(divider);

        listenToChats();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_new_chat) {
            showJoinGameGroupDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showJoinGameGroupDialog() {
        db.collection("users").document(currentUid).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    @SuppressWarnings("unchecked")
                    List<String> games = (List<String>) doc.get("favoriteGames");

                    if (games == null || games.isEmpty()) {
                        new AlertDialog.Builder(this)
                                .setTitle("אין משחקים")
                                .setMessage("קודם תבחרי משחקים בפרופיל כדי להצטרף לקבוצה.")
                                .setPositiveButton("סבבה", null)
                                .show();
                        return;
                    }

                    String[] arr = games.toArray(new String[0]);

                    new AlertDialog.Builder(this)
                            .setTitle("הצטרפות לקבוצת משחק")
                            .setItems(arr, (d, which) -> joinGameGroupAndOpen(arr[which]))
                            .setNegativeButton("ביטול", null)
                            .show();
                });
    }

    private void joinGameGroupAndOpen(String gameName) {
        String key = normalizeGameKey(gameName); // fifa / csgo
        String chatId = "game_" + key;

        Map<String, Object> init = new HashMap<>();
        init.put("type", "game_group");
        init.put("gameKey", key);
        init.put("gameName", gameName);
        init.put("lastMessage", "");
        init.put("lastTimestamp", FieldValue.serverTimestamp());

        db.collection("chats").document(chatId)
                .set(init, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(v -> db.collection("chats").document(chatId)
                        .update("participants", FieldValue.arrayUnion(currentUid))
                        .addOnSuccessListener(v2 -> openGroupChat(chatId, gameName)));
    }

    private void openGroupChat(String chatId, String gameName) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("chatId", chatId);
        i.putExtra("isGroup", true);
        i.putExtra("chatTitle", gameName);
        startActivity(i);
    }

    private String normalizeGameKey(String gameName) {
        return gameName.toLowerCase().replaceAll("[^a-z0-9]", "");
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