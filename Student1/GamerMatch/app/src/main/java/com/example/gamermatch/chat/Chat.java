package com.example.gamermatch.chat;

import com.google.firebase.Timestamp;
import java.util.List;

public class Chat {
    private List<String> participants;
    private String lastMessage;
    private String lastSenderId;
    private Timestamp lastTimestamp;

    // שדות עבור קבוצות משחק
    private String type;      // "dm" או "game_group"
    private String gameName;  // למשל: "FIFA"
    private String gameKey;   // למשל: "fifa"

    // שדות חדשים למניעת ANR וקריסות ב-Inbox
    private String senderName;    // שם שולח ההודעה האחרונה
    private String receiverName;  // שם מקבל ההודעה (רלוונטי ל-DM)

    // קונסטרקטור ריק חובה עבור Firebase Firestore
    public Chat() {}

    public Chat(List<String> participants, String lastMessage, String lastSenderId, Timestamp lastTimestamp) {
        this.participants = participants;
        this.lastMessage = lastMessage;
        this.lastSenderId = lastSenderId;
        this.lastTimestamp = lastTimestamp;
    }

    // גטרים (Getters) וסטרים (Setters) בסיסיים
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

    // גטרים וסטרים עבור השמות (לשימוש ה-InboxAdapter)
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

    // מתודה נוספת שהאדפטר מחפש כדי לזהות את השולח האחרון
    public String getSenderId() {
        return lastSenderId;
    }
}