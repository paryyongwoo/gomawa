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

import com.gomawa.R;
import com.gomawa.adapter.NoticeListViewAdapter;
import com.gomawa.dto.NoticeItem;
import com.gomawa.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends Activity {
    // Adapter
    private NoticeListViewAdapter noticeListViewAdapter = null;

    // Header - Title & Back Button
    private ImageButton backBtn = null;
    private TextView titleTextView = null;

    // Body - List View
    private ListView listView = null;

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
        // question: Item 가져온 이후의 작업은 꼭 onResponse 안에서 해야하나?... 밖에 빼놓으니 null 값이 들어간다.
        new RequestApi().execute();
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

        // ListView 에 Adapter 연결 & Visibility 설정
        listView = findViewById(R.id.activity_notice_listView);
        noticeListViewAdapter = new NoticeListViewAdapter(this);
        listView.setAdapter(noticeListViewAdapter);

        listView.setVisibility(View.VISIBLE);

        // dsc TextView 연결 & Visibility 설정
        linearLayout = findViewById(R.id.activity_notice_linearLayout);
        linearLayout.setVisibility(View.INVISIBLE);
        dscTextView = findViewById(R.id.activity_notice_dsc);
        dscDateTextView = findViewById(R.id.activity_notice_dsc_date);
    }

    private class RequestApi extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            Call<List<NoticeItem>> call = RetrofitHelper.getInstance().getRetrofitService().getNoticeAll();
            Callback<List<NoticeItem>> callback = new Callback<List<NoticeItem>>() {
                @Override
                public void onResponse(Call<List<NoticeItem>> call, Response<List<NoticeItem>> response) {
                    if(response.isSuccessful()) {
                        // DB 에서 받아온 공지사항 Item List 와 그 List 의 Size
                        List<NoticeItem> noticeItems = response.body();
                        int noticeItemsCnt = noticeItems.size();

                        // Adapter 의 List 에 받아온 List 를 추가
                        for(int i=0; i<noticeItemsCnt; i++) {
                            noticeListViewAdapter.addNoticeItem(noticeItems.get(i));
                        }

                        // Adapter 새로고침 메소드
                        noticeListViewAdapter.notifyDataSetChanged();
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

    // question: 이 함수 과연 괜찮은가?
    public void selectItem(int i) {
        // 선택한 아이템의 id 값으로 데이터를 가져옴
        NoticeItem selectedItem = (NoticeItem) noticeListViewAdapter.getItem(i);
        String title = selectedItem.getTitle();
        String dsc = selectedItem.getDsc();
        String date = selectedItem.getDate();

        // 각 View 에 데이터 입력
        titleTextView.setText(title);
        dscTextView.setText(dsc);
        dscDateTextView.setText(date);

        // 기존의 리스트 뷰를 숨김
        listView.setVisibility(View.INVISIBLE);

        // Linear Layout 보여줌
        linearLayout.setVisibility(View.VISIBLE);
    }

    // 백 버튼 눌렀을 때 호출되는 메소드
    private void backFunc() {
        // 분기점으로 삼을 리스트 뷰의 Visibility 속성 값
        int visibilityOfListView = listView.getVisibility();

        if(visibilityOfListView == View.VISIBLE) {
            // 리스트 뷰가 보여지고 있다면 ~ 액티비티 종료
            finish();
        } else if(visibilityOfListView == View.INVISIBLE) {
            // 리스트 뷰가 보여지지 않고 있다면 ~ dsc View 가 보여지고 있음 ~ 리스트 뷰로 돌아감
            linearLayout.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);

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
