package com.example.gamermatch.chat;

import com.google.firebase.Timestamp;
import java.util.List;

public class Chat {
    private List<String> participants;
    private String lastMessage;
    private String lastSenderId;
    private Timestamp lastTimestamp;

    private String type;
    private String gameName;
    private String gameKey;
    private String senderName;
    private String receiverName;
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

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastSenderId() {
        return lastSenderId;
    }

    public void setLastSenderId(String lastSenderId) {
        this.lastSenderId = lastSenderId;
    }

    public Timestamp getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(Timestamp lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderId() {
        return lastSenderId;
    }
}