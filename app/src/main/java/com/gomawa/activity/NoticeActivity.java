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

    // Body - dsc View
    private LinearLayout linearLayout = null;
    private TextView dscTextView = null;
    private TextView dscDateTextView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        initView();

        // DB에서 공지사항 Item 을 전부 가져와서 Adapter에 넘겨준 다음 화면에 띄워줌.
        new GetNoticeAllApi().execute();
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
                backFunc();
            }
        });

        // Recylcer View
        recyclerView = findViewById(R.id.activity_notice_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewAdapter = new NoticeRecyclerViewAdapter(noticeItemList, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.setVisibility(View.VISIBLE);

        // dsc TextView 연결 & Visibility 설정
        linearLayout = findViewById(R.id.activity_notice_linearLayout);
        linearLayout.setVisibility(View.INVISIBLE);
        dscTextView = findViewById(R.id.activity_notice_dsc);
        dscDateTextView = findViewById(R.id.activity_notice_dsc_date);
    }

    private class GetNoticeAllApi extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
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

            return null;
        }
    }

    public void selectItem(NoticeItem noticeItemSelected) {
        String title = noticeItemSelected.getTitle();
        String dsc = noticeItemSelected.getDsc();
        Date regDate = noticeItemSelected.getRegDate();
        String regDateStr = CommonUtils.convertFromDateToString(regDate);

        // 각 View 에 데이터 입력
        titleTextView.setText(title);
        dscTextView.setText(dsc);
        dscDateTextView.setText(regDateStr);

        // 기존의 리스트 뷰를 숨김
        recyclerView.setVisibility(View.INVISIBLE);

        // Linear Layout 보여줌
        linearLayout.setVisibility(View.VISIBLE);
    }

    // 백 버튼 눌렀을 때 호출되는 메소드
    private void backFunc() {
        // 분기점으로 삼을 리스트 뷰의 Visibility 속성 값
        int visibilityOfListView = recyclerView.getVisibility();

        if(visibilityOfListView == View.VISIBLE) {
            // 리사이클러 뷰가 보여지고 있다면 ~ 액티비티 종료
            finish();
        } else if(visibilityOfListView == View.INVISIBLE) {
            // 리사이클러 뷰가 보여지지 않고 있다면 ~ dsc View 가 보여지고 있음 ~ 리사이클러 뷰로 돌아감
            linearLayout.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

            // Header - Title Text 값 돌려받기 & dsc View 의 데이터 삭제
            titleTextView.setText("공지사항");
            dscTextView.setText("");
            dscDateTextView.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        backFunc();
    }
}
