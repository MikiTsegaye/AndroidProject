package com.example.gamermatch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FireBaseHelper {

    private final FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore m_DataBase = FirebaseFirestore.getInstance();

    public String GetCurrentUserId() {
        return (m_Auth.getCurrentUser() != null) ? m_Auth.getCurrentUser().getUid() : null;
    }

    public void RegisterNewUser(String i_Email, String i_Password, String i_Name) {
        m_Auth.createUserWithEmailAndPassword(i_Email, i_Password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String v_UserId = GetCurrentUserId();
                        if (v_UserId == null) return;

                        // שומרים שדות אחידים, בלי m_
                        m_DataBase.collection("users").document(v_UserId).set(
                                new java.util.HashMap<String, Object>() {{
                                    put("userId", v_UserId);      // אופציונלי
                                    put("name", i_Name);
                                    put("email", i_Email);
                                    put("favoriteGames", new java.util.ArrayList<>());
                                    put("friends", new java.util.ArrayList<>());
                                }}
                        );
                    }
                });
    }

    // 🔎 חיפוש שחקנים לפי משחק: מחפש בתוך favoriteGames
    public Query SearchPlayersByGame(String i_GameName) {
        return m_DataBase.collection("users")
                .whereArrayContains("favoriteGames", i_GameName);
    }

    // ✏️ עדכון שם: מעדכן name
    public void UpdateName(String i_NewName) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("name", i_NewName);
        }
    }

    // 🎮 הוספת משחק
    public void AddFavoriteGame(String i_GameName) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("favoriteGames", FieldValue.arrayUnion(i_GameName));
        }
    }

    // 🎮 הסרת משחק (צריך בשביל checkbox)
    public void RemoveFavoriteGame(String i_GameName) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("favoriteGames", FieldValue.arrayRemove(i_GameName));
        }
    }

    // 👥 הוספת חבר
    public void AddFriend(String i_FriendUid) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("friends", FieldValue.arrayUnion(i_FriendUid));
        }
    }

    // 👥 הסרת חבר (מומלץ)
    public void RemoveFriend(String i_FriendUid) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("friends", FieldValue.arrayRemove(i_FriendUid));
        }
    }
}