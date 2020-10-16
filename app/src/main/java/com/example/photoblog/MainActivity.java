package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FloatingActionButton floatingActionButton;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private Notification_Fragment notification_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PHOTO BLOG");
        firebaseAuth = FirebaseAuth.getInstance();
        floatingActionButton = findViewById(R.id.new_post);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(MainActivity.this, Login_Activity.class);
            startActivity(intent);
            finish();
        }

        homeFragment = new HomeFragment();
        notification_fragment = new Notification_Fragment();

        replaceFragment(homeFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home_icon) {
                    replaceFragment(homeFragment);
                    return true;
                }
                if (item.getItemId() == R.id.notification_icon) {
                    replaceFragment(notification_fragment);
                    return true;
                }
                return false;
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPostActivity();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
    }

    private void newPostActivity() {
        Intent intent = new Intent(MainActivity.this, newPostActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout_btn_main_menu) {
            Toast.makeText(this, "LOGOUT PRESSED ??", Toast.LENGTH_SHORT).show();
            logout_user();
        }

        if (item.getItemId() == R.id.account_settings_main_menu) {
            Intent intent = new Intent(MainActivity.this, SetupActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout_user() {
        firebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this, Login_Activity.class);
        startActivity(intent);
        finish();
    }
}