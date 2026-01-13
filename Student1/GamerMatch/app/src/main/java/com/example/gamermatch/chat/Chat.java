package com.example.gamermatch.chat;

import com.google.firebase.Timestamp;
import java.util.List;

public class Chat {
    private List<String> participants;
    private String lastMessage;
    private String lastSenderId;
    private Timestamp lastTimestamp;

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
}
