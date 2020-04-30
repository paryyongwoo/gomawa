package com.gomawa.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gomawa.R;
import com.gomawa.activity.CommentActivity;
import com.gomawa.activity.ShareActivity;
import com.gomawa.activity.UpdateActivity;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.ImageUtils;
import com.gomawa.dialog.HorizontalTwoButtonDialog;
import com.gomawa.dialog.OnlyVerticalFourButtonDialog;
import com.gomawa.dialog.OnlyVerticalTwoButtonDialog;
import com.gomawa.dto.Member;
import com.gomawa.dto.ShareItem;
import com.gomawa.fragment.FragmentShareList;
import com.gomawa.network.RetrofitHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareRecyclerViewAdapter extends RecyclerView.Adapter<ShareRecyclerViewAdapter.ShareRecyclerViewHolder> {
    // this
    private ShareRecyclerViewAdapter shareRecyclerViewAdapter = this;

    // Context Activity
    private Context mContext = null;
    private Activity mActivity;

    // FragmentShareList ( getShareItemAll 사용을 위해 )
    private FragmentShareList fragmentShareList;

    // 메뉴 버튼 다이얼로그 ( 글쓴이 계정 )
    private OnlyVerticalFourButtonDialog myMenuDialog = null;

    // 메뉴 버튼 다이얼로그
    private OnlyVerticalTwoButtonDialog menuDialog = null;

    // 삭제 확인 다이얼로그
    private HorizontalTwoButtonDialog deleteDialog = null;

    // 다운로드 버튼 다이얼로그
    private OnlyVerticalTwoButtonDialog downloadShareDialog = null;

    // ShareItem List
    private ArrayList<ShareItem> shareItemList;

    // 생성자
    public ShareRecyclerViewAdapter(ArrayList<ShareItem> shareItemList, Activity mActivity, FragmentShareList fragmentShareList) {
        this.shareItemList = shareItemList;
        this.mActivity = mActivity;
        this.fragmentShareList = fragmentShareList;
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
            // 메뉴 버튼
        ImageButton menuButton = null;

        // 바디
            // 배경 이미지
        ImageView backgroundImageView = null;
            // 메뉴
                // 좋아요 버튼
        ImageButton likeButton = null;
                // 좋아요 수
        TextView likeTextView = null;
                // 다운로드 버튼
        ImageButton downloadButton = null;
            // 본문 글
        TextView contentTextView = null;

        // 바텀
            // 댓글 보기
        TextView bottomTextView = null;

        public ShareRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            // 헤더
            profileImageView = itemView.findViewById(R.id.recyclerView_item_share_header_profile);
            nickNameTextView = itemView.findViewById(R.id.recyclerView_item_share_header_nickName);
            dateTextView = itemView.findViewById(R.id.recyclerView_item_share_header_date);
            menuButton = itemView.findViewById(R.id.recyclerView_item_share_header_menu);

            // 바디
            backgroundImageView = itemView.findViewById(R.id.recyclerView_item_share_body_background_imageView);
                // 메뉴
            likeButton = itemView.findViewById(R.id.recyclerView_item_share_body_menu_like);
            likeTextView = itemView.findViewById(R.id.recyclerView_item_share_body_menu_likeNum);
            contentTextView = itemView.findViewById(R.id.recyclerView_item_share_body_content_textView);

            // 바텀
            bottomTextView = itemView.findViewById(R.id.recyclerView_item_share_bottom_textView);
        }
    }

    /**
     * 1. onCreateViewHolder
     */
    @NonNull
    @Override
    public ShareRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_share, parent, false);

        mContext = parent.getContext();

        ShareRecyclerViewHolder viewHolder = new ShareRecyclerViewHolder(view);

        return viewHolder;
    }

    /**
     * 3. onBindViewHolder
     */
    @Override
    public void onBindViewHolder(@NonNull final ShareRecyclerViewHolder holder, final int position) {
        // ShareItem List 에서 Position Index 의 ShareItem 을 가져옴
        final ShareItem shareItemSelected = shareItemList.get(position);

        Log.d("onBindViewHolder: ", String.valueOf(position));

        /**
         * 각 View 의 변수는 이미 ViewHolder 에 선언 & 초기화 되어있으므로 매개변수로 넘어온 holder 변수를 이용해 호출할 수 있음
         */

        // 프로필 이미지 표시
        ImageUtils.setProfileImageOnCircleImageView(mContext, holder.profileImageView, shareItemSelected.getMember().getProfileImgUrl());

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
        } else {
            holder.backgroundImageView.setImageResource(ImageUtils.DEFAULT_BACKGROUND_IMAGE);
        }

        // 본문 표시
        String content = shareItemSelected.getContent();
        holder.contentTextView.setText(content);
        CommonUtils.setReadMore(holder.contentTextView, content, 5);

        // 메뉴 버튼 Listener
        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shareItemSelected.getMember().getKey().equals(CommonUtils.getMember().getKey())) {
                    /**
                     * 글쓴이일 때
                     */

                    // 수정 버튼 in Dialog Listener
                    View.OnClickListener updateBtnListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myMenuDialog.dismiss();

                            fragmentShareList.startUpdateActivity(shareItemSelected);
                        }
                    };

                    // 삭제 버튼 in Dialog Listener
                    final View.OnClickListener deleteBtnListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 확인 버튼 in Dialog Listener
                            View.OnClickListener okBtnListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    deleteDialog.dismiss();

                                    deleteShareItemApi(shareItemSelected);
                                }
                            };

                            // 취소 버튼 in Dialog Listener
                            View.OnClickListener cancelBtnListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    deleteDialog.dismiss();
                                }
                            };

                            deleteDialog = new HorizontalTwoButtonDialog(mContext, okBtnListener, cancelBtnListener, "정말 삭제하시겠습니까?", "확인", "취소");

                            myMenuDialog.dismiss();

                            deleteDialog.show();
                        }
                    };

                    // 공유 버튼 in Dialog, Listener
                    View.OnClickListener shareBtnListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO: 2020-04-26 사진 공유 기능
                            myMenuDialog.dismiss();
                        }
                    };

                    // 다운로드 버튼 in Dialog, Listener
                    View.OnClickListener downloadBtnListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String url = shareItemSelected.getBackgroundUrl();
                            if(url == null) {
                                Toast.makeText(mActivity, "기본 이미지는 다운받으실 수 없습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                new ImageUtils.ImageDownload().execute(shareItemSelected.getBackgroundUrl(), mActivity);
                            }

                            myMenuDialog.dismiss();
                        }
                    };

                    myMenuDialog = new OnlyVerticalFourButtonDialog(mContext, updateBtnListener, deleteBtnListener, shareBtnListener, downloadBtnListener,
                            "글 수정", "글 삭제", "사진 공유", "사진 다운로드");
                    myMenuDialog.show();
                } else {
                    /**
                     * 글쓴이가 아닐 떄
                     */

                    // 공유 버튼 in Dialog, Listener
                    View.OnClickListener shareBtnListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO: 2020-04-26 사진 공유 기능
                            menuDialog.dismiss();
                        }
                    };

                    // 다운로드 버튼 in Dialog, Listener
                    View.OnClickListener downloadBtnListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO: 2020-04-26 사진 다운로드 기능
                            menuDialog.dismiss();
                        }
                    };

                    menuDialog = new OnlyVerticalTwoButtonDialog(mContext, shareBtnListener, downloadBtnListener, "사진 공유", "사진 다운로드");
                    menuDialog.show();
                }
            }
        });

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

        // TODO: 2020-04-22 댓글 몇개 보기 로 수정
        // 댓글 모두 보기 Text 설정
        holder.bottomTextView.setText("댓글 모두 보기");

        // 댓글 모두 보기 Listener
        holder.bottomTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);

                // 필요한 정보들만 액티비티로 전달
                intent.putExtra("memberNickName", shareItemSelected.getMember().getNickName());
                intent.putExtra("memberProfileImgUrl", shareItemSelected.getMember().getProfileImgUrl());

                intent.putExtra("id", shareItemSelected.getId());
                intent.putExtra("content", shareItemSelected.getContent());
                Date regDate = shareItemSelected.getRegDate();
                String regDateString = CommonUtils.convertFromDateToString(regDate, "YYYY.MM.dd");
                intent.putExtra("dateString", regDateString);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shareItemList.size();
    }



    /**
     * APIs
     */
    // 좋아요 버튼 클릭 시 실행되는 Task todo: 일반 메소드로 바꾸기
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

    // ShareItem 삭제
    private void deleteShareItemApi(final ShareItem shareItemSelected) {
        // DB 작업을 위해 필요한 ShareItem ID
        Long shareItemId = shareItemSelected.getId();

        Call<Void> call = RetrofitHelper.getInstance().getRetrofitService().deleteShareItemById(shareItemId);
        Callback<Void> callback = new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    shareItemList.remove(shareItemSelected);

                    // TODO: 2020-04-23 임시로 0 들어감
                    fragmentShareList.getShareItems(0);
                } else {
                    Log.d("api 응답은 왔으나 실패", "status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("api 로그인 통신 실패", t.getMessage());
            }
        };
        call.enqueue(callback);
    }
}
