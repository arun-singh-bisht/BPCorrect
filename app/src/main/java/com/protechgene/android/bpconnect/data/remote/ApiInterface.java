package com.protechgene.android.bpconnect.data.remote;


import com.protechgene.android.bpconnect.data.remote.models.User;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {


    //@GET("movie/top_rated")
    //Observable<MovieResponse> getTopRatedMovie(@Query("api_key") String apiKey);

    @POST("login/user")
    Observable<User> login(@Query("api_key") String apiKey, @Query("email") String email, @Query("password") String password);

}
