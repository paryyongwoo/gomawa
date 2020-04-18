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

import com.gomawa.R;
import com.gomawa.adapter.ShareRecyclerViewAdapter;
import com.gomawa.dto.ShareItem;
import com.gomawa.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentShareList extends Fragment {
    private ViewGroup rootView = null;

    private ArrayList<ShareItem> shareItemList = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_share_list, container, false);

        // 모든 게시글을 가져오는 Task
        executeRequestApi();

        // Task 후 return 함수
        return rootView;
    }

    private void initView() {
        // 레이아웃 매니저 설정
        RecyclerView recyclerView = rootView.findViewById(R.id.share_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        Log.d("initView: ", String.valueOf(shareItemList.size()));

        // Adapter 설정
        ShareRecyclerViewAdapter adapter = new ShareRecyclerViewAdapter(shareItemList);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    public void executeRequestApi() {
        new RequestApi().execute();
    }

    // 모든 게시글을 가져오는 Task
    private class RequestApi extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            Call<List<ShareItem>> call = RetrofitHelper.getInstance().getRetrofitService().getShareItemAll();
            Callback<List<ShareItem>> callback = new Callback<List<ShareItem>>() {
                @Override
                public void onResponse(Call<List<ShareItem>> call, Response<List<ShareItem>> response) {
                    if(response.isSuccessful()) {
                        // shareItemList 초기화
                        shareItemList = new ArrayList<ShareItem>();

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

                            initView();
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

            return null;
        }
    }
}
