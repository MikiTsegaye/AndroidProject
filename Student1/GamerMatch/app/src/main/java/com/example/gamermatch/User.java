package com.example.gamermatch;

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

    public String getUserId() { return userId; }
    public String getName() { return m_Name; }
    public String getEmail() { return m_Email; }
    public List<String> getFavoriteGames() { return m_FavoriteGames; }
    public List<String> getFriendsList() { return m_FriendsList; }


}
