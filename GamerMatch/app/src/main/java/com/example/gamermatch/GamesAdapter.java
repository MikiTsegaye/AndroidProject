package com.example.gamermatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adapter for selecting favorite games via checkboxes
 */
public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.VH> {

    public interface OnToggleListener {
        void onToggle(String gameName, boolean checked);
    }

    private final List<String> m_AllGames = new ArrayList<>();
    private final Set<String> m_Selected = new HashSet<>();
    private final OnToggleListener m_Listener;

    public GamesAdapter(OnToggleListener listener) {
        m_Listener = listener;
    }

    /**
     * Updates the list of all available games
     */
    public void setAllGames(List<String> games) {
        m_AllGames.clear();
        if (games != null) m_AllGames.addAll(games);
        notifyDataSetChanged();
    }

    /**
     * Updates the set of currently selected (favorite) games
     */
    public void setSelectedGames(List<String> selected) {
        m_Selected.clear();
        if (selected != null) m_Selected.addAll(selected);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_checkbox, parent, false);
        return new VH(v);
    }

    /**
     * Method: onBindViewHolder
     * Changes: Ensures the checkbox text is set dynamically from the database
     */
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String game = m_AllGames.get(position);

        // Reset listener before setting state to avoid trigger during binding
        holder.cb.setOnCheckedChangeListener(null);
        holder.cb.setText(game); // Game names are dynamic from Firestore
        holder.cb.setChecked(m_Selected.contains(game));

        holder.cb.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) m_Selected.add(game);
            else m_Selected.remove(game);

            if (m_Listener != null) m_Listener.onToggle(game, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return m_AllGames.size();
    }

    // ViewHolder instead of typing findViewById() everytime
    static class VH extends RecyclerView.ViewHolder {
        CheckBox cb;
        VH(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.cbGame);
        }
    }
}