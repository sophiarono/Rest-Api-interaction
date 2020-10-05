package com.app.restapi.network;

import com.app.restapi.model.Comments;
import com.app.restapi.model.Post;
import com.app.restapi.model.SinglePost;
import com.google.gson.annotations.SerializedName;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiClient {

    @GET("posts")
    Call<Post> getAllPosts();
    @GET("comments")
    Call<Comments> getAllComments();

    String POST_ROUTE = "posts";
    @Headers({"Content-type: application/json"})
    @POST(POST_ROUTE)
    Call<Post> sendPost(@Body Post post);
    @DELETE("posts/{id}")
    Call<ResponseBody> deletePost(@Path("id") int id);
    @PATCH("posts/{id}")
    Call<ResponseBody> editPost(@Path("id") int id, @Body Post post);
    @DELETE("comments/{id}")
    Call<ResponseBody> deleteComment(@Path("id") int commentId);
    @PATCH("comments/like/{commentId}")
    Call<ResponseBody> commentLike(@Path("commentId") int commentId,@Body Comments commentlike);
    @POST("comments/post/{postId}")
    Call<ResponseBody> addComment(@Path("postId") int postId, @Body Comments comments);
    @GET("posts/{postId}")
    Call<SinglePost> getSinglePost(@Path("postId") int postId);

}
