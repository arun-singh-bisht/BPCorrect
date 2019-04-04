package com.protechgene.android.bpconnect.data.remote;


import com.protechgene.android.bpconnect.data.remote.responseModels.User;
import com.protechgene.android.bpconnect.data.remote.responseModels.oauth.OauthResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {


    @GET("ProtechSentinel/oauth/token")
    Observable<OauthResponse> oauth(@Query("grant_type") String grant_type, @Query("client_id") String client_id, @Query("username") String username, @Query("password") String password);

    @POST("login/user")
    Observable<User> login(@Query("api_key") String apiKey, @Query("email") String email, @Query("password") String password);

    @POST("signUp/user")
    Observable<User> signUp(@Query("api_key") String apiKey, @Query("email") String email, @Query("password") String password);

    @POST("resetPassword/user")
    Observable<User> resetPassword(@Query("api_key") String apiKey, @Query("email") String email);
}
