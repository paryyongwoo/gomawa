package com.gomawa.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gomawa.R;
import com.gomawa.adapter.ShareListViewAdapter;
import com.gomawa.common.AuthUtils;
import com.gomawa.dto.Member;
import com.gomawa.dto.ShareItem;
import com.gomawa.network.RetrofitHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentShareList extends Fragment {

    private ViewGroup rootView = null;

    private ListView shareListView = null;

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
        shareListView = rootView.findViewById(R.id.share_list_view);

        // 어댑터 설정 - DB 에서 가져온 ShareItemList 를 매개변수로 보내 적용함
        ShareListViewAdapter shareListViewAdapter = new ShareListViewAdapter(getContext(), shareItemList);

        // 리스트뷰에 어댑터 연결
        shareListView.setAdapter(shareListViewAdapter);
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
                                System.out.println(shareItemsReceived.get(i).getBackgroundUrl());
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
