package com.example.gamermatch;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class User {private String userId;
    private String m_Name;
    private String m_Email;
    private List<String> m_FavoriteGames;
    private List<String> m_FriendsList;

    public User() {
        //empty for firebase
        }

    public User(String i_UserId, String i_Name, String i_Email) {
        this.userId = i_UserId;
        this.m_Name = i_Name;
        this.m_Email = i_Email;
        this.m_FavoriteGames = new ArrayList<>();
        this.m_FriendsList = new ArrayList<>();
    }

    //  אנו אומרים לפיירבייס בדיוק איזה שדה לחפש
    @PropertyName("userId")
    public String getUserId() { return userId; }
    @PropertyName("userId")
    public void setUserId(String userId) { this.userId = userId; }

    @PropertyName("name")
    public String getName() { return m_Name; }

    @PropertyName("name")
    public void setName(String name) { this.m_Name = name; }
    @PropertyName("email")
    public void setEmail(String email) { this.m_Email = email; }
    @PropertyName("email")
    public String getEmail() { return m_Email; }
    @PropertyName("favoriteGames")
    public List<String> getFavoriteGames() { return m_FavoriteGames; }
    @PropertyName("favoriteGames")
    public void setFavoriteGames(List<String> games) { this.m_FavoriteGames = games; }

    @PropertyName("friends")
    public List<String> getFriendsList() { return m_FriendsList; }

    @PropertyName("friends")
    public void setFriendsList(List<String> friends) { this.m_FriendsList = friends; }

}
