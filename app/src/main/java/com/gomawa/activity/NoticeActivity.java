package com.gomawa.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gomawa.R;
import com.gomawa.adapter.NoticeRecyclerViewAdapter;
import com.gomawa.common.CommonUtils;
import com.gomawa.dto.NoticeItem;
import com.gomawa.dto.ShareItem;
import com.gomawa.network.RetrofitHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends Activity {
    // 공지사항 리스트
    private List<NoticeItem> noticeItemList = new ArrayList<>();

    // Header - Title & Back Button
    private ImageButton backBtn = null;
    private TextView titleTextView = null;

    // Body - Recycler View
    private RecyclerView recyclerView = null;
    private NoticeRecyclerViewAdapter recyclerViewAdapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        // DB 에서 Notice Item 가져옴
        getNoticeAllApi();

        // 뷰 설정
        initView();
    }

    private void initView() {
        // 액티비티의 타이틀
        titleTextView = findViewById(R.id.activity_notice_title);
        titleTextView.setText("공지사항");

        // 뒤로가기 버튼
        backBtn = findViewById(R.id.activity_notice_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Recylcer View
        recyclerView = findViewById(R.id.activity_notice_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new NoticeRecyclerViewAdapter(noticeItemList);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.setVisibility(View.VISIBLE);
    }

    private void getNoticeAllApi() {
        Call<List<NoticeItem>> call = RetrofitHelper.getInstance().getRetrofitService().getNoticeAll();
        Callback<List<NoticeItem>> callback = new Callback<List<NoticeItem>>() {
            @Override
            public void onResponse(Call<List<NoticeItem>> call, Response<List<NoticeItem>> response) {
                if(response.isSuccessful()) {
                    // DB 에서 받아온 공지사항 Item List 와 그 List 의 Size
                    List<NoticeItem> noticeItemListReceived = response.body();
                    int size = noticeItemListReceived.size();

                    for(int i=0; i<size; i++) {
                        noticeItemList.add(noticeItemListReceived.get(i));
                    }

                    // Adapter 새로고침 메소드
                    recyclerViewAdapter.notifyDataSetChanged();
                } else {
                    // todo: 예외 처리
                    Toast.makeText(getApplicationContext(), "공지사항 불러오기 실패", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<NoticeItem>> call, Throwable t) {
                Log.d("api 로그인 통신 실패", t.getMessage());
            }
        };
        call.enqueue(callback);
    }
}
