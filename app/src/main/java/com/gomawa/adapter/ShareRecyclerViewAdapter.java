package com.gomawa.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gomawa.R;
import com.gomawa.activity.CommentActivity;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.Constants;
import com.gomawa.dto.Member;
import com.gomawa.dto.ShareItem;
import com.gomawa.network.RetrofitHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareRecyclerViewAdapter extends RecyclerView.Adapter<ShareRecyclerViewAdapter.ShareRecyclerViewHolder> {
    // this
    private ShareRecyclerViewAdapter shareRecyclerViewAdapter = this;

    // Context
    private Context mContext = null;

    // ShareItem List
    private ArrayList<ShareItem> shareItemList;

    // 생성자
    public ShareRecyclerViewAdapter(ArrayList<ShareItem> shareItemList) {
        this.shareItemList = shareItemList;
    }

    /**
     * RecyclerViewAdapter 의 진행 순서 :
     * 1. onCreateViewHolder                        viewHolder 에 inflate 한 view 를 전달
     * 2. ViewHolder (ShareRecyclerViewHolder)      view 안의 각각의 view(Button, TextView, etc.) 를 변수에 대입
     * 3. onBindViewHolder                          ViewHolder 의 멤버 변수들을 이용해 실제 데이터를 저장
     */

    /**
     * 2. ViewHolder
     */
    public class ShareRecyclerViewHolder extends RecyclerView.ViewHolder {
        // 헤더
        // 프로필 이미지
        CircleImageView profileImageView = null;
        // 닉네임
        TextView nickNameTextView = null;
        // 날짜
        TextView dateTextView = null;

        // 바디
        // 배경 이미지
        ImageView backgroundImageView = null;
        // 본문 글
        TextView contentTextView = null;
        // 좋아요 버튼
        ImageButton likeButton = null;
        // 다운로드 버튼
        ImageButton downloadButton = null;
        // 좋아요 수
        TextView likeTextView = null;

        // 바텀
        // 댓글 모두 보기
        TextView commentTextView = null;

        public ShareRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            Log.d("#####", "ShareRecyclerViewHolder 생성자 진입");

            // 헤더
            profileImageView = itemView.findViewById(R.id.share_profile);
            nickNameTextView = itemView.findViewById(R.id.share_nickname);
            dateTextView = itemView.findViewById(R.id.share_date);

            // 바디
            backgroundImageView = itemView.findViewById(R.id.listview_item_share_background_imageview);
            contentTextView = itemView.findViewById(R.id.listview_item_share_body_textview);
            likeButton = itemView.findViewById(R.id.listview_item_share_like);
            downloadButton = itemView.findViewById(R.id.listview_item_share_download);
            likeTextView = itemView.findViewById(R.id.listview_item_share_like_num);

            // 바텀
            commentTextView = itemView.findViewById(R.id.listview_item_share_comment);
        }
    }

    /**
     * 1. onCreateViewHolder
     */
    @NonNull
    @Override
    public ShareRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_item_share, parent, false);

        mContext = parent.getContext();

        ShareRecyclerViewHolder viewHolder = new ShareRecyclerViewHolder(view);

        return viewHolder;
    }

    /**
     * 3. onBindViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull ShareRecyclerViewHolder holder, final int position) {
        // ShareItem List 에서 Position Index 의 ShareItem 을 가져옴
        final ShareItem shareItemSelected = shareItemList.get(position);

        Log.d("onBindViewHolder: ", String.valueOf(position));

        /**
         * 각 View 의 변수는 이미 ViewHolder 에 선언 & 초기화 되어있으므로 매개변수로 넘어온 holder 변수를 이용해 호출할 수 있음
         */

        // 프로필 이미지 표시
        String imageUrl = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png"; // 기능 미 구현, 임시 이미지의 URL
        Glide.with(mContext).load(imageUrl).into(holder.profileImageView);

        // 닉네임 표시
        String nickName = shareItemSelected.getMember().getNickName();
        holder.nickNameTextView.setText(nickName);

        // 날짜 표시
        Date date = shareItemSelected.getRegDate();
        String dateString = CommonUtils.convertFromDateToString(date, "YYYY.MM.dd");
        holder.dateTextView.setText(dateString);

        // 배경 이미지 표시
        String backgroundImageUrl = shareItemSelected.getBackgroundUrl();
        if(backgroundImageUrl != null) {
            // DB 에서 가져온 ShareItem 의 BackgroundURL 이 null 이면 아래 코드를 진행하지 않음 - 기본 src 속성으로 지정된 drawable 이 표시됨
            Picasso.get().load(backgroundImageUrl).into(holder.backgroundImageView);
        }

        // 본문 표시
        String content = shareItemSelected.getContent();
        holder.contentTextView.setText(content);
        CommonUtils.setReadMore(holder.contentTextView, content, 5);

        // 좋아요 버튼 Listener
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LikeApi().execute(position);
            }
        });
        boolean isLike = shareItemList.get(position).getIsLike();
        Log.d("isLike : ", String.valueOf(isLike));
        if (isLike) {
            holder.likeButton.setImageResource(R.drawable.heart);
        } else {
            holder.likeButton.setImageResource(R.drawable.menu_my_thanks_icon);
        }

        // 좋아요 수 표시
        String likeNum = String.valueOf(shareItemSelected.getLikeNum());
        holder.likeTextView.setText(likeNum);

        // 댓글 모두 보기 Listener
        holder.commentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);

                // ShareItem 을 분해하는 과정...
                intent.putExtra("memberKey", shareItemSelected.getMember().getKey());
                intent.putExtra("memberEmail", shareItemSelected.getMember().getEmail());
                intent.putExtra("memberGender", shareItemSelected.getMember().getGender());
                intent.putExtra("memberNickName", shareItemSelected.getMember().getNickName());
                intent.putExtra("memberProfileImgUrl", shareItemSelected.getMember().getProfileImgUrl());
                //intent.putExtra("memberDateString", shareItemSelected.getMember().getRegDate());

                intent.putExtra("id", shareItemSelected.getId());
                intent.putExtra("content", shareItemSelected.getContent());
                intent.putExtra("backgroundUrl", shareItemSelected.getBackgroundUrl());
                intent.putExtra("likeNum", shareItemSelected.getLikeNum());
                //intent.putExtra("dateString", shareItemSelected.getDate());

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shareItemList.size();
    }

    /**
     * 좋아요 버튼 클릭 시 실행되는 Task
     */
    private class LikeApi extends AsyncTask {
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

                        Log.d("likeNum", String.valueOf(shareItemList.get(position).getLikeNum()));

                        // 리스트뷰 갱신
                        shareRecyclerViewAdapter.notifyDataSetChanged();
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
