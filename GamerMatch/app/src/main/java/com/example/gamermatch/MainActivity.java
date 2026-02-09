package com.example.gamermatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gamermatch.search.MyFriendsActivity;
import com.example.gamermatch.search.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Main Activity
 * Handles navigation to all major app features
 */
public class MainActivity extends AppCompatActivity
{
    private Button m_BtnEditProfile;
    private FirebaseAuth m_Auth;
    private Button m_BtnSearch;

    @Override
    protected void onCreate(Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_main);

        m_Auth = FirebaseAuth.getInstance();

        m_BtnEditProfile = findViewById(R.id.btnGoToEditProfile);
        m_BtnSearch = findViewById(R.id.btnGoToSearch);
        Button btnMyFriends = findViewById(R.id.btnMyFriends);

        // Navigation to my profile.
        m_BtnEditProfile.setOnClickListener(v ->
        {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Navigation to inbox.
        findViewById(R.id.btnGoToChat).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, com.example.gamermatch.chat.InboxActivity.class));
        });

        // Navigation to search.
        m_BtnSearch.setOnClickListener(v ->
        {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });

        // Navigation to friend list.
        btnMyFriends.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyFriendsActivity.class);
            startActivity(intent);
        });
    }
}