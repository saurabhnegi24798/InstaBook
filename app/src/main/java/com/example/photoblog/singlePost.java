package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.ListDocumentsRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class singlePost extends AppCompatActivity {

    private String documentID;
    private FirebaseFirestore firebaseFirestore;
    private EditText userComment;
    private RecyclerView recyclerView;
    private List<CommentModelClass> list;
    private CommentAdapter commentAdapter;
    private Button addNewComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        addNewComment = findViewById(R.id.add_new_comment);
        userComment = findViewById(R.id.add_comment_single);
        list = new ArrayList<>();
        documentID = getIntent().getStringExtra("KEY_DOC_ID");
        recyclerView = findViewById(R.id.recycler_comments_single);
        firebaseFirestore = FirebaseFirestore.getInstance();

        addNewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToComments();
            }
        });


        commentAdapter = new CommentAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentAdapter);


        //THIS IS FOR RETRIEVING COMMENTS THAT ARE IN CURRENT POST AND SHOWING THEM IN RECYCLER VIEW
        firebaseFirestore
                .collection("UserPosts")
                .document(documentID)
                .collection("Comments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            List<DocumentChange> documentChangeList = value.getDocumentChanges();
                            for (DocumentChange docu : documentChangeList) {
                                if (docu.getType() == DocumentChange.Type.ADDED) {
                                    DocumentSnapshot doc = docu.getDocument();
                                    list.add(new CommentModelClass(doc.getString("image"), doc.getString("Comment")));
                                    commentAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Toast.makeText(singlePost.this, "Unable to retrieve comments", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //THIS METHOD IS FOR USER ADDING THE COMMENT INTO THE DATABASE AND ALSO SHOWING IT IN THE RECYCLER VIEW
    public void addToComments() {
        final String comment = userComment.getText().toString().trim();
        if (comment.length() == 0) {
            Toast.makeText(this, "EMPTY COMMENT", Toast.LENGTH_SHORT).show();
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(userComment.getWindowToken(), 0);

        userComment.setText("");
        FirebaseFirestore
                .getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            final String image = documentSnapshot.getString("imageUrl");
                            Map<String, String> map = new HashMap<>();

                            //comment is the comment done by the user and image is the userProfile of the current user who has commented
                            map.put("Comment", comment);
                            map.put("image", image);

                            Toast.makeText(singlePost.this, image.toString(), Toast.LENGTH_SHORT).show();


                            //now i have to add this map in to the document (any name of doc is valid )
                            FirebaseFirestore
                                    .getInstance()
                                    .collection("UserPosts")
                                    .document(documentID)
                                    .collection("Comments")
                                    .document()
                                    .set(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

//                                            Map<String,Object> map1 = new HashMap<>();
//                                            map1.put("CommentCount",)
//                                            FirebaseFirestore
//                                                    .getInstance()
//                                                    .collection("UserPosts")
//                                                    .document(documentID)
//                                                    .update(map1)
//                                                    .

                                            // now display to the recycler view also
//                                            list.add(new CommentModelClass(image, comment));
                                            commentAdapter.notifyDataSetChanged();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(singlePost.this, "Unable to add to the database singlePostActivity", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(singlePost.this, "Document does not exists singlePostActivity", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(singlePost.this, "", Toast.LENGTH_SHORT).show();
            }
        });

    }
}