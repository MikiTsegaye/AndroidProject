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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private final String currentUid;
    private final List<Message> messages = new ArrayList<>();

    public MessageAdapter(String currentUid) {
        this.currentUid = currentUid;
    }

    public void setMessages(List<Message> list) {
        messages.clear();
        if (list != null) messages.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message m = messages.get(position);
        if (m.getSenderId() != null && m.getSenderId().equals(currentUid)) return TYPE_SENT;
        return TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == TYPE_SENT) ? R.layout.item_message_sent : R.layout.item_message_received;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.tvText.setText(messages.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvText;
        VH(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
        }
    }

    private final java.util.Set<String> seenIds = new java.util.HashSet<>();

    public void clearAll() {
        messages.clear();
        seenIds.clear();
        notifyDataSetChanged();
    }

    public boolean addMessageIfNew(String id, Message m) {
        if (id == null || m == null) return false;
        if (seenIds.contains(id)) return false;

        seenIds.add(id);
        messages.add(m);
        notifyItemInserted(messages.size() - 1);
        return true;
    }
}
