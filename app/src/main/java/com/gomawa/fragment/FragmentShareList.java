package com.gomawa.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gomawa.R;
import com.gomawa.adapter.ShareRecyclerViewAdapter;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.Constants;
import com.gomawa.dto.ShareItem;
import com.gomawa.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentShareList extends Fragment {
    // ALL or MY
    private int type;

    private ViewGroup rootView = null;

    private ArrayList<ShareItem> shareItemList = new ArrayList<>();

    // Recycler View
    private RecyclerView recyclerView = null;

    // Recycler View Layout Manager
    private LinearLayoutManager layoutManager = null;

    // Recycler View Adapter
    private ShareRecyclerViewAdapter shareRecyclerViewAdapter = null;

    // initView 가 실행되었는지 체크하는 스위치
    private boolean initViewCheck = false;

    // 스와이프 새로고침 레이아웃
    private SwipeRefreshLayout swipeRefreshLayout = null;

    // 이 리스트가 All 인 지 MY 인 지를 가져오기 위한 생성자
    public FragmentShareList(int type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_share_list, container, false);

        executeRequestApi();

        // Task 후 return 함수
        return rootView;
    }

    public void executeRequestApi() {
        if(type == Constants.ALL_LIST) {
            // 모든 게시물 보기
            new RequestApi().execute();
        } else if(type == Constants.MY_LIST) {
            // 나의 게시물 보기
            new RequestApi().execute(CommonUtils.getMember().getKey());
        }
    }

    private void initView() {
        Log.d("initView 실행", "정상입니다");

        swipeRefreshLayout = rootView.findViewById(R.id.share_recycler_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeRequestApi();
            }
        });

        // 레이아웃 매니저 설정
        recyclerView = rootView.findViewById(R.id.share_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        Log.d("initView: ", String.valueOf(shareItemList.size()));

        // Adapter 설정
        shareRecyclerViewAdapter = new ShareRecyclerViewAdapter(shareItemList);
        recyclerView.setAdapter(shareRecyclerViewAdapter);

        // 다음 RequestApi 부터는 initView 를 실행하지 않음
        initViewCheck = true;
    }

    // 게시글을 가져오는 Task
    private class RequestApi extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            Long memberKey;
            Call<List<ShareItem>> call = null;

            if(type == Constants.ALL_LIST) {
                // 모든 게시물 보기
                call = RetrofitHelper.getInstance().getRetrofitService().getShareItemAll(CommonUtils.getMember().getId());
            } else if(type == Constants.MY_LIST) {
                // 나의 게시물 보기

                // Index 0: Long memberKey
                memberKey = (Long) objects[0];

                call = RetrofitHelper.getInstance().getRetrofitService().getShareItemByMemberKey(memberKey);
            }

            Callback<List<ShareItem>> callback = new Callback<List<ShareItem>>() {
                @Override
                public void onResponse(Call<List<ShareItem>> call, Response<List<ShareItem>> response) {
                    if(response.isSuccessful()) {
                        // add 를 해주기 위해 먼저 ShareItem List 를 Clear 해줌
                        shareItemList.clear();

                        // Response 에서 받아온 List
                        List<ShareItem> shareItemsReceived = response.body();

                        if(shareItemsReceived == null) {
                            // 받은 데이터가 NULL 일 때
                            Log.d("ShareItems is Null", "");
                        } else {
                            int size = shareItemsReceived.size();

                            for(int i=0; i<size; i++) {
                                // 받은 데이터를 shareItemList 에 옮겨담는 작업
                                shareItemList.add(shareItemsReceived.get(i));
                            }

                            // initView 가 실행된 적이 있다면 initView 는 다시 실행하지 않음
                            if(initViewCheck == false) {
                                initView();
                            }

                            // 정보 갱신
                            shareRecyclerViewAdapter.notifyDataSetChanged();

                            // 최상단으로 스크롤
                            recyclerView.smoothScrollToPosition(0);

                            // 새로고침 아이콘 제거
                            swipeRefreshLayout.setRefreshing(false);
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

            try{
                call.enqueue(callback);
            } catch(NullPointerException e) {
                Log.d("ListFragment Type 분류 실패", e.getMessage());
            }


            return null;
        }
    }
}
