package com.example.gamermatch.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamermatch.FireBaseHelper;
import com.example.gamermatch.R;
import com.example.gamermatch.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private FireBaseHelper m_FirebaseHelper;
    private PlayersAdapter m_Adapter;
    private final List<User> m_ResultsList = new ArrayList<>();
    private RecyclerView m_RvResults;
    private TextView m_TvNoResults;

    // NEW
    private Spinner m_SpGames;
    private TextView m_TvSelectedGame;

    private final List<String> m_AllGames = new ArrayList<>();
    private ArrayAdapter<String> m_GamesSpinnerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i_Inflater, @Nullable ViewGroup i_Container, @Nullable Bundle i_SavedInstanceState) {
        return i_Inflater.inflate(R.layout.fragment_search, i_Container, false);
    }

    @Override
    public void onViewCreated(@NonNull View i_View, @Nullable Bundle i_SavedInstanceState) {
        super.onViewCreated(i_View, i_SavedInstanceState);

        m_FirebaseHelper = new FireBaseHelper();

        m_RvResults = i_View.findViewById(R.id.rvPlayersResults);
        m_TvNoResults = i_View.findViewById(R.id.tvNoResults);

        m_SpGames = i_View.findViewById(R.id.spGames);
        m_TvSelectedGame = i_View.findViewById(R.id.tvSelectedGame);

        m_Adapter = new PlayersAdapter(m_ResultsList);
        m_RvResults.setLayoutManager(new LinearLayoutManager(getContext()));
        m_RvResults.setAdapter(m_Adapter);

        setupGamesSpinner();
        loadGamesFromFirestore();
    }

    private void setupGamesSpinner() {
        // שורה ראשונה שתכריח לבחור
        m_AllGames.clear();
        m_AllGames.add("בחר משחק...");

        m_GamesSpinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                m_AllGames
        );
        m_GamesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_SpGames.setAdapter(m_GamesSpinnerAdapter);

        m_SpGames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = m_AllGames.get(position);

                if ("בחר משחק...".equals(selected)) {
                    m_TvSelectedGame.setText("");
                    clearResults();
                    return;
                }

                m_TvSelectedGame.setText("נבחר: " + selected);
                performSearch(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadGamesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // אם אין לך nameLower בכולם, תגידי לי ונשנה ל-orderBy("name") או בלי orderBy
        db.collection("games")
                .orderBy("nameLower")
                .get()
                .addOnSuccessListener(snap -> {
                    // נשאיר את "בחרי משחק..." במקום 0
                    m_AllGames.clear();
                    m_AllGames.add("בחר משחק...");

                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        String name = doc.getString("name");
                        if (name != null) m_AllGames.add(name);
                    }
                    m_GamesSpinnerAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "שגיאה בטעינת משחקים", Toast.LENGTH_SHORT).show()
                );
    }

    private void performSearch(String i_GameName) {
        String v_CurrentUserId = m_FirebaseHelper.GetCurrentUserId();

        m_FirebaseHelper.SearchPlayersByGame(i_GameName).get()
                .addOnCompleteListener(i_Task -> {
                    if (i_Task.isSuccessful() && i_Task.getResult() != null) {
                        m_ResultsList.clear();

                        for (DocumentSnapshot v_Doc : i_Task.getResult()) {
                            User v_User = v_Doc.toObject(User.class);
                            if (v_User != null) {
                                // הגנה: אם getUserId() אצלך לא נטען מה-doc id
                                // אפשר גם: v_UserId = v_Doc.getId();
                                String uid = v_User.getUserId();
                                if (uid == null) uid = v_Doc.getId();

                                if (!uid.equals(v_CurrentUserId)) {
                                    m_ResultsList.add(v_User);
                                }
                            }
                        }

                        m_Adapter.notifyDataSetChanged();

                        if (m_ResultsList.isEmpty()) {
                            m_TvNoResults.setVisibility(View.VISIBLE);
                            m_RvResults.setVisibility(View.GONE);
                        } else {
                            m_TvNoResults.setVisibility(View.GONE);
                            m_RvResults.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Toast.makeText(getContext(), "שגיאה בחיפוש הנתונים", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearResults() {
        m_ResultsList.clear();
        m_Adapter.notifyDataSetChanged();
        m_TvNoResults.setVisibility(View.GONE);
        m_RvResults.setVisibility(View.GONE);
    }
}
