package com.example.gamermatch.search;

import android.content.Context;
import android.content.Intent;
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
import com.example.gamermatch.chat.ChatActivity;
import com.example.gamermatch.chat.ChatUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {

        // 1. קודם כל מביאים את המשתמש
        User user = m_PlayerList.get(position);
        Context context = holder.itemView.getContext();

        // (כאן שאר הקוד שלך שמציג את השם, גיל וכו'...)
        // holder.tvName.setText(user.getName());

        // בתוך onBindViewHolder
        holder.m_BtnAddFriend.setOnClickListener(v -> {
            String currentUid = FirebaseAuth.getInstance().getUid();
            String otherUid = user.getUserId();

            if (currentUid == null || otherUid == null) return;

            // פקודת הקסם של פיירבייס: arrayUnion
            // זה מוסיף את החבר לרשימה רק אם הוא לא קיים שם כבר (מונע כפילויות)
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUid)
                    .update("friends", FieldValue.arrayUnion(otherUid))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Added to friends!", Toast.LENGTH_SHORT).show();
                        // אופציונלי: להעלים את הכפתור או לשנות טקסט
                        holder.m_BtnAddFriend.setEnabled(false);
                        holder.m_BtnAddFriend.setText("Saved");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error adding friend", Toast.LENGTH_SHORT).show();
                    });
        });

        // 2. עכשיו הכפתור יעבוד כי הוא מכיר את ה-user
        holder.m_BtnChat.setOnClickListener(v -> {

            // תיקון: משיגים את ה-Context מתוך הכפתור עצמו


            String currentUid = FirebaseAuth.getInstance().getUid();
            String otherUid = user.getUserId(); // עכשיו זה יעבוד כי user מוגדר למעלה

            // הגנה
            if (currentUid == null || otherUid == null || currentUid.equals(otherUid)) {
                return;
            }

            // יצירת מזהה השיחה
            String chatId = ChatUtils.chatId(currentUid, otherUid);


            // מעבר מסך
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("chatId", chatId);
            intent.putExtra("otherUid", otherUid);
            intent.putExtra("otherName", user.getName());
            context.startActivity(intent);
        });
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
        private Button m_BtnChat;

        public PlayerViewHolder(@NonNull View i_ItemView)
        {
            super(i_ItemView);
            m_TvName = i_ItemView.findViewById(R.id.tvSearchPlayerName);
            m_TvGames = i_ItemView.findViewById(R.id.tvSearchPlayerGames);
            m_BtnAddFriend = i_ItemView.findViewById(R.id.btnAddFriend);
            m_BtnChat = i_ItemView.findViewById(R.id.btnChat);
        }
    }
}