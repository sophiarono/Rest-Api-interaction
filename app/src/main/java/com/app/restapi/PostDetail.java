package com.app.restapi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.restapi.model.Comments;
import com.app.restapi.model.SinglePost;
import com.app.restapi.network.ApiClient;
import com.app.restapi.network.RetrofitInstance;
import com.developer.kalert.KAlertDialog;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetail extends AppCompatActivity implements CommentsAdapter.OnItemClickListener{

    private RecyclerView mRecyclerComments;
    private ImageButton mDeletepost;
    private ImageButton mAddcomment;
    private ImageButton mEdit;
    private EditText mAddComment;
    private CommentsAdapter mAdapter;
    private TextView mTitle;
    private TextView mBody;
    private int mPostId;
    private TextView mNoComments;
    private KAlertDialog pDialog;
    private SinglePost mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        pDialog = new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();


        mTitle = findViewById(R.id.tvPostTitle);
        mBody = findViewById(R.id.tvPostBody);
        mRecyclerComments = findViewById(R.id.rvComments);
        mDeletepost = findViewById(R.id.btnDeletePost);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerComments.setLayoutManager(linearLayoutManager);
        mEdit = findViewById(R.id.btnEditPost);
        mAddcomment = findViewById(R.id.btnComment);
        mAddComment = findViewById(R.id.etPostComment);
        ImageButton like = findViewById(R.id.btnCommentLike);
        mNoComments = findViewById(R.id.tvNoComments);
        mNoComments.setVisibility(View.VISIBLE);


        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetail.this, NewPost.class);
                intent.putExtra("type", "edit");
                intent.putExtra("title", mPost.getTitle());
                intent.putExtra("body", mPost.getBody());
                startActivity(intent);
            }
        });


        mDeletepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePost();
            }
        });
        mAddcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });

        showComments();
        getPost();


    }

    private void getPost() {
        int IdFromIntent = getIntent().getIntExtra("postId", 0);
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        Call<SinglePost> call = apiClient.getSinglePost(IdFromIntent);
        call.enqueue(new Callback<SinglePost>() {
            @Override
            public void onResponse(Call<SinglePost> call, Response<SinglePost> response) {
                mPost = response.body().getData();
                mTitle.setText(mPost.getTitle());
                mBody.setText(mPost.getBody());
                mPostId = mPost.getId();

                pDialog.dismiss();

            }

            @Override
            public void onFailure(Call<SinglePost> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();


            }
        });
    }


    private void addComment() {
        if (TextUtils.isEmpty(mAddComment.getText().toString())) {
            new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                    .setTitleText("Error")
                    .setContentText("field is empty!")
                    .setConfirmText("Ok")
                    .show();

        } else {
            int postId = mPostId;
            Comments add = new Comments();
            add.setBody(mAddComment.getText().toString());
            ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
            Call<ResponseBody> call = apiClient.addComment(postId, add);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    new KAlertDialog(PostDetail.this, KAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Success")
                            .setContentText("Your comment has been posted")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                @Override
                                public void onClick(KAlertDialog kAlertDialog) {
                                    kAlertDialog.dismiss();
                                    showComments();
                                }
                            })
                            .show();
                    mAddComment.setText("");

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(PostDetail.this, t.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }


    }

    private void showComments() {

        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        Call<Comments> call = apiClient.getAllComments();
        call.enqueue(new Callback<Comments>() {
            @Override
            public void onResponse(Call<Comments> call, Response<Comments> response) {
//                    mComments = new ArrayList<Comments>(response.body().getData());
                ArrayList<Comments> dataList = new ArrayList<Comments>(response.body().getData());
                ArrayList<Comments> finaldata = new ArrayList<Comments>();

                    for(int i=0 ; i < dataList.size(); i++) {
                        if( dataList.get(i).getPostId() == mPostId) {

                            Comments data = new Comments();
                            data.setBody(dataList.get(i).getBody());
                            data.setLike(dataList.get(i).getLike());
                            data.setId(dataList.get(i).getId());
                            data.setPostId(dataList.get(i).getPostId());
                            finaldata.add(data);
                            mNoComments.setVisibility(View.INVISIBLE);
                            mAdapter = new CommentsAdapter(finaldata);
                            mRecyclerComments.setAdapter(mAdapter);
                            mAdapter.setOnItemClickListener(new CommentsAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {

                                }

                                @Override
                                public void onDeleteClick(int position) {
                                    int commentId = finaldata.get(position).getId();
                                    ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
                                    Call<ResponseBody> call = apiClient.deleteComment(commentId);
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            new KAlertDialog(PostDetail.this, KAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Are you sure?")
                                                    .setContentText("comment will be deleted permanently!")
                                                    .setConfirmText("Ok")
                                                    .setCancelText("Cancel")
                                                    .showCancelButton(true)
                                                    .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                                        @Override
                                                        public void onClick(KAlertDialog kAlertDialog) {
                                                            kAlertDialog.cancel();
                                                        }
                                                    })
                                                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                                        @Override
                                                        public void onClick(KAlertDialog sDialog) {
                                                            showComments();
                                                            sDialog.dismiss();
                                                        }
                                                    })
                                                    .show();
                                        }
                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            new KAlertDialog(PostDetail.this, KAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Error")
                                                    .setContentText(t.getMessage())
                                                    .setConfirmText("Ok")
                                                    .show();


                                        }
                                    });

                                }

                                @Override
                                public void onCommentLike(int position) {
                                    ImageButton like = findViewById(R.id.btnCommentLike);
                                    Comments comments = new Comments();
                                    comments.setLike(finaldata.get(position).getLike() + 1);
                                    int commentId = finaldata.get(position).getId();
                                    ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
                                    Call<ResponseBody> call = apiClient.commentLike(commentId, comments);
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                new KAlertDialog(PostDetail.this, KAlertDialog.SUCCESS_TYPE)
////                                        .setTitleText("Success")
////                                        .setContentText("You have liked the comment")
////                                        .setConfirmText("Ok")
////                                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
////                                            @Override
////                                            public void onClick(KAlertDialog kAlertDialog) {
////                                                kAlertDialog.dismiss();
////                                                showComments();
////                                            }
////                                        })
////                                        .show();
                                            Toast.makeText(PostDetail.this, "You liked the comment", Toast.LENGTH_LONG).show();
                                            showComments();
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(PostDetail.this, t.getMessage(), Toast.LENGTH_LONG).show();

                                        }
                                    });

                                }
                            });

                        }



                        }

                    }




            @Override
            public void onFailure(Call<Comments> call, Throwable t) {

            }
        });
    }



    private void deletePost() {

        int postId = mPostId;
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        Call<ResponseBody> call = apiClient.deletePost(postId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                new KAlertDialog(PostDetail.this, KAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("post will be deleted permanently!")
                        .setConfirmText("Ok")
                        .setCancelText("Cancel")
                        .showCancelButton(true)
                        .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                kAlertDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                Intent intent = new Intent(PostDetail.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new KAlertDialog(PostDetail.this, KAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText(t.getMessage())
                        .setConfirmText("Ok")
                        .show();


            }
        });

    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {


    }

    @Override
    public void onCommentLike(int position) {

    }
}


