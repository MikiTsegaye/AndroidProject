package com.example.gamermatch.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamermatch.R;

import java.util.ArrayList;
import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ChatVH> {

    public interface OnChatClickListener {
        void onChatClick(String chatId, String otherUid);
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

        // Extract the other party's UID
        String otherUid = null;
        if (c.getParticipants() != null) {
            for (String uid : c.getParticipants()) {
                if (!uid.equals(currentUid)) {
                    otherUid = uid;
                    break;
                }
            }
        }

        holder.tvTitle.setText(otherUid != null ? otherUid : "Chat");
        holder.tvLast.setText(c.getLastMessage() != null ? c.getLastMessage() : "");

        String finalOtherUid = otherUid;
        holder.itemView.setOnClickListener(v -> {
            if (finalOtherUid != null) {
                String chatId = ChatUtils.chatId(currentUid, finalOtherUid);
                listener.onChatClick(chatId, finalOtherUid);
            }
        });
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
