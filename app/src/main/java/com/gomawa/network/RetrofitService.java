package com.gomawa.network;

import com.gomawa.dto.DailyThanks;
import com.gomawa.dto.Member;
import com.gomawa.dto.ShareItem;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitService {
    //POST
    @Headers("Accept: application/json")
    @POST("/api/member")
    Call<Member> addMemberOnStart(@Body Member memberParam);

    @Headers("Accept: application/json")
    @POST("/api/dailyThanks")
    Call<DailyThanks> addDailyThanks(@Body DailyThanks dailyThanks);

    @Headers("Accept: application/hal+json")
    @Multipart
    @POST("/api/shareItem")
    Call<String> addShareItem(@Body ShareItem shareItem, @Part MultipartBody.Part file);



    // PUT
    @Headers("Accept: application/json")
    @PUT("/api/nickName")
    Call<Member> setNickName(@Body Member memberParam);
}
