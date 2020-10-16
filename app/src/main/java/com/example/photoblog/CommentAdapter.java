package com.example.photoblog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.viewholder> {

    private Context context;
    private List<CommentModelClass> list;

    public CommentAdapter(Context context, List<CommentModelClass> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comments_list,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        // displaying the comment in the comment text view
        holder.commentTextView.setText(list.get(position).getComment());

        // displaying the image in the circle image view
        Glide.with(context).load(list.get(position).getImageUri()).into(holder.circleImageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView commentTextView;
        CircleImageView circleImageView;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.comment_of_user);
            circleImageView = itemView.findViewById(R.id.comment_userImage);
        }
    }
}
