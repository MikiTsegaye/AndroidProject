package com.example.gamermatch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Helper class for Firebase operations / מחלקת עזר לפעולות פיירבייס
 */
public class FireBaseHelper {

    private final FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore m_DataBase = FirebaseFirestore.getInstance();

    /**
     * Get Current User ID / קבלת מזהה המשתמש המחובר
     */
    public String GetCurrentUserId() {
        return (m_Auth.getCurrentUser() != null) ? m_Auth.getCurrentUser().getUid() : null;
    }

    /**
     * Register New User / הרשמת משתמש חדש
     * Updated to ensure the provided name is saved correctly to the database.
     */
    public void RegisterNewUser(String i_Email, String i_Password, String i_Name) {
        m_Auth.createUserWithEmailAndPassword(i_Email, i_Password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String v_UserId = GetCurrentUserId();
                        if (v_UserId == null) return;

                        // Save user profile with standardized fields
                        m_DataBase.collection("users").document(v_UserId).set(
                                new java.util.HashMap<String, Object>() {{
                                    put("userId", v_UserId);
                                    put("name", i_Name); // Uses the name provided in the registration form
                                    put("email", i_Email);
                                    put("favoriteGames", new java.util.ArrayList<>());
                                    put("friends", new java.util.ArrayList<>());
                                }}
                        );
                    }
                });
    }

    /**
     * Search Players by Game / חיפוש שחקנים לפי משחק
     */
    public Query SearchPlayersByGame(String i_GameName) {
        return m_DataBase.collection("users")
                .whereArrayContains("favoriteGames", i_GameName);
    }

    /**
     * Update User Name / עדכון שם משתמש
     */
    public void UpdateName(String i_NewName) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("name", i_NewName);
        }
    }

    /**
     * Add Game to Favorites / הוספת משחק למועדפים
     */
    public void AddFavoriteGame(String i_GameName) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("favoriteGames", FieldValue.arrayUnion(i_GameName));
        }
    }

    /**
     * Remove Game from Favorites / הסרת משחק מהמועדפים
     */
    public void RemoveFavoriteGame(String i_GameName) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("favoriteGames", FieldValue.arrayRemove(i_GameName));
        }
    }

    /**
     * Add Friend / הוספת חבר
     */
    public void AddFriend(String i_FriendUid) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("friends", FieldValue.arrayUnion(i_FriendUid));
        }
    }

    /**
     * Remove Friend / הסרת חבר
     */
    public void RemoveFriend(String i_FriendUid) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("friends", FieldValue.arrayRemove(i_FriendUid));
        }
    }
}