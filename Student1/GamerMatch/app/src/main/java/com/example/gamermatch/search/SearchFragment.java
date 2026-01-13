package com.example.gamermatch.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamermatch.FireBaseHelper;
import com.example.gamermatch.R;
import com.example.gamermatch.User;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment
{
    private FireBaseHelper m_FirebaseHelper;
    private PlayersAdapter m_Adapter;
    private List<User> m_ResultsList;
    private RecyclerView m_RvResults;
    private SearchView m_SvSearch;
    private TextView m_TvNoResults;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater i_Inflater, @Nullable ViewGroup i_Container, @Nullable Bundle i_SavedInstanceState)
    {
        return i_Inflater.inflate(R.layout.fragment_search, i_Container, false);
    }

    @Override
    public void onViewCreated(@NonNull View i_View, @Nullable Bundle i_SavedInstanceState)
    {
        super.onViewCreated(i_View, i_SavedInstanceState);

        m_FirebaseHelper = new FireBaseHelper();
        m_ResultsList = new ArrayList<>();
        m_RvResults = i_View.findViewById(R.id.rvPlayersResults);
        m_SvSearch = i_View.findViewById(R.id.svPlayerSearch);
        m_TvNoResults = i_View.findViewById(R.id.tvNoResults);

        m_Adapter = new PlayersAdapter(m_ResultsList);
        m_RvResults.setLayoutManager(new LinearLayoutManager(getContext()));
        m_RvResults.setAdapter(m_Adapter);

        m_SvSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String i_Query)
            {

                if (i_Query != null && !i_Query.trim().isEmpty())
                {
                    // .trim() מסיר רווחים מיותרים מההתחלה והסוף
                    performSearch(i_Query.trim());
                }
                else
                {

                    Toast.makeText(getContext(), "נא להזין שם משחק לחיפוש", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String i_NewText)
            {
                return false;
            }
        });
    }

    private void performSearch(String i_GameName)
    {
        String v_CurrentUserId = m_FirebaseHelper.GetCurrentUserId();

        m_FirebaseHelper.SearchPlayersByGame(i_GameName).get()
                .addOnCompleteListener(i_Task -> {
                    if (i_Task.isSuccessful() && i_Task.getResult() != null)
                    {
                        m_ResultsList.clear();
                        for (DocumentSnapshot v_Doc : i_Task.getResult())
                        {
                            User v_User = v_Doc.toObject(User.class);
                            if (v_User != null)
                            {
                                if (!v_User.getUserId().equals(v_CurrentUserId))
                                {
                                    m_ResultsList.add(v_User);
                                }
                            }
                        }


                        if (m_ResultsList.isEmpty())
                        {
                            m_TvNoResults.setVisibility(View.VISIBLE);
                            m_RvResults.setVisibility(View.GONE);
                        }
                        else
                        {
                            m_TvNoResults.setVisibility(View.GONE);
                            m_RvResults.setVisibility(View.VISIBLE);
                            m_Adapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "שגיאה בחיפוש הנתונים", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}