package com.example.gamermatch;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity
{
    private EditText m_EtName;
    private Button m_BtnSave;
    private Button m_BtnBack;
    private FireBaseHelper m_FirebaseHelper;

    @Override
    protected void onCreate(Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        m_FirebaseHelper = new FireBaseHelper();
        m_EtName = findViewById(R.id.etEditName);
        m_BtnSave = findViewById(R.id.btnSaveProfile);

        m_BtnSave.setOnClickListener(v ->
        {
            updateProfile();
        });

        EditText etGameName = findViewById(R.id.etGameName);
        Button btnAddGame = findViewById(R.id.btnAddGame);

        btnAddGame.setOnClickListener(v -> {
            String game = etGameName.getText().toString().trim();
            if (!game.isEmpty()) {
                m_FirebaseHelper.AddFavoriteGame(game);
                Toast.makeText(this, game + " נוסף למשחקים שלך!", Toast.LENGTH_SHORT).show();
                etGameName.setText("");
            }
        });
        m_BtnBack = findViewById(R.id.btnBackToMain);
        m_BtnBack.setOnClickListener(v ->
        {
            finish();
        });
    }

    private void updateProfile()
    {
        String newName = m_EtName.getText().toString().trim();

        if (!newName.isEmpty())
        {
            m_FirebaseHelper.UpdateName(newName);
            Toast.makeText(this, "הפרופיל עודכן בהצלחה! ✅", Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            Toast.makeText(this, "נא להזין שם תקין", Toast.LENGTH_SHORT).show();
        }
    }
}