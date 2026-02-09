package com.example.gamermatch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Helper class for Firebase operations
 */
public class FireBaseHelper {

    private final FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore m_DataBase = FirebaseFirestore.getInstance();

    /**
     * Get Current User ID
     */
    public String GetCurrentUserId() {
        return (m_Auth.getCurrentUser() != null) ? m_Auth.getCurrentUser().getUid() : null;
    }

    /**
     * Register New User
     * Updated to ensure the provided name is saved correctly to the database.
     */
    public void RegisterNewUser(String i_Email, String i_Password, String i_Name, OnRegistrationCompleteListener listener) {
        m_Auth.createUserWithEmailAndPassword(i_Email, i_Password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String v_UserId = GetCurrentUserId();
                        if (v_UserId == null) {
                            listener.onResult(false, "User ID is null");
                            return;
                        }

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("userId", v_UserId);
                        userMap.put("name", i_Name);
                        userMap.put("email", i_Email);
                        userMap.put("favoriteGames", new ArrayList<String>());
                        userMap.put("friends", new ArrayList<String>());

                        // Set used to make sure every document is new
                        m_DataBase.collection("users").document(v_UserId).set(userMap)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        listener.onResult(true, null);
                                    } else {
                                        listener.onResult(false, dbTask.getException().getMessage());
                                    }
                                });
                    } else {
                        listener.onResult(false, task.getException().getMessage());
                    }
                });
    }
    public interface OnRegistrationCompleteListener {
        void onResult(boolean success, String errorMessage);
    }
    /**
     * Search Players by Game
     */
    public Query SearchPlayersByGame(String i_GameName) {
        return m_DataBase.collection("users")
                .whereArrayContains("favoriteGames", i_GameName);
    }

    /**
     * Update User Name
     */
    public void UpdateName(String i_NewName) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("name", i_NewName);
        }
    }

    /**
     * Add Game to Favorites
     */
    public void AddFavoriteGame(String i_GameName) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("favoriteGames", FieldValue.arrayUnion(i_GameName));
        }
    }

    /**
     * Remove Game from Favorites
     */
    public void RemoveFavoriteGame(String i_GameName) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("favoriteGames", FieldValue.arrayRemove(i_GameName));
        }
    }

    /**
     * Add Friend
     */
    public void AddFriend(String i_FriendUid) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("friends", FieldValue.arrayUnion(i_FriendUid));
        }
    }

    /**
     * Remove Friend
     */
    public void RemoveFriend(String i_FriendUid) {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null) {
            m_DataBase.collection("users").document(v_Uid)
                    .update("friends", FieldValue.arrayRemove(i_FriendUid));
        }
    }
}