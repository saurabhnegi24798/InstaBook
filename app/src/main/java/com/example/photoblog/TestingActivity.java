package com.example.photoblog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class TestingActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("UserPosts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(TestingActivity.this, "Error comming", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<DocumentChange> documentChangeList = value.getDocumentChanges();

                        for (DocumentChange docu : documentChangeList) {
                            if (docu.getType() == DocumentChange.Type.ADDED) {

                                DocumentSnapshot doc = docu.getDocument();

                                String caption = doc.getString("caption");
                                String user_id = doc.getString("user_id");
                                String user_name = doc.getString("user_name");

                                Toast.makeText(TestingActivity.this, caption, Toast.LENGTH_SHORT).show();

                                Log.d("", "*****************************************************");
                                Log.d("", caption + " " + user_name + " " + user_id);

                            }
                        }

                    }
                });





    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}