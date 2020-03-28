package com.gomawa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gomawa.R;
import com.gomawa.dto.ShareItem;

import java.util.ArrayList;

public class ShareListViewAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<ShareItem> shareItemList = null;

    public ShareListViewAdapter(Context context, ArrayList<ShareItem> shareItemList) {
        this.mContext = context;
        this.shareItemList = shareItemList;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return shareItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return shareItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return shareItemList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.listview_item_share, null);

        ImageView profileImageView = (ImageView)view.findViewById(R.id.share_profile);

        // Glide로 이미지 표시하기
        String imageUrl = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png";
        Glide.with(mContext).load(imageUrl).into(profileImageView);

        return view;
    }
}
