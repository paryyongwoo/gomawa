package com.gomawa.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gomawa.R;
import com.gomawa.activity.NoticeActivity;
import com.gomawa.common.CommonUtils;
import com.gomawa.dto.NoticeItem;

import java.util.Date;
import java.util.List;

public class NoticeRecyclerViewAdapter extends RecyclerView.Adapter<NoticeRecyclerViewAdapter.NoticeRecyclerViewHolder> {
    // Context
    private Context mContext = null;

    // 공지사항 리스트
    private List<NoticeItem> noticeItemList;

    // 생성자 - 리스트 초기화, Activity 가져오기
    public NoticeRecyclerViewAdapter(List<NoticeItem> noticeItemList) {
        this.noticeItemList = noticeItemList;
    }

    // 뷰 홀더
    public class NoticeRecyclerViewHolder extends RecyclerView.ViewHolder {
        // NoticeItem 의 전체 레이아웃 ( Listener )
        LinearLayout linearLayout;

        TextView titleTextView;
        TextView regDateTextView;
        TextView contentTextView;

        public NoticeRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.recyclerView_item_notice_layout);

            titleTextView = itemView.findViewById(R.id.recyclerView_item_notice_title);
            regDateTextView = itemView.findViewById(R.id.recyclerView_item_notice_date);
            contentTextView = itemView.findViewById(R.id.recyclerView_item_notice_content);
        }
    }

    @NonNull
    @Override
    public NoticeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_notice, parent, false);

        NoticeRecyclerViewHolder viewHolder = new NoticeRecyclerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeRecyclerViewHolder holder, int position) {
        final NoticeItem noticeItemSelected = noticeItemList.get(position);

        // 공지사항 제목
        String title = noticeItemSelected.getTitle();
        holder.titleTextView.setText(title);

        // 공지사항 날짜
        Date regDate = noticeItemSelected.getRegDate();
        String regDateStr = CommonUtils.convertFromDateToString(regDate, "YYYY.MM.dd");
        holder.regDateTextView.setText(regDateStr);

        // 공지사항 본문
        String content = noticeItemSelected.getDsc();
        holder.contentTextView.setText(content);
    }

    @Override
    public int getItemCount() {
        return noticeItemList.size();
    }
}
