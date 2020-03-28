package com.gomawa.fragment;

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
import com.gomawa.dto.ShareItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FragmentShareList extends Fragment {

    private ViewGroup rootView = null;

    private ListView shareListView = null;

    private ArrayList<ShareItem> shareItemList = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_share_list, container, false);

        /**
         * 서버에서 리스트뷰에 뿌려줄 데이터 가져오기
         */
        try {
            initData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initView();

        return rootView;
    }

    private void initView() {
        shareListView = rootView.findViewById(R.id.share_list_view);

        // 어댑터 설정
        ShareListViewAdapter shareListViewAdapter = new ShareListViewAdapter(getContext(), shareItemList);

        // 리스트뷰에 어댑터 연결
        shareListView.setAdapter(shareListViewAdapter);
    }

    /**
     * 서버에서 리스트뷰에 뿌려줄 데이터 가져오기
     * @throws JSONException
     */
    private void initData() throws JSONException {

        // TODO: 2020-01-21 데이터베이스에서 로딩
        String jsonData = "[{ \"id\": 12, \"key\": \"2\", \"date\": \"2010-10-15T09:27:37Z\", \"profileUrl\":\"https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png\", \"content\": \"hihello\"}, { \"id\": 12, \"username\": \"admin\", \"date\": \"2010-10-15T09:27:37Z\", \"profileUrl\":\"https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png\", \"content\": \"hihello\"}]";
        JSONArray jsonArray = new JSONArray(jsonData);

        shareItemList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i += 1) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            // Date 형변환
            Date date = null;
            String dtStart = jsonObject.getString("date");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // TODO: 2020-01-21 날짜 포맷 선정
            try {
                date = format.parse(dtStart);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // shareItem 생성
            ShareItem shareItem = new ShareItem(jsonObject.getLong("id"), jsonObject.getLong("long"), date, jsonObject.getString("content"), jsonObject.getString("profileUrl"));
            Log.d("share", shareItem.getDate().toString());

            shareItemList.add(shareItem);
        }

    }
}
