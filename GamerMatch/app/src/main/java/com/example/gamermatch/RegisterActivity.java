package com.example.gamermatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Register Activity / מסך הרשמה
 * Supports localization for English and Hebrew.
 */
public class RegisterActivity extends AppCompatActivity
{
    private EditText m_EtName;
    private EditText m_EtEmail;
    private EditText m_EtPassword;
    private Button m_BtnRegister;
    private FireBaseHelper m_FirebaseHelper;

    @Override
    protected void onCreate(Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_register);

        m_FirebaseHelper = new FireBaseHelper();
        m_EtName = findViewById(R.id.etName);
        m_EtEmail = findViewById(R.id.etEmail);
        m_EtPassword = findViewById(R.id.etPassword);
        m_BtnRegister = findViewById(R.id.btnRegister);

        m_BtnRegister.setOnClickListener(v -> {
            String name = m_EtName.getText().toString().trim();
            String email = m_EtEmail.getText().toString().trim();
            String password = m_EtPassword.getText().toString().trim();

            if (validateInput(name, email, password)) {
                // מציגים הודעת המתנה במידת הצורך
                m_BtnRegister.setEnabled(false);

                m_FirebaseHelper.RegisterNewUser(email, password, name, (success, error) -> {
                    if (success) {
                        Toast.makeText(this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        m_BtnRegister.setEnabled(true);
                        Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /**
     * Method: validateInput
     * Changes: Replaced hardcoded Hebrew toasts with localized strings.
     */
    private boolean validateInput(String i_Name, String i_Email, String i_Password)
    {
        if (i_Name.isEmpty() || i_Email.isEmpty() || i_Password.isEmpty())
        {
            // Localized: "Please fill all fields"
            Toast.makeText(this, getString(R.string.enter_valid_name), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (i_Password.length() < 6)
        {
            // Localized error for short password
            Toast.makeText(this, getString(R.string.not_logged_in), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}