package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Activity extends AppCompatActivity {

    private EditText emailLogin, passwordLogin;
    private Button loginLogin, registerLogin;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        firebaseAuth = FirebaseAuth.getInstance();
        emailLogin = findViewById(R.id.editTextTextEmailAddress);
        passwordLogin = findViewById(R.id.editTextTextPassword);
        loginLogin = findViewById(R.id.login_button);
        registerLogin = findViewById(R.id.register_button);

        loginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        registerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Activity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    void loginUser() {
        String email_login = emailLogin.getText().toString().trim();
        String password_login = passwordLogin.getText().toString().trim();
        if (!TextUtils.isEmpty(email_login) && !TextUtils.isEmpty(password_login)) {
            firebaseAuth.signInWithEmailAndPassword(email_login, password_login).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessege = task.getException().toString();
                        Toast.makeText(Login_Activity.this, errorMessege, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}