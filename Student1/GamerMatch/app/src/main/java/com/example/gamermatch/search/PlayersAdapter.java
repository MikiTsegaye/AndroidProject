package com.example.gamermatch.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamermatch.FireBaseHelper;
import com.example.gamermatch.R;
import com.example.gamermatch.User;

import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>
{
    private List<User> m_PlayerList;
    private FireBaseHelper m_FirebaseHelper;
    public PlayersAdapter(List<User> i_PlayerList)
    {
        this.m_PlayerList = i_PlayerList;
        this.m_FirebaseHelper = new FireBaseHelper();
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup i_Parent, int i_ViewType)
    {
        View v_View = LayoutInflater.from(i_Parent.getContext()).inflate(R.layout.item_player_card, i_Parent, false);
        return new PlayerViewHolder(v_View);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder i_Holder, int i_Position)
    {
        User v_User = m_PlayerList.get(i_Position);

        if (v_User != null)
        {
            i_Holder.m_TvName.setText(v_User.getName()); //

            if (v_User.getFavoriteGames() != null && !v_User.getFavoriteGames().isEmpty())
            {
                i_Holder.m_TvGames.setText("משחקים: " + String.join(", ", v_User.getFavoriteGames())); //
            }
            else
            {
                i_Holder.m_TvGames.setText("אין משחקים מועדפים עדין");
            }


            i_Holder.m_BtnAddFriend.setOnClickListener(v -> {
                String v_TargetUserId = v_User.getUserId();
                m_FirebaseHelper.AddFriend(v_TargetUserId); //
                Toast.makeText(v.getContext(), "הוספת את " + v_User.getName() + " לחברים! 🎮", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return m_PlayerList.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView m_TvName;
        private TextView m_TvGames;
        private Button m_BtnAddFriend;

        public PlayerViewHolder(@NonNull View i_ItemView)
        {
            super(i_ItemView);
            m_TvName = i_ItemView.findViewById(R.id.tvSearchPlayerName);
            m_TvGames = i_ItemView.findViewById(R.id.tvSearchPlayerGames);
            m_BtnAddFriend = i_ItemView.findViewById(R.id.btnAddFriend);
        }
    }
}