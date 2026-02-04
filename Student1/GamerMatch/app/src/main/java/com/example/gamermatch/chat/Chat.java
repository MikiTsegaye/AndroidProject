package com.example.gamermatch.chat;

import com.google.firebase.Timestamp;
import java.util.List;

public class Chat {
    private List<String> participants;
    private String lastMessage;
    private String lastSenderId;
    private Timestamp lastTimestamp;

    // NEW:
    private String type;      // "dm" / "game_group"
    private String gameName;  // "FIFA"
    private String gameKey;   // "fifa"

    // Must for Firestore
    public Chat() {}

    public Chat(List<String> participants, String lastMessage, String lastSenderId, Timestamp lastTimestamp) {
        this.participants = participants;
        this.lastMessage = lastMessage;
        this.lastSenderId = lastSenderId;
        this.lastTimestamp = lastTimestamp;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastSenderId() {
        return lastSenderId;
    }

    public Timestamp getLastTimestamp() {
        return lastTimestamp;
    }

    public String getType() { return type; }
    public String getGameName() { return gameName; }
    public String getGameKey() { return gameKey; }
}
