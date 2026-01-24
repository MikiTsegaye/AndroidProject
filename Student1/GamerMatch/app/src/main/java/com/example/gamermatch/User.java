package com.example.gamermatch;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class User {private String userId;
    private String name;
    private String email;
    private List<String> m_FavoriteGames;
    private List<String> m_FriendsList;

    public User() {
        //empty for firebase
        }

    public User(String i_UserId, String i_Name, String i_Email) {
        this.userId = i_UserId;
        this.name = i_Name;
        this.email = i_Email;
        this.m_FavoriteGames = new ArrayList<>();
        this.m_FriendsList = new ArrayList<>();
    }

    //  אנו אומרים לפיירבייס בדיוק איזה שדה לחפש
    @PropertyName("userId")
    public String getUserId() { return userId; }
    @PropertyName("userId")
    public void setUserId(String userId) { this.userId = userId; }

    @PropertyName("name")
    public String getName() { return name; }

    @PropertyName("name")
    public void setName(String name) { this.name = name; }
    @PropertyName("email")
    public void setEmail(String email) { this.email = email; }
    @PropertyName("email")
    public String getEmail() { return email; }
    @PropertyName("favoriteGames")
    public List<String> getFavoriteGames() { return m_FavoriteGames; }
    @PropertyName("favoriteGames")
    public void setFavoriteGames(List<String> games) { this.m_FavoriteGames = games; }

    @PropertyName("friends")
    public List<String> getFriendsList() { return m_FriendsList; }

    @PropertyName("friends")
    public void setFriendsList(List<String> friends) { this.m_FriendsList = friends; }

}
