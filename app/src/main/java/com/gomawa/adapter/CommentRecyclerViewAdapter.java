package com.gomawa.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gomawa.R;
import com.gomawa.activity.CommentActivity;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.Constants;
import com.gomawa.common.Data;
import com.gomawa.common.ImageUtils;
import com.gomawa.dialog.HorizontalTwoButtonDialog;
import com.gomawa.dialog.OnlyVerticalTwoButtonDialog;
import com.gomawa.dialog.UpdateCommentDialog;
import com.gomawa.dto.Comment;
import com.gomawa.dto.Member;
import com.gomawa.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentRecyclerViewHolder> {
    // Context & Activity
    private Context mContext = null;
    private CommentActivity mActivity;

    // Comment List
    private List<Comment> commentList;

    // 댓글 눌렀을 때 뜨는 다이얼로그
    private OnlyVerticalTwoButtonDialog menuDialog = null;

    // 댓글 수정 다이얼로그
    private UpdateCommentDialog updateCommentDialog = null;

    // 삭제 버튼 확인 다이얼로그
    private HorizontalTwoButtonDialog deleteDialog = null;

    public CommentRecyclerViewAdapter(List<Comment> commentList, CommentActivity mActivity) {
        this.commentList = commentList;
        this.mActivity = mActivity;
    }

    public class CommentRecyclerViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImageView;
        TextView nickNameTextView;
        
        TextView contentTextView;

        TextView dateTextView;

        public CommentRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            // 댓글을 선택했을 때
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 선택된 댓글의 Position
                    int position = getAdapterPosition();

                    // 선택된 댓글의 인스턴스
                    final Comment comment = commentList.get(position);

                    // 댓글 쓴 멤버와 현재 접속된 멤버가 같을 때만 실행됨
                    if(comment.getMember().getKey().equals(Data.getMember().getKey())) {
                        // 수정 버튼 in Dialog Listener
                        View.OnClickListener updateBtnListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // 수정 다이얼로그
                                updateCommentDialog = new UpdateCommentDialog(mContext, mActivity, comment);

                                menuDialog.dismiss();

                                updateCommentDialog.show();
                            }
                        };

                        // 삭제 버튼 in Dialog Listener
                        View.OnClickListener deleteBtnListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // 확인 버튼 in Dialog, Listener
                                View.OnClickListener okBtnListener = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        deleteCommentApi(comment.getId());

                                        deleteDialog.dismiss();
                                    }
                                };

                                // 취소 버튼 in Dialog, Listener
                                View.OnClickListener cancelBtnListener = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        deleteDialog.dismiss();
                                    }
                                };

                                deleteDialog = new HorizontalTwoButtonDialog(mContext, okBtnListener, cancelBtnListener, "삭제하시겠습니까?", "확인", "취소");

                                menuDialog.dismiss();

                                deleteDialog.show();

                            }
                        };

                        // 다이얼로그
                        menuDialog = new OnlyVerticalTwoButtonDialog(mContext, updateBtnListener, deleteBtnListener, "댓글 수정", "댓글 삭제");
                        menuDialog.show();
                    }
                }
            });

            profileImageView = itemView.findViewById(R.id.recyclerView_comment_profile_imageView);
            nickNameTextView = itemView.findViewById(R.id.recyclerView_comment_nickName_textView);

            contentTextView = itemView.findViewById(R.id.recyclerView_comment_content_textView);

            dateTextView = itemView.findViewById(R.id.recyclerView_comment_date_textView);
        }
    }

    @NonNull
    @Override
    public CommentRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_comment, parent, false);

        mContext = parent.getContext();

        CommentRecyclerViewHolder viewHolder = new CommentRecyclerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentRecyclerViewHolder holder, int position) {
        // Position Index Comment
        final Comment comment = commentList.get(position);

        // 댓글 쓴 Member
        Member member = comment.getMember();

        // 프로필 이미지
        ImageUtils.setProfileImageOnCircleImageView(mContext, holder.profileImageView, member.getProfileImgUrl());

        // 닉네임
        String nickName = member.getNickName();
        holder.nickNameTextView.setText(nickName);

        // 댓글 본문
        String content = comment.getContent();
        holder.contentTextView.setText(content);

        // 댓글 날짜
        Date regDate = comment.getRegDate();
        String regDateString = CommonUtils.convertFromDateToString(regDate, "YYYY.MM.dd");
        holder.dateTextView.setText(regDateString);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }



    /**
     * API
     */
    private void deleteCommentApi(Long commentId) {
        Call<Void> call = RetrofitHelper.getInstance().getRetrofitService().deleteCommentById(commentId);
        Callback<Void> callback = new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    mActivity.shareItemList.get(mActivity.position).setCommentNum(mActivity.shareItemList.get(mActivity.position).getCommentNum() - 1);

                    mActivity.getCommentByShareItemIdApi();
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
