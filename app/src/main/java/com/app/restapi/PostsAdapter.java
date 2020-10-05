package com.app.restapi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.app.restapi.model.Post;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {
    ArrayList<Post> posts;
    public PostsAdapter(ArrayList<Post> posts){
        this.posts = posts;
    }
    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.post_list_item, parent, false);
        return new PostsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
       Post postArray = posts.get(position);
        holder.bind(postArray);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            itemView.setOnClickListener(this);
        }

        public  void bind(Post posts){
            title.setText(posts.getTitle());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            int postId = posts.get(position).getId();
            Intent intent = new Intent(view.getContext(), PostDetail.class);
            intent.putExtra("postId", postId);
            view.getContext().startActivity(intent);

        }
    }
}
