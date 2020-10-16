package com.example.photoblog;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_NewsFeed extends RecyclerView.Adapter<Adapter_NewsFeed.viewholder> {

    private List<ModelClass> list;
    private Context context;

    public Adapter_NewsFeed(List<ModelClass> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_feed, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, final int position) {

        // Updating the username
        TextView userName = (TextView) holder.itemView.findViewById(R.id.user_name_feedTop);
        userName.setText(list.get(position).getUserName());

        //fetching weather this post was liked or not that's it
        FirebaseFirestore.getInstance().collection("UserPosts")
                .document(list.get(position).getDocumentID()).collection("Likes")
                .document(list.get(position).getUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    holder.itemView.findViewById(R.id.like_button).setBackgroundResource(R.drawable.like);
                } else {
                    holder.itemView.findViewById(R.id.like_button).setBackgroundResource(R.drawable.dislike);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Can't say if liked or not", Toast.LENGTH_SHORT).show();
            }
        });

        //Updating the caption
        TextView caption = (TextView) holder.itemView.findViewById(R.id.feed_caption);
        caption.setText(list.get(position).getCaption());

        //Updating the likes count and comments count
        final TextView likes = holder.itemView.findViewById(R.id.count_of_likes);
        likes.setText(String.valueOf(list.get(position).getLikesCount()) + " Likes");

        final TextView comments = holder.itemView.findViewById(R.id.count_of_comments);
//        comments.setText(String.valueOf(list.get(position).getCommentsCount()) + " Comments");

        //Updating the circle imageView
        CircleImageView circleImageView = holder.itemView.findViewById(R.id.circle_image_feedTop);
        Glide.with(context).load(list.get(position).getProfileImage()).into(circleImageView);

        //Updating the post image
        ImageView imageView = holder.itemView.findViewById(R.id.feed_imageView);
        Glide.with(context).load(list.get(position).getImageResource()).into(imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, singlePost.class);
                intent.putExtra("KEY_DOC_ID", list.get(position).getDocumentID());
                context.startActivity(intent);
            }
        });

        holder.itemView.findViewById(R.id.like_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DocumentReference documentReference =
                        FirebaseFirestore
                                .getInstance()
                                .collection("UserPosts").document(list.get(position).getDocumentID())
                                .collection("Likes")
                                .document(list.get(position).getUserId());

                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            // liked initially so now i have to dislike
                            if (task.getResult().exists()) {
                                documentReference.delete();
                                Toast.makeText(context, "Disliked", Toast.LENGTH_SHORT).show();
                                holder.itemView.findViewById(R.id.like_button).setBackgroundResource(R.drawable.dislike);

                                //also update the likes count in the adapter
                                updateLikeCount(-1, position);

                                //showing the new number of likes in the textView
                                TextView textView = holder.itemView.findViewById(R.id.count_of_likes);
                                textView.setText(list.get(position).getLikesCount() + " Likes");

                            }
                            // disliked initially so now i have to like
                            else {
                                Toast.makeText(context, "YOYO", Toast.LENGTH_SHORT).show();
                                Map<String, Object> map = new HashMap<>();
                                map.put("Timestamp", Timestamp.now());
                                holder.itemView.findViewById(R.id.like_button).setBackgroundResource(R.drawable.like);

                                updateLikeCount(1, position);

                                //also update the likes count in the adapter and in the firestore database
                                TextView textView = holder.itemView.findViewById(R.id.count_of_likes);
                                textView.setText(list.get(position).getLikesCount() + " Likes");

                                documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Unable to like", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            //now update in the database post "LikesCount"
                            //we have to run a query for that
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put("LikesCount", list.get(position).getLikesCount());

                            FirebaseFirestore.getInstance().collection("UserPosts").document(list.get(position).getDocumentID())
                                    .update(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Like/Dislike added", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Poor Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                    }
                });
            }
        });
    }

    private void updateLikeCount(int value, int pos) {
        list.get(pos).setLikesCount(list.get(pos).getLikesCount() + value);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        private TextView userName, caption, likes, comments;
        private ImageView profileImage, postImage;

        public viewholder(@NonNull final View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name_feedTop);
            caption = itemView.findViewById(R.id.feed_caption);
            likes = itemView.findViewById(R.id.count_of_likes);
            comments = itemView.findViewById(R.id.count_of_comments);
            profileImage = itemView.findViewById(R.id.circle_image_feedTop);
            postImage = itemView.findViewById(R.id.feed_imageView);

        }
    }
}
