package com.example.gamermatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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

            if (validateInput(name, email, password))
            {
                m_FirebaseHelper.RegisterNewUser(email, password, name);
                Toast.makeText(this, "נרשמת בהצלחה! 🎮", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validateInput(String i_Name, String i_Email, String i_Password)
    {
        boolean v_IsValid = true;

        if (i_Name.isEmpty() || i_Email.isEmpty() || i_Password.isEmpty())
        {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            v_IsValid = false;
        }
        else if (i_Password.length() < 6)
        {
            Toast.makeText(this, "סיסמה חייבת להכיל לפחות 6 תווים", Toast.LENGTH_SHORT).show();
            v_IsValid = false;
        }

        return v_IsValid;
    }
}