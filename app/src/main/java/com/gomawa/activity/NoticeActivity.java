package com.gomawa.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.gomawa.R;
import com.gomawa.adapter.NoticeListViewAdapter;
import com.gomawa.dto.NoticeItem;
import com.gomawa.network.RetrofitHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends Activity {
    // 공지사항 Item 을 담아두는 List
    private List<NoticeItem> noticeItems = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        initView();

        // DB에서 공지사항 Item 을 전부 가져와서 Adapter에 넘겨준 다음 화면에 띄워줌.
        // question: Item 가져온 이후의 작업은 꼭 onResponse 안에서 해야하나?... 밖에 빼놓으니 null 값이 들어간다.
        new RequestApi().execute();
    }

    private void initView() {
        // 액티비티의 타이틀
        TextView titleTextView = findViewById(R.id.activity_notice_title);
        titleTextView.setText("공지사항");

        // 뒤로가기 버튼
        ImageButton backBtn = findViewById(R.id.activity_notice_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class RequestApi extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            // question: List<NoticeItem> 이 들어가는 게 맞나?
            Call<List<NoticeItem>> call = RetrofitHelper.getInstance().getRetrofitService().getNoticeAll();
            Callback<List<NoticeItem>> callback = new Callback<List<NoticeItem>>() {
                @Override
                public void onResponse(Call<List<NoticeItem>> call, Response<List<NoticeItem>> response) {
                    if(response.isSuccessful()) {
                        // DB 에서 받아온 공지사항 Item List
                        noticeItems = response.body();

                        Log.d("@@@@@ noticeItems : ", noticeItems.toString());

                        // 공지사항 Item 갯수
                        int noticeItemsCount = noticeItems.size();

                        // ListView 에 Adapter 연결
                        ListView listView = findViewById(R.id.activity_notice_listView);
                        NoticeListViewAdapter noticeListViewAdapter = new NoticeListViewAdapter(noticeItems, noticeItemsCount);
                        listView.setAdapter(noticeListViewAdapter);
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
