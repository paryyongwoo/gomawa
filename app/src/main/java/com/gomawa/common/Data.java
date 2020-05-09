package com.gomawa.common;

import android.util.Log;

import com.gomawa.dto.DailyThanks;
import com.gomawa.dto.Member;
import com.gomawa.dto.ShareItem;
import com.gomawa.fragment.FragmentShareList;
import com.gomawa.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Data {
    /**
     * 멤버
     */
    private static Member member;

    public static void setMember(Member m) {
        member = m;
    }
    public static Member getMember() {
        return member;
    }

    /**
     * DailyThanks
     */
    private static DailyThanks dailyThanks;

    public static void setDailyThanks(DailyThanks d) {
        dailyThanks = d;
    }
    public static DailyThanks getDailyThanks() {
        return dailyThanks;
    }

    // All ShareItem List
    private static List<ShareItem> shareItemList = new ArrayList<>();

    public static List<ShareItem> getShareItemList() {
        return shareItemList;
    }
    public static void setShareItemList(List<ShareItem> shareItemList) {
        Data.shareItemList = shareItemList;
    }

    public static void getShareItemApi(final FragmentShareList mFragment, final int paramPage, final boolean doScroll) {
        Call<List<ShareItem>> call = RetrofitHelper.getInstance().getRetrofitService().getShareItemAll(Data.getMember().getId(), paramPage);
        Callback<List<ShareItem>> callback = new Callback<List<ShareItem>>() {
            @Override
            public void onResponse(Call<List<ShareItem>> call, Response<List<ShareItem>> response) {
                if(response.isSuccessful()) {
                    // 0 페이지를 불러올 때, 기존의 shareItem List 초기화
                    if(paramPage == 0) {
                        shareItemList.clear();
                    }

                    // Response 에서 받아온 List
                    List<ShareItem> shareItemListReceived = response.body();

                    if(shareItemListReceived == null) {
                        // 받은 데이터가 NULL 일 때
                        Log.d("ShareItems is Null", "");
                    } else {
                        for (ShareItem shareItemReceived : shareItemListReceived) {
                            // 받은 데이터를 shareItemList 에 옮겨담는 작업
                            shareItemList.add(shareItemReceived);
                        }

                        // 스크롤
                        mFragment.refreshList(paramPage, doScroll);
                    }
                } else {
                    Log.d("api 응답은 왔으나 실패", "status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ShareItem>> call, Throwable t) {
                Log.d("api 로그인 통신 실패", t.getMessage());
            }
        };
        call.enqueue(callback);
    }

    // My ShareItem List
    private static List<ShareItem> myShareItemList = new ArrayList<>();

    public static List<ShareItem> getMyShareItemList() {
        return myShareItemList;
    }
    public static void setMyShareItemList(List<ShareItem> myShareItemList) {
        Data.myShareItemList = myShareItemList;
    }

    // TODO: 2020-05-06 memberKey 를 memberID 로 바꾸기 / page 추가
    public static void getShareItemByMemberKeyApi(final FragmentShareList mFragment) {
        Call<List<ShareItem>> call = RetrofitHelper.getInstance().getRetrofitService().getShareItemByMemberKey(member.getKey());
        Callback<List<ShareItem>> callback = new Callback<List<ShareItem>>() {
            @Override
            public void onResponse(Call<List<ShareItem>> call, Response<List<ShareItem>> response) {
                if (response.isSuccessful()) {
                    // 기존의 shareItem List 초기화
                    myShareItemList.clear();

                    // Response 에서 받아온 List
                    List<ShareItem> shareItemListReceived = response.body();

                    if (shareItemListReceived == null) {
                        // 받은 데이터가 NULL 일 때
                        Log.d("ShareItems is Null", "");
                    } else {
                        for (ShareItem shareItemReceived : shareItemListReceived) {
                            // 받은 데이터를 shareItemList 에 옮겨담는 작업
                            myShareItemList.add(shareItemReceived);
                        }

                        mFragment.refreshList(0, true);
                    }
                } else {
                    Log.d("api 응답은 왔으나 실패", "status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ShareItem>> call, Throwable t) {
                Log.d("api 로그인 통신 실패", t.getMessage());
            }
        };
        call.enqueue(callback);
    }

    // Like ShareItem List
    private static List<ShareItem> likeShareItemList = new ArrayList<>();

    public static List<ShareItem> getLikeShareItemList() {
        return likeShareItemList;
    }
    public static void setLikeShareItemList(List<ShareItem> likeShareItemList) {
        Data.likeShareItemList = likeShareItemList;
    }

    // TODO: 2020-05-06 page 추가
    public static void getShareItemByLikeApi(final FragmentShareList mFragment) {
        Call<List<ShareItem>> call = RetrofitHelper.getInstance().getRetrofitService().getShareItemByLike(member.getId());
        Callback<List<ShareItem>> callback = new Callback<List<ShareItem>>() {
            @Override
            public void onResponse(Call<List<ShareItem>> call, Response<List<ShareItem>> response) {
                if (response.isSuccessful()) {
                    // 기존의 shareItem List 초기화
                    likeShareItemList.clear();

                    // Response 에서 받아온 List
                    List<ShareItem> shareItemListReceived = response.body();

                    if (shareItemListReceived == null) {
                        // 받은 데이터가 NULL 일 때
                        Log.d("ShareItems is Null", "");
                    } else {
                        for (ShareItem shareItemReceived : shareItemListReceived) {
                            // 받은 데이터를 shareItemList 에 옮겨담는 작업
                            likeShareItemList.add(shareItemReceived);
                        }

                        mFragment.refreshList(0, true);
                    }
                } else {
                    Log.d("api 응답은 왔으나 실패", "status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ShareItem>> call, Throwable t) {
                Log.d("api 로그인 통신 실패", t.getMessage());
            }
        };
        call.enqueue(callback);
    }
}
