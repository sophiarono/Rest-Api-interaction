package com.app.restapi;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.restapi.model.Comments;
import com.app.restapi.network.ApiClient;
import com.app.restapi.network.RetrofitInstance;
import com.developer.kalert.KAlertDialog;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {
    ArrayList<Comments> comments;
    private Comments mCommentsArray;
    private OnItemClickListener mListener;
    private RecyclerView mRecyclerView;

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onCommentLike(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public CommentsAdapter(ArrayList<Comments> comments){
        this.comments = comments;
    }
    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.comment_list_item, parent, false);
        return new CommentsViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        mCommentsArray = comments.get(position);
        holder.bind(mCommentsArray);

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }



    public class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView comment;
        TextView likes;
        ImageButton delete;
        ImageButton like;
        public CommentsViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            comment = itemView.findViewById(R.id.tvComment);
            likes = itemView.findViewById(R.id.tvLikes);
            delete = itemView.findViewById(R.id.btnCommentDelete);
            like = itemView.findViewById(R.id.btnCommentLike);
            mRecyclerView = itemView.findViewById(R.id.rvComments);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }

                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onCommentLike(position);
                        }
                    }
                    notifyDataSetChanged();


                }
            });
        }

        public  void bind(Comments comments){

            comment.setText(comments.getBody());
            likes.setText(comments.getLike() + " " + "likes");
        }

        @Override
        public void onClick(View view) {

        }
    }

    private void showComments() {
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        Call<Comments> call = apiClient.getAllComments();
        call.enqueue(new Callback<Comments>() {
            @Override
            public void onResponse(Call<Comments> call, Response<Comments> response) {
                ArrayList<Comments> cmtlist = new ArrayList<Comments>(response.body().getData());
                CommentsAdapter adapter = new CommentsAdapter(cmtlist);
                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<Comments> call, Throwable t) {

            }
        });
    }
}
