package com.protechgene.android.bpconnect.di.module;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.protechgene.android.bpconnect.BuildConfig;
import com.protechgene.android.bpconnect.Utils.NetworkUtil;
import com.protechgene.android.bpconnect.data.remote.ApiInterface;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestModule {


    Gson provideGson() {
        GsonBuilder builder =
                new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return builder.setLenient().create();
    }

    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {

        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    Interceptor provideInterceptor(Context context) {

        Interceptor interceptor = chain -> {
            Request originalRequest = chain.request();

            Request.Builder builder = originalRequest.newBuilder();
            builder.header("Authorization", Credentials.basic("aUsername", "aPassword"));
            if(NetworkUtil.isNetworkAvailable(context)) {
                builder.header("Cache-Control", "public, max-age=" + 5);
            }else
            {
                builder.header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7);
            }

            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        };

        return interceptor;
    }

    Cache provideCache(Context context) {
        File httpCacheDirectory = new File(context.getCacheDir(), "offlineCache");
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024); //10 MB
        return cache;
    }


    ApiInterface provideApiCallInterface(Retrofit retrofit) {
        return retrofit.create(ApiInterface.class);
    }

    OkHttpClient provideOkHttpClient(Context context) {

        //Header,Cache Intercepter
        File httpCacheDirectory = new File(context.getCacheDir(), "offlineCache");
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024); //10 MB

        Interceptor cacheIntercepter = chain -> {
            Request originalRequest = chain.request();

            Request.Builder builder = originalRequest.newBuilder();
            builder.header("Authorization", Credentials.basic("aUsername", "aPassword"));
            if(NetworkUtil.isNetworkAvailable(context)) {
                builder.header("Cache-Control", "public, max-age=" + 5);
            }else
            {
                builder.header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7);
            }

            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        };

        //Logging Intercepter
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(cacheIntercepter)
                .addInterceptor(loggingInterceptor)
                .cache(cache)
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS);

        return httpClient.build();
    }



}
