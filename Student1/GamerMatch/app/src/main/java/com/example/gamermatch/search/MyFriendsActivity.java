package com.example.gamermatch.search;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamermatch.R;
import com.example.gamermatch.User;
import com.example.gamermatch.search.PlayersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MyFriendsActivity extends AppCompatActivity {
    private RecyclerView rvFriends;
    private PlayersAdapter adapter;
    private List<User> friendsList = new ArrayList<>();
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

        FirebaseFirestore.getInstance().collection("users").document(myUid)
                .get().addOnSuccessListener(document -> {
                    List<String> friendsIds = (List<String>) document.get("friends");
                    if (friendsIds != null && !friendsIds.isEmpty()) {
                        // משיכת פרטי המשתמשים עבור כל ה-IDs ברשימה
                        FirebaseFirestore.getInstance().collection("users")
                                .whereIn("userId", friendsIds)
                                .get().addOnSuccessListener(querySnapshot -> {
                                    friendsList.clear();
                                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                        friendsList.add(doc.toObject(User.class));
                                    }
                                    adapter.notifyDataSetChanged();
                                    tvNoFriends.setVisibility(View.GONE);
                                });
                    } else {
                        tvNoFriends.setVisibility(View.VISIBLE);
                    }
                });
    }
}