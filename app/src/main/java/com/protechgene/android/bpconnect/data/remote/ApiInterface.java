package com.protechgene.android.bpconnect.data.remote;


import com.google.gson.JsonArray;
import com.protechgene.android.bpconnect.data.remote.responseModels.AddBPReading.AddBpReadingResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.BpReadings.BpReadingsResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.ChangePasswordModel;
import com.protechgene.android.bpconnect.data.remote.responseModels.ResetPassword.ResetPasswordResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.cityandstate.StateCityOptions;
import com.protechgene.android.bpconnect.data.remote.responseModels.oauth.OauthResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.profile.ProfileResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.protocol.GetProtocolResponse;
import com.protechgene.android.bpconnect.data.remote.responseModels.sharereadingprovider.ShareReading;

import org.json.JSONArray;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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
    Observable<ProfileResponse> updateProfile(@Query("access_token") String access_token, @Query("userId") String userId, @Query("firstname") String firstname,@Query("lastname") String lastname,@Query("gender") String gender,@Query("dob") String dob,@Query("mobile1") String mobile1,@Query("address1") String address1,@Query("weight") String weight,@Query("height") String height,@Query("photo_url") String photo,@Query("state") String state,@Query("city") String city,@Query("zipcode") String zipcode);

    @GET("ProtechSentinel/common/get/chart/data/according/to/user")
    Observable<BpReadingsResponse> getBpReadings(@Query("access_token") String access_token, @Query("patientUserId") String patientUserId, @Query("fromdate") String fromdate, @Query("todate") String todate, @Query("dayno") String dayno);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("ProtechSentinel/common/add/patient/reading")
    Observable<AddBpReadingResponse> addBpReadings(@Query("access_token") String access_token,@Body JsonArray body);

    @POST("ProtechSentinel/common/create/ehc/protocol")
    Observable<ProfileResponse> createProtocol(@Query("access_token") String access_token, @Query("patient_id") String userId,@Query("startdate") String startdate,@Query("enddate") String enddate,@Query("protocol_id") String protocol_id,@Query("morning_alarm") String morning_alarm,@Query("evening_alarm") String evening_alarm);

    @GET("ProtechSentinel/common/get/ehc/protocol/by/patient")
    Observable<GetProtocolResponse> getProtocolDetail(@Query("access_token") String access_token, @Query("patient_id") String userId);

    @GET("ProtechSentinel/common/get/protocol/data/according/to/user")
    Observable<GetProtocolResponse> getHistoryProtocol(@Query("access_token") String access_token, @Query("patientUserId") String userId);

    // edit by sohit address select state and city and password
     @GET("ProtechSentinel/common/get/state/list")
    Observable<StateCityOptions> getOptionCity();

     @POST("ProtechSentinel/common/change/password")
     Observable<ResetPasswordResponse> changePassword(@Query("password") String password,@Query("access_token")String access_token,@Query("userId") String userid);

     @POST("ProtechSentinel/common/share/reading")
     Observable<ShareReading> getShareReading(@Query("patient_id") String userid,@Query("access_token")String accessToken,@Query("request_type") String request_type,@Query("status") Boolean status);

}
