package com.app.restapi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.app.restapi.model.Post;
import com.app.restapi.network.ApiClient;
import com.app.restapi.network.RetrofitInstance;
import com.developer.kalert.KAlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerPosts;
    private PostsAdapter mAdapter;
    private KAlertDialog mPDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerPosts = findViewById(R.id.rvPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerPosts.setLayoutManager(linearLayoutManager);

        mPDialog = new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
        mPDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mPDialog.setTitleText("Loading");
        mPDialog.setCancelable(false);
        mPDialog.show();
        loadPosts();

        if(!isConnected()){
            new KAlertDialog(MainActivity.this, KAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("this app requires an internet connection!")
                    .setConfirmText("Exit")
                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog sDialog) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .show();
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewPost.class);
                String type = "create";
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    private void loadPosts() {
        ApiClient apiClient = RetrofitInstance.getRetrofitInstance().create(ApiClient.class);
        Call<Post> call = apiClient.getAllPosts();

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                mPDialog.dismiss();
                ArrayList<Post> postArrays = new ArrayList<Post>(response.body().getData());
                mAdapter = new PostsAdapter(postArrays);
                mRecyclerPosts.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
