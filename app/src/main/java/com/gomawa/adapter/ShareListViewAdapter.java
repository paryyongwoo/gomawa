package com.gomawa.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gomawa.R;
import com.gomawa.common.CommonUtils;
import com.gomawa.dto.ShareItem;
import com.gomawa.network.RetrofitHelper;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareListViewAdapter extends BaseAdapter {

    ShareListViewAdapter shareListViewAdapter = this;
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<ShareItem> shareItemList = null;

    // 좋아요 수 표시해주는 텍스트뷰
    TextView likeNumTextView = null;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.listview_item_share, null);

        ShareItem shareItemPicked = shareItemList.get(position);

        // Glide로 프로필 이미지 표시하기 ( 지금은 더미 )
        ImageView profileImageView = (ImageView)view.findViewById(R.id.share_profile);
        String imageUrl = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png";
        Glide.with(mContext).load(imageUrl).into(profileImageView);

        // Picasso로 배경 이미지 표시하기
        ImageView backgroundImageView = view.findViewById(R.id.listview_item_share_background_imageview);
        String backgroundImageUrl = shareItemPicked.getBackgroundUrl();
        Log.d("### backGroundUrl : ", backgroundImageUrl);
        Picasso.get().load(backgroundImageUrl).into(backgroundImageView);

        // 닉네임 표시
        TextView nickNameTextView = view.findViewById(R.id.share_nickname);
        String nickName = shareItemPicked.getMember().getNickName();
        nickNameTextView.setText(nickName);

        // 날짜 표시
        TextView dateTextView = view.findViewById(R.id.share_date);
        String date = shareItemPicked.getDate().toString();
        dateTextView.setText(date);

        // 본문 표시
        TextView contentTextView = view.findViewById(R.id.listview_item_share_body_textview);
        String content = shareItemPicked.getContent();
        contentTextView.setText(content);

        // 좋아요 버튼 Listener
        ImageButton likeBtn = view.findViewById(R.id.listview_item_share_like_button);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RequestApi().execute(position);
            }
        });

        // TODO: 2020-04-16 좋아요 클릭 시 버튼 리스너 변경과 새로운 API 생성 
        // 좋아요 수 표시
        likeNumTextView = view.findViewById(R.id.listview_item_share_like_num);
        likeNumTextView.setText(String.valueOf(shareItemList.get(position).getLikeNum()));

        return view;
    }

    private class RequestApi extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            // Index 0: int position
            final int position = (int) objects[0];

            // 선택된 Item 의 id 값을 가져옴
            Long shareItemId = shareItemList.get(position).getId();
            // 유저의 key 값을 가져옴
            Long memberKey = CommonUtils.getMember().getKey();

            Call<ShareItem> call = RetrofitHelper.getInstance().getRetrofitService().updateLike(shareItemId, memberKey);
            Callback<ShareItem> callback = new Callback<ShareItem>() {
                @Override
                public void onResponse(Call<ShareItem> call, Response<ShareItem> response) {
                    if(response.isSuccessful()) {
                        ShareItem shareItemReceived = response.body();

                        shareItemList.set(position, shareItemReceived);

                        // 리스트뷰 갱신
                        shareListViewAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("api 응답은 왔으나 실패", "status: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ShareItem> call, Throwable t) {
                    Log.d("api 로그인 통신 실패", t.getMessage());
                }
            };
            call.enqueue(callback);

            return null;
        }
    }
}
