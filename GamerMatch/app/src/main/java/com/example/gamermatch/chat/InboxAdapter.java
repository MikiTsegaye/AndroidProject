package com.example.gamermatch.chat;

import android.content.Context;
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
        void onChatClick(String chatId, boolean isGroup, String title, String otherUid);
    }

    private final List<Chat> m_Chats = new ArrayList<>();
    private final String m_CurrentUid;
    private final OnChatClickListener m_Listener;

    public InboxAdapter(String i_CurrentUid, OnChatClickListener i_Listener) {
        this.m_CurrentUid = i_CurrentUid;
        this.m_Listener = i_Listener;
    }

    public void setChats(List<Chat> i_NewChats) {
        m_Chats.clear();
        if (i_NewChats != null) m_Chats.addAll(i_NewChats);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatVH onCreateViewHolder(@NonNull ViewGroup i_Parent, int i_ViewType) {
        View v_View = LayoutInflater.from(i_Parent.getContext()).inflate(R.layout.item_chat_row, i_Parent, false);
        return new ChatVH(v_View);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatVH holder, int position) {
        Chat v_Chat = m_Chats.get(position);
        Context context = holder.itemView.getContext();
        boolean v_IsGroup = "game_group".equalsIgnoreCase(v_Chat.getType());

        // 1) Title logic with Localization
        if (v_IsGroup) {
            String v_Title = v_Chat.getGameName() != null ? v_Chat.getGameName() : context.getString(R.string.game_group_default_title);
            holder.tvTitle.setText(v_Title);
        } else {
            String v_OtherName;

            // Logic to pick the correct display name
            if (v_Chat.getSenderId() != null && v_Chat.getSenderId().equals(m_CurrentUid)) {
                v_OtherName = v_Chat.getReceiverName();
            } else {
                v_OtherName = v_Chat.getSenderName();
            }

            // Use localized "Chat with %s" format
            String finalDisplayName = v_OtherName != null ? v_OtherName : context.getString(R.string.player_name_placeholder);
            holder.tvTitle.setText(context.getString(R.string.chat_with_prefix, finalDisplayName));
        }

        // 2) Last message logic with Localization
        holder.tvLast.setText(v_Chat.getLastMessage() != null && !v_Chat.getLastMessage().isEmpty()
                ? v_Chat.getLastMessage()
                : context.getString(R.string.chat_last_message_placeholder));

        // 3) Click listener
        holder.itemView.setOnClickListener(v -> {
            if (m_Listener == null) return;

            if (v_IsGroup) {
                String v_ChatId = getChatIdByPosition(position);
                String v_Title = v_Chat.getGameName() != null ? v_Chat.getGameName() : context.getString(R.string.game_group_default_title);
                m_Listener.onChatClick(v_ChatId, true, v_Title, null);
            } else {
                String v_OtherUid = extractOtherUid(v_Chat);
                if (v_OtherUid == null) return;
                String v_ChatId = ChatUtils.chatId(m_CurrentUid, v_OtherUid);
                m_Listener.onChatClick(v_ChatId, false, null, v_OtherUid);
            }
        });
    }

    private String getChatIdByPosition(int i_Position) {
        Chat v_Chat = m_Chats.get(i_Position);
        if ("game_group".equalsIgnoreCase(v_Chat.getType())) {
            String v_Key = v_Chat.getGameKey();
            return "game_" + (v_Key != null ? v_Key : "group");
        }
        String v_OtherUid = extractOtherUid(v_Chat);
        return v_OtherUid != null ? ChatUtils.chatId(m_CurrentUid, v_OtherUid) : "";
    }

    private String extractOtherUid(Chat i_Chat) {
        if (i_Chat.getParticipants() == null) return null;
        for (String v_Uid : i_Chat.getParticipants()) {
            if (v_Uid != null && !v_Uid.equals(m_CurrentUid)) return v_Uid;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return m_Chats.size();
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