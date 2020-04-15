package com.gomawa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gomawa.R;
import com.gomawa.activity.NoticeActivity;
import com.gomawa.dto.NoticeItem;

import java.util.ArrayList;
import java.util.List;

public class NoticeListViewAdapter extends BaseAdapter {
    // Context Activity
    private Context mContext = null;
    private NoticeActivity mActivity = null;

    // Inflater
    private LayoutInflater inflater = null;

    // 공지사항 Item List
    private List<NoticeItem> noticeItemList = new ArrayList<NoticeItem>();
    private int listCnt = 0;

    // LinearLayout
    private LinearLayout linearLayout;

    // TextViews
    private TextView titleTextView;
    private TextView dateTextView;

    public NoticeListViewAdapter(NoticeActivity mActivity) { this.mActivity = mActivity; }

    @Override
    public int getCount() {
        return listCnt;
    }

    @Override
    public Object getItem(int i) {
        return noticeItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        // 이게 뭔지 모르겠음
        return 0;
    }

    public void addNoticeItem(NoticeItem noticeItem) {
        noticeItemList.add(noticeItem);
        listCnt++;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        // 이 부분은 거의 복사붙여넣기 한 부분이라 제대로 이해를 못했음
        // if문 안쪽은 fragment inflate 하는 거랑 거의 똑같은 거 보면 레이아웃 연결해주는 건 확실함
        // 이 함수 자체는 Item 들 하나하나의 뷰를 설정해주는 듯
        if(view == null) {
            mContext = viewGroup.getContext();

            if(inflater == null) {
                inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            view = inflater.inflate(R.layout.listview_item_notice, viewGroup, false);
        }

        titleTextView = view.findViewById(R.id.listview_item_notice_title);
        titleTextView.setText(noticeItemList.get(i).getTitle());

        dateTextView = view.findViewById(R.id.listview_item_notice_date);
        dateTextView.setText(noticeItemList.get(i).getDate());

        linearLayout = view.findViewById(R.id.listview_item_notice_linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // question: 이렇게 접근해도 괜찮은가?
                mActivity.selectItem(i);
            }
        });

        return view;
    }
}
