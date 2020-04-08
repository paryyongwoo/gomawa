package com.gomawa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gomawa.R;
import com.gomawa.dto.NoticeItem;

import java.util.List;

public class NoticeListViewAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;

    private List<NoticeItem> noticeItemList = null;
    private int listCnt = 0;

    public NoticeListViewAdapter(List<NoticeItem> noticeItemList, int listCnt) {
        this.noticeItemList = noticeItemList;
        this.listCnt = listCnt;
    }

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
        // todo: 이게 뭔지 모르겠음
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // 이 부분은 거의 복사붙여넣기 한 부분이라 제대로 이해를 못했음
        // if문 안쪽은 fragment inflate 하는 거랑 거의 똑같은 거 보면 레이아웃 연결해주는 건 확실함
        // 이 함수 자체는 Item 들 하나하나의 뷰를 설정해주는 듯
        if(view == null) {
            final Context context = viewGroup.getContext();

            if(inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            view = inflater.inflate(R.layout.listview_item_notice, viewGroup, false);
        }

        TextView titleTextView = view.findViewById(R.id.listview_item_notice_title);
        titleTextView.setText(noticeItemList.get(i).getTitle());

        TextView dateTextView = view.findViewById(R.id.listview_item_notice_date);
        dateTextView.setText(noticeItemList.get(i).getDate().toString());

        return view;
    }
}
