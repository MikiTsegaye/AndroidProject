package com.example.gamermatch;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private EditText m_EtName;
    private RecyclerView m_RvGames;
    private TextView m_TvNoGames;
    private GamesAdapter m_GamesAdapter;
    private FirebaseFirestore m_Db;
    private String m_Uid;

    @Override
    protected void onCreate(Bundle i_SavedInstanceState) {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        m_Db = FirebaseFirestore.getInstance();
        m_Uid = FirebaseAuth.getInstance().getUid();

        m_EtName = findViewById(R.id.etEditName);
        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> updateName());
        findViewById(R.id.btnBackToMain).setOnClickListener(v -> finish());

        setupGamesRecycler();
        loadAllGamesAndSelected();
    }

    private void setupGamesRecycler() {
        m_RvGames = findViewById(R.id.rvGames);
        m_TvNoGames = findViewById(R.id.tvNoGames);
        m_RvGames.setLayoutManager(new LinearLayoutManager(this));

        m_GamesAdapter = new GamesAdapter((gameName, checked) -> {
            if (m_Uid == null) return;

            // Perform the update and add a listener for feedback
            var docRef = m_Db.collection("users").document(m_Uid);
            var task = checked ?
                    docRef.update("favoriteGames", FieldValue.arrayUnion(gameName)) :
                    docRef.update("favoriteGames", FieldValue.arrayRemove(gameName));

            task.addOnFailureListener(e ->
                    Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        m_RvGames.setAdapter(m_GamesAdapter);
    }

    private void loadAllGamesAndSelected() {
        if (m_Uid == null) return;

        // 1) Load all available games
        m_Db.collection("games").orderBy("name").get().addOnSuccessListener(snap -> {
            List<String> allGames = new ArrayList<>();
            for (var doc : snap.getDocuments()) {
                String name = doc.getString("name");
                if (name != null) allGames.add(name);
            }
            m_GamesAdapter.setAllGames(allGames);
            m_TvNoGames.setVisibility(allGames.isEmpty() ? View.VISIBLE : View.GONE);

            // 2) Load user's selected favorites
            m_Db.collection("users").document(m_Uid).get().addOnSuccessListener(userDoc -> {
                if (userDoc.exists()) {
                    List<String> fav = (List<String>) userDoc.get("favoriteGames");
                    if (fav != null) m_GamesAdapter.setSelectedGames(fav);
                }
            });
        });
    }

    private void updateName() {
        String newName = m_EtName.getText().toString().trim();
        if (!newName.isEmpty()) {
            m_Db.collection("users").document(m_Uid).update("name", newName)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}