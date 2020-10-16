package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView imageView;
    private Button button;
    private Uri mImageUri = null;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private EditText user_name;
    private DocumentReference documentReference;
    private static final String imageUrl = "imageUrl";
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        button = findViewById(R.id.uploadImage);
        backButton = findViewById(R.id.backButton);
        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.main_toolbar);
        user_name = findViewById(R.id.user_name);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        documentReference = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("PHOTO BLOG");

        checkForUserData();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_user_data();
            }
        });
    }

    private void checkForUserData() {
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    String name_of_user = documentSnapshot.getString("name");
                    String url_of_image = documentSnapshot.getString(imageUrl);

                    user_name.setText(name_of_user);
                    Glide.with(SetupActivity.this).load(url_of_image).into(imageView);

                    Toast.makeText(SetupActivity.this, "Url = " + url_of_image + " and name = " + name_of_user, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SetupActivity.this, "Doc does not exist yet", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void upload_user_data() {
        if (mImageUri != null) {
            final String username = user_name.getText().toString().trim();
            if (username.length() > 0) {

                final StorageReference ref = storageReference.child("UserProfile/" + firebaseAuth.getUid() + ".png");
                ref.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(SetupActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();

                        //HERE I AM GETTING THE DOWNLOAD URL
                        final Map<String, String> map = new HashMap<>();
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                map.put("name", username);
                                map.put(imageUrl, uri.toString());
                                documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //yaha pe glide ki zarurat nahi hai coz we are finishing at last this activity
                                        // if we use glide here then we will get exception at runtime because before glide is doing its work the activity gets destroyed due to finish
                                        Toast.makeText(SetupActivity.this, "Image and Name Added on database", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SetupActivity.this, "Error in adding to database", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SetupActivity.this, "Unable to add Try again", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please Enter Username", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please Select a Photo", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                imageView.setImageBitmap(bitmap);
                Toast.makeText(this, mImageUri.toString(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}