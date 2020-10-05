package com.app.restapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.restapi.model.Post;
import com.app.restapi.network.ApiClient;
import com.app.restapi.network.RetrofitInstance;
import com.developer.kalert.KAlertDialog;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPost extends AppCompatActivity {

    private EditText mTitle;
    private EditText mBody;
    private TextView mMessage;
    private Post mSelectedPost;
    private Button mSubmit;
    private String mType;
    private TextView mDisplayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mTitle = findViewById(R.id.etTitle);
        mDisplayText = findViewById(R.id.txtMessage);
        mBody = findViewById(R.id.etBody);
        mSubmit = findViewById(R.id.btnCreatePost);
        mType = getIntent().getStringExtra("type");
        String body = getIntent().getStringExtra("title");
        String title = getIntent().getStringExtra("body");

        switch (mType){
            case "create":
                mSubmit.setText("SUBMIT POST");
                mDisplayText.setText("Create New Post");

                break;
            case "edit":
                mSubmit.setText("EDIT POST");
                mDisplayText.setText("Edit Post");
                mTitle.setText(title);
                mBody.setText(body);
                break;
        }

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (mType){
                    case "create":
                        if(TextUtils.isEmpty(mTitle.getText().toString()) &&  TextUtils.isEmpty(mBody.getText().toString())){
                            new KAlertDialog(view.getContext(), KAlertDialog.WARNING_TYPE)
                                    .setTitleText("Error")
                                    .setContentText("Some fields are empty!")
                                    .setConfirmText("Ok")
                                    .show();

                        }
                        else{
                            sendPost();
                        }
                        break;

                    case "edit":
                        editPost();
                        break;
                }



            }
        });
        



    }

    private void editPost() {
        Post editpost = new Post();
        editpost.setTitle(mTitle.getText().toString());
        editpost.setBody(mBody.getText().toString());
        int id = 22;
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        Call<ResponseBody> call = apiClient.editPost(id, editpost);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                new KAlertDialog(NewPost.this, KAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success")
                        .setContentText("edit successfull")
                        .setConfirmText("Ok")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                Intent intent = new Intent(NewPost.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new KAlertDialog(NewPost.this, KAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText(t.getMessage())
                        .setConfirmText("Ok")
                        .show();

            }
        });
    }

    private void sendPost() {
        Post sendpost = new Post();
        sendpost.setTitle(mTitle.getText().toString());
        sendpost.setBody(mBody.getText().toString());
        mSubmit.setText("Submit Post");
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        Call<Post> call = apiClient.sendPost(sendpost);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                new KAlertDialog(NewPost.this, KAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success")
                        .setContentText("Your post was successfully submitted!")
                        .setConfirmText("Ok")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog kAlertDialog) {
                                Intent intent = new Intent(NewPost.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
                mBody.setText("");
                mTitle.setText("");

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                new KAlertDialog(NewPost.this, KAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText(t.getMessage())
                        .setConfirmText("Ok")
                        .show();

            }
        });
    }
}
