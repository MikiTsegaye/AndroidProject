package com.example.gamermatch.search;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamermatch.R;
import com.example.gamermatch.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyFriendsActivity extends AppCompatActivity {

    private RecyclerView rvFriends;
    private PlayersAdapter adapter;
    private final List<User> friendsList = new ArrayList<>();
    private TextView tvNoFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);

        rvFriends = findViewById(R.id.rvMyFriends);
        tvNoFriends = findViewById(R.id.tvNoFriends);

        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlayersAdapter(friendsList);
        rvFriends.setAdapter(adapter);

        loadFriendsData();
    }

    private void loadFriendsData() {
        String myUid = FirebaseAuth.getInstance().getUid();
        if (myUid == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(myUid)
                .get()
                .addOnSuccessListener(document -> {
                    List<String> friendsIds = (List<String>) document.get("friends");

                    if (friendsIds == null || friendsIds.isEmpty()) {
                        friendsList.clear();
                        adapter.notifyDataSetChanged();
                        tvNoFriends.setVisibility(View.VISIBLE);
                        return;
                    }

                    // ⚠️ whereIn מוגבל ל-10 IDs. אם תגיעי ליותר, צריך לפצל (אגיד לך איך אם צריך).
                    db.collection("users")
                            .whereIn(FieldPath.documentId(), friendsIds)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                friendsList.clear();

                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    User u = doc.toObject(User.class);
                                    if (u != null) {
                                        friendsList.add(u);
                                    }
                                }

                                adapter.notifyDataSetChanged();
                                tvNoFriends.setVisibility(friendsList.isEmpty() ? View.VISIBLE : View.GONE);
                            })
                            .addOnFailureListener(e -> {
                                tvNoFriends.setVisibility(View.VISIBLE);
                            });
                })
                .addOnFailureListener(e -> {
                    tvNoFriends.setVisibility(View.VISIBLE);
                });
    }
}
