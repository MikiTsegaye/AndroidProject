package com.example.gamermatch.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamermatch.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ChatVH> {

    public interface OnChatClickListener {
        // הוספנו isGroup + title כדי ש-InboxActivity יוכל לפתוח נכון
        void onChatClick(String chatId, boolean isGroup, String title, String otherUid);
    }

    private final List<Chat> chats = new ArrayList<>();
    private final String currentUid;
    private final OnChatClickListener listener;

    public InboxAdapter(String currentUid, OnChatClickListener listener) {
        this.currentUid = currentUid;
        this.listener = listener;
    }

    public void setChats(List<Chat> newChats) {
        chats.clear();
        if (newChats != null) chats.addAll(newChats);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_row, parent, false);
        return new ChatVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatVH holder, int position) {
        Chat c = chats.get(position);

        boolean isGroup = "game_group".equalsIgnoreCase(c.getType());

        // 1) Title
        if (isGroup) {
            String title = c.getGameName() != null ? c.getGameName() : "Game Group";
            holder.tvTitle.setText(title);
        } else {
            // DM: מציגים שם של המשתמש השני
            String otherUid = extractOtherUid(c);
            if (otherUid == null) {
                holder.tvTitle.setText("Chat");
            } else {
                String finalOtherUid = otherUid;
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(otherUid)
                        .get()
                        .addOnSuccessListener(doc -> {
                            String name = doc.getString("name");
                            holder.tvTitle.setText(name != null ? name : finalOtherUid);
                        })
                        .addOnFailureListener(e -> holder.tvTitle.setText(finalOtherUid));
            }
        }

        // 2) Last message
        holder.tvLast.setText(c.getLastMessage() != null ? c.getLastMessage() : "");

        // 3) Click
        holder.itemView.setOnClickListener(v -> {
            if (listener == null) return;

            if (isGroup) {
                // בקבוצה: chatId הוא docId קבוע כמו "game_fifa"
                // חשוב: לא לחשב מחדש באמצעות ChatUtils
                String title = c.getGameName() != null ? c.getGameName() : "Game Group";
                // כאן אין otherUid
                listener.onChatClick(getChatIdByPosition(position), true, title, null);
            } else {
                String otherUid = extractOtherUid(c);
                if (otherUid == null) return;

                String chatId = ChatUtils.chatId(currentUid, otherUid);
                listener.onChatClick(chatId, false, null, otherUid);
            }
        });
    }

    // אצלך ה-chatId הוא ה-DocumentId (כמו "uid_uid" או "game_fifa")
    // אבל snapshot.toObjects(Chat.class) לא מביא את ה-id עצמו.
    // לכן כאן אנחנו מניחים שב-InboxActivity לא צריך את ה-id מהאובייקט,
    // וב-DM מחשבים עם ChatUtils, וב-Group אנחנו חייבים שה-id יהיה "game_key".
    // כדי שה-Group ייפתח נכון מהרשימה, הכי פשוט: לקבוע שה-chatId של group הוא "game_<gameKey>"
    // ואז אנחנו יכולים לשחזר אותו כאן.
    private String getChatIdByPosition(int position) {
        Chat c = chats.get(position);
        if ("game_group".equalsIgnoreCase(c.getType())) {
            String key = c.getGameKey();
            if (key == null && c.getGameName() != null) {
                key = c.getGameName().toLowerCase().replaceAll("[^a-z0-9]", "");
            }
            return "game_" + (key != null ? key : "group");
        }
        // DM fallback
        String otherUid = extractOtherUid(c);
        return otherUid != null ? ChatUtils.chatId(currentUid, otherUid) : "";
    }

    private String extractOtherUid(Chat c) {
        if (c.getParticipants() == null) return null;
        for (String uid : c.getParticipants()) {
            if (uid != null && !uid.equals(currentUid)) return uid;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ChatVH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvLast;
        ChatVH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLast = itemView.findViewById(R.id.tvLast);
        }
    }
}