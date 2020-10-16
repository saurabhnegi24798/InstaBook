package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.DocumentTransform;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class newPostActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText editText;
    private Button button;
    private Uri mImageUri;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private String userName = "*****";
    private String userProfileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        imageView = findViewById(R.id.new_post_image);
        editText = findViewById(R.id.new_post_caption);
        button = findViewById(R.id.new_post_button);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getUserName();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });

    }

    private void getUserName() {
        firebaseFirestore.collection("Users").document(firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userName = documentSnapshot.getString("name");
                    userProfileUrl = documentSnapshot.getString("imageUrl");
                } else {
                    Toast.makeText(newPostActivity.this, "Upload username and photo first", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(e.toString(), "Not able to retrieve name and userid from firestore");
            }
        });
    }

    private void chooseImage() {    //This is an explicit intent
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

    private void uploadPost() {
        if (mImageUri == null || editText.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Add image and caption", Toast.LENGTH_SHORT).show();
        } else {
            final String caption = editText.getText().toString().trim();
            final String name = String.valueOf(System.currentTimeMillis());
            storageReference.child("UserPosts").child(name).putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        storageReference.child("UserPosts").child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                Map<String, Object> map = new HashMap<>();
                                map.put("postImageURL", uri.toString());
                                map.put("user_name", userName);
                                map.put("user_id", firebaseAuth.getUid());
                                map.put("caption", caption);
                                map.put("profileImageURL", userProfileUrl);
                                map.put("Timestamp", FieldValue.serverTimestamp());
                                map.put("LikesCount", 0L);
                                map.put("CommentCount", 0L);

                                //INSIDE THE DATABASE WE HAVE ADDED THE WHOLE INFORMATION ABOUT THE POST CURRENTLY LIKES AND COMMENTS COUNT TO 0 BOTH WE WILL UPDATE THIS SOON
                                firebaseFirestore.collection("UserPosts").document(name).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Map<String, Object> map2 = new HashMap<>();
                                            map2.put("Timestamp", Timestamp.now());

                                            //HERE I HAVE ADDED LIKES COLLECTION TO THE DB .. THIS IS FOR GETTING THE LIKES COUNT
                                            firebaseFirestore.collection("UserPosts").document(name).collection("Likes").add(map2).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {

                                                    Toast.makeText(newPostActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(newPostActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(newPostActivity.this, "Error newPostActivity 1 ", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(newPostActivity.this, "Error newPostActivity 2", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(newPostActivity.this, "Error newPostActivity 3", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(newPostActivity.this, "Error newPostActivity 4", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(newPostActivity.this, "Failed to add to storage retry", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(newPostActivity.this, "Unable to update please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}