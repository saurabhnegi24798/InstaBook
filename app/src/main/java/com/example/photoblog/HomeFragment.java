package com.example.photoblog;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<ModelClass> list;
    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FloatingActionButton floatingActionButton;
    private Adapter_NewsFeed adapter_newsFeed;


    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.main_feed);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        floatingActionButton = getActivity().findViewById(R.id.new_post);
        floatingActionButton.setVisibility(View.VISIBLE);

        functionCall();

        adapter_newsFeed = new Adapter_NewsFeed(list, view.getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter_newsFeed);

        return view;
    }

    public void functionCall() {
        firebaseFirestore.collection("UserPosts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<DocumentChange> documentChangeList = value.getDocumentChanges();

                for (DocumentChange docu : documentChangeList) {
                    if (docu.getType() == DocumentChange.Type.ADDED) {

                        DocumentSnapshot doc = docu.getDocument();

                        String postImageUrl = doc.getString("postImageURL");
                        String caption = doc.getString("caption");
                        String user_id = doc.getString("user_id");
                        String documentID = doc.getId();
                        String user_name = doc.getString("user_name");
                        String profileImageURL = doc.getString("profileImageURL");

                        long likesCount = Long.valueOf(doc.getLong("LikesCount"));
                        long commentsCount = Long.valueOf(doc.getLong("CommentCount"));

                        list.add(new ModelClass(postImageUrl, profileImageURL, likesCount, commentsCount, caption, user_name, user_id, documentID));
                        adapter_newsFeed.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}