package com.example.gamermatch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;

public class FireBaseHelper
{
    private FirebaseAuth m_Auth = FirebaseAuth.getInstance();
    private FirebaseFirestore m_DataBase = FirebaseFirestore.getInstance();

    public String GetCurrentUserId()
    {
        String v_UserId = null;
        if (m_Auth.getCurrentUser() != null)
        {
            v_UserId = m_Auth.getCurrentUser().getUid();
        }
        return v_UserId;
    }

    public void RegisterNewUser(String i_Email, String i_Password, String i_Name)
    {
        m_Auth.createUserWithEmailAndPassword(i_Email, i_Password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        String v_UserId = m_Auth.getCurrentUser().getUid();

                        User newUser = new User(v_UserId, i_Name, i_Email);

                        m_DataBase.collection("users").document(v_UserId).set(newUser);
                    }
                });
    }

    public Query SearchPlayersByGame(String i_GameName)
    {
        return m_DataBase.collection("users")
                .whereArrayContains("m_FavoriteGames", i_GameName);
    }

    public void UpdateName(String i_NewName)
    {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null)
        {
            m_DataBase.collection("users").document(v_Uid)
                    .update("m_Name", i_NewName);
        }
    }

    public void AddFavoriteGame(String i_GameName)
    {
        String v_Uid = GetCurrentUserId();
        if (v_Uid != null)
        {
            m_DataBase.collection("users").document(v_Uid)
                    .update("m_FavoriteGames", FieldValue.arrayUnion(i_GameName));
        }
    }
}