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

        User user = m_PlayerList.get(position);
        Context context = holder.itemView.getContext();

        if (user.getName() != null) {
            holder.m_TvName.setText(user.getName());
        } else {
            holder.m_TvName.setText("Unknown");
        }
        // אנחנו בודקים אם יש רשימה, ואם כן - מחברים אותה למשפט עם פסיקים
        if (user.getFavoriteGames() != null && !user.getFavoriteGames().isEmpty()) {
            String gamesString = android.text.TextUtils.join(", ", user.getFavoriteGames());
            holder.m_TvGames.setText(context.getString(R.string.player_games_prefix) + gamesString);
        } else {
            holder.m_TvGames.setText(context.getString(R.string.player_games_prefix) +" :None");
        }

        // בתוך onBindViewHolder
        holder.m_BtnAddFriend.setOnClickListener(v -> {
            String v_CurrentUid = FirebaseAuth.getInstance().getUid();
            String v_OtherUid = user.getUserId();

            // בדיקת הגנה - מניעת הוספה של עצמי או נתונים ריקים
            if (v_CurrentUid == null || v_OtherUid == null || v_CurrentUid.equals(v_OtherUid))
            {
                return;
            }

            // פקודת הקסם של פיירבייס: arrayUnion
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(v_CurrentUid)
                    .update("friends", FieldValue.arrayUnion(v_OtherUid))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, context.getString(R.string.added_to_friends), Toast.LENGTH_SHORT).show();

                        // עדכון ויזואלי מיידי למשתמש
                        holder.m_BtnAddFriend.setEnabled(false);
                        holder.m_BtnAddFriend.setText(context.getString(R.string.saved));


                        // אם קיים כפתור הסרה, נפעיל אותו כעת
                        if (holder.m_BtnRemoveFriend != null)
                        {
                            holder.m_BtnRemoveFriend.setEnabled(true);
                            holder.m_BtnRemoveFriend.setText("❌");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context,context.getString(R.string.error_adding_friend), Toast.LENGTH_SHORT).show();
                    });
        });
        holder.m_BtnRemoveFriend.setOnClickListener(v -> {
            String currentUid = FirebaseAuth.getInstance().getUid();
            String otherUid = user.getUserId();

            if (currentUid == null || otherUid == null) return;

            // פקודת הקסם להסרה: arrayRemove
            // זה מסיר את ה-ID מהמערך רק אם הוא קיים שם
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUid)
                    .update("friends", FieldValue.arrayRemove(otherUid))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, context.getString(R.string.removed_from_friends), Toast.LENGTH_SHORT).show();

                        // עדכון ויזואלי: מאפשרים להוסיף שוב או משנים את הטקסט
                        holder.m_BtnRemoveFriend.setEnabled(false);
                        holder.m_BtnRemoveFriend.setText(context.getString(R.string.removed));
                        holder.m_BtnAddFriend.setEnabled(true);
                        holder.m_BtnAddFriend.setText("➕");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, context.getString(R.string.error_removing_friend), Toast.LENGTH_SHORT).show();
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
        private Button m_BtnRemoveFriend;
        private Button m_BtnChat;

        public PlayerViewHolder(@NonNull View i_ItemView)
        {
            super(i_ItemView);
            m_TvName = i_ItemView.findViewById(R.id.tvSearchPlayerName);
            m_TvGames = i_ItemView.findViewById(R.id.tvSearchPlayerGames);
            m_BtnAddFriend = i_ItemView.findViewById(R.id.btnAddFriend);
            m_BtnRemoveFriend = i_ItemView.findViewById(R.id.btnRemoveFriend);
            m_BtnChat = i_ItemView.findViewById(R.id.btnChat);
        }
    }
}