package com.example.gamermatch;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity
{
    private EditText m_EtEmail;
    private EditText m_EtPassword;
    private Button m_BtnLogin;
    private TextView m_TvRegisterLink;
    private FirebaseAuth m_Auth;

    @Override
    protected void onCreate(Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_login);

        m_Auth = FirebaseAuth.getInstance();

        m_EtEmail = findViewById(R.id.etLoginEmail);
        m_EtPassword = findViewById(R.id.etLoginPassword);
        m_BtnLogin = findViewById(R.id.btnLogin);
        m_TvRegisterLink = findViewById(R.id.tvGoToRegister);

        m_BtnLogin.setOnClickListener(v ->
        {
            String email = m_EtEmail.getText().toString().trim();
            String password = m_EtPassword.getText().toString().trim();

            if (validateFields(email, password))
            {
                signIn(email, password);
            }
        });

        m_TvRegisterLink.setOnClickListener(v ->
        {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * פונקציה: validateFields
     * שינויים: שימוש ב-getString עבור הודעת מילוי שדות
     */
    private boolean validateFields(String i_Email, String i_Password)
    {
        if (i_Email.isEmpty() || i_Password.isEmpty())
        {
            // מומלץ להוסיף string ייעודי ב-XML עבור "Please fill all fields"
            Toast.makeText(this, getString(R.string.not_logged_in), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * פונקציה: signIn
     * שינויים: לוקאליזציה של הודעות הצלחה ושגיאה
     */
    private void signIn(String i_Email, String i_Password)
    {
        m_Auth.signInWithEmailAndPassword(i_Email, i_Password)
                .addOnCompleteListener(this, task ->
                {
                    if (task.isSuccessful())
                    {
                        // שימוש במחרוזת ברוכים הבאים מה-XML
                        Toast.makeText(this, getString(R.string.welcome_title), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        // לוקאליזציה של שגיאת התחברות
                        Toast.makeText(this, getString(R.string.not_logged_in), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}