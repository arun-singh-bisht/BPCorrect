package com.protechgene.android.bpconnect.data.remote;


import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.BpReadingsResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.ResetPassword.ResetPasswordResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.oauth.OauthResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.profile.ProfileResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {


    @GET("ProtechSentinel/oauth/token")
    Observable<OauthResponse> oauth(@Query("grant_type") String grant_type, @Query("client_id") String client_id, @Query("username") String username, @Query("password") String password);

    @POST("ProtechSentinel/basic/forgot/password")
    Observable<ResetPasswordResponse> resetPassword(@Query("email") String email);

    @POST("ProtechSentinel/common/register/patient")
    Observable<ResetPasswordResponse> signUp(@Query("email") String email, @Query("password") String password);

    @GET("ProtechSentinel/common/get/patient/information")
    Observable<ProfileResponse> profileDetails(@Query("access_token") String access_token, @Query("patientUserId") String patientUserId);

    @POST("ProtechSentinel/common/update/patient")
    Observable<ProfileResponse> updateProfile(@Query("access_token") String access_token, @Query("userId") String userId, @Query("firstname") String firstname,@Query("gender") String gender,@Query("dob") String dob,@Query("mobile1") String mobile1,@Query("address1") String address1);

    @GET("ProtechSentinel/common/get/chart/data/according/to/user")
    Observable<BpReadingsResponse> getBpReadings(@Query("access_token") String access_token, @Query("patientUserId") String patientUserId, @Query("fromdate") String fromdate, @Query("todate") String todate, @Query("dayno") String dayno);
}
