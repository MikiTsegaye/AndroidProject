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
    private Button m_BtnSave;
    private Button m_BtnBack;

    // Games UI
    private RecyclerView m_RvGames;
    private TextView m_TvNoGames;
    private GamesAdapter m_GamesAdapter;

    private FireBaseHelper m_FirebaseHelper;

    private FirebaseFirestore m_Db;
    private String m_Uid;

    @Override
    protected void onCreate(Bundle i_SavedInstanceState) {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        m_FirebaseHelper = new FireBaseHelper();
        m_Db = FirebaseFirestore.getInstance();
        m_Uid = FirebaseAuth.getInstance().getUid();

        // Name
        m_EtName = findViewById(R.id.etEditName);
        m_BtnSave = findViewById(R.id.btnSaveProfile);
        m_BtnSave.setOnClickListener(v -> updateProfile());

        // Back
        m_BtnBack = findViewById(R.id.btnBackToMain);
        m_BtnBack.setOnClickListener(v -> finish());

        // Games (Checkboxes)
        setupGamesRecycler();
        loadAllGamesAndSelected();
    }

    private void setupGamesRecycler() {
        m_RvGames = findViewById(R.id.rvGames);
        m_TvNoGames = findViewById(R.id.tvNoGames);

        m_RvGames.setLayoutManager(new LinearLayoutManager(this));

        m_GamesAdapter = new GamesAdapter((gameName, checked) -> {
            if (m_Uid == null) return;

            if (checked) {
                m_Db.collection("users").document(m_Uid)
                        .update("favoriteGames", FieldValue.arrayUnion(gameName));
            } else {
                m_Db.collection("users").document(m_Uid)
                        .update("favoriteGames", FieldValue.arrayRemove(gameName));
            }
        });

        m_RvGames.setAdapter(m_GamesAdapter);
    }

    private void loadAllGamesAndSelected() {
        if (m_Uid == null) return;

        // 1) load all games
        m_Db.collection("games")
                .orderBy("nameLower") // אם אין nameLower, תגידי לי ונחליף ל-name
                .get()
                .addOnSuccessListener(snap -> {
                    List<String> allGames = new ArrayList<>();
                    for (var doc : snap.getDocuments()) {
                        String name = doc.getString("name");
                        if (name != null) allGames.add(name);
                    }

                    m_GamesAdapter.setAllGames(allGames);
                    m_TvNoGames.setVisibility(allGames.isEmpty() ? View.VISIBLE : View.GONE);

                    // 2) load selected (favoriteGames)
                    m_Db.collection("users").document(m_Uid)
                            .get()
                            .addOnSuccessListener(userDoc -> {
                                List<String> fav = (List<String>) userDoc.get("favoriteGames");
                                m_GamesAdapter.setSelectedGames(fav);
                            });
                })
                .addOnFailureListener(e -> {
                    m_TvNoGames.setVisibility(View.VISIBLE);
                });
    }

    private void updateProfile() {
        String newName = m_EtName.getText().toString().trim();

        if (!newName.isEmpty()) {
            m_FirebaseHelper.UpdateName(newName);
            Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.enter_valid_name), Toast.LENGTH_SHORT).show();
        }
    }
}
