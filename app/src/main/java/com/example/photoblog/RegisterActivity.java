package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailRegister, passwordRegister, confirmPasswordRegister;
    private Button registerButton, loginRegister;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        emailRegister = findViewById(R.id.editTextTextEmailAddress);
        passwordRegister = findViewById(R.id.editTextTextPassword);
        confirmPasswordRegister = findViewById(R.id.editTextConfimPasssword);
        registerButton = findViewById(R.id.register_button);
        loginRegister = findViewById(R.id.login_register);

        loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, Login_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    void registerUser() {
        String email = emailRegister.getText().toString().trim();
        String password = passwordRegister.getText().toString().trim();
        String confirmPassword = confirmPasswordRegister.getText().toString().trim();
        if (password.length() >= 8) {
            if (password.equals(confirmPassword)) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Invalid email id or poor internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(RegisterActivity.this, "Password does not matches confirm password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(RegisterActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
        }
    }
}