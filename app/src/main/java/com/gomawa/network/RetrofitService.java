package com.gomawa.network;

import com.gomawa.dto.Comment;
import com.gomawa.dto.DailyThanks;
import com.gomawa.dto.Member;
import com.gomawa.dto.NoticeItem;
import com.gomawa.dto.ShareItem;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
    // GET
    @GET("/api/notice")
    Call<List<NoticeItem>> getNoticeAll();

    @GET("/api/shareItems/{memberId}/{page}")
    Call<List<ShareItem>> getShareItemAll(@Path("memberId") Long memberId, @Path("page") int page);

    @GET("/api/shareItem/{memberKey}")
    Call<List<ShareItem>> getShareItemByMemberKey(@Path("memberKey") Long memberKey);

    @GET("/api/comment/{shareItemId}")
    Call<List<Comment>> getCommentByShareItemId(@Path("shareItemId") Long shareItemId);



    //POST
    @Headers("Accept: application/json")
    @POST("/api/member")
    Call<Member> addMemberOnStart(@Body Member memberParam);

    @Headers("Accept: application/json")
    @POST("/api/dailyThanks")
    Call<DailyThanks> addDailyThanks(@Body DailyThanks dailyThanks);

    /**
     * 글쓰기
     * @param file 업로드 파일
     * @param items { content: '', key: ''}
     * @return
     */
    @Headers("Accept: application/hal+json")
    @Multipart
    @POST("/api/shareItem")
    Call<ShareItem> addShareItem(@Part MultipartBody.Part file, @Part("items") RequestBody items);

    @Headers("Accept: application/json")
    @POST("/api/comment")
    Call<Comment> addComment(@Body Comment comment);

    // PUT
    @Headers("Accept: application/json")
    @PUT("/api/nickName")
    Call<Member> setNickName(@Body Member memberParam);

    @PUT("/api/like/{shareItemId}/{memberKey}")
    Call<ShareItem> updateLike(@Path("memberKey") Long memberKey, @Path("shareItemId") Long shareItemId);

    @Headers("Accept: application/hal+json")
    @Multipart
    @PUT("/api/shareItem")
    Call<Void> updateShareItem(@Part MultipartBody.Part file, @Part("items") RequestBody items);

    @Headers("Accept: application/json")
    @PUT("/api/comment")
    Call<Void> updateComment(@Body Comment comment);

    // DELETE
    @DELETE("/api/member/{key}")
    Call<Void> deleteMemberByKey(@Path("key") Long key);

    @DELETE("/api/shareItem/{shareItemId}")
    Call<Void> deleteShareItemById(@Path("shareItemId") Long shareItemId);

    @DELETE("/api/comment/{id}")
    Call<Void> deleteCommentById(@Path("id") Long id);
}
