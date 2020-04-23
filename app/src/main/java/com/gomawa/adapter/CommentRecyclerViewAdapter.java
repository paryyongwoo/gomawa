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
import com.gomawa.dialog.HorizontalTwoButtonDialog;
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

    // 삭제 버튼 확인 다이얼로그
    private HorizontalTwoButtonDialog horizontalTwoButtonDialog;

    public CommentRecyclerViewAdapter(List<Comment> commentList, CommentActivity mActivity) {
        this.commentList = commentList;
        this.mActivity = mActivity;
    }

    public class CommentRecyclerViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImageView;
        TextView nickNameTextView;

        LinearLayout buttonsLayout;
        ImageButton updateButton;
        ImageButton deleteButton;

        FrameLayout contentLayout;
        TextView contentTextView;
        EditText contentEditText;

        TextView dateTextView;

        public CommentRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.recyclerView_comment_profile_imageView);
            nickNameTextView = itemView.findViewById(R.id.recyclerView_comment_nickName_textView);

            buttonsLayout = itemView.findViewById(R.id.recyclerView_comment_buttons);
            updateButton = itemView.findViewById(R.id.recyclerView_comment_update_button);
            deleteButton = itemView.findViewById(R.id.recyclerView_comment_delete_button);

            contentLayout = itemView.findViewById(R.id.recyclerView_comment_content);
            contentTextView = itemView.findViewById(R.id.recyclerView_comment_content_textView);
            contentEditText = itemView.findViewById(R.id.recyclerView_comment_content_editText);

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

        // 미리 설정해둔 Layout Params
        final FrameLayout.LayoutParams showLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final FrameLayout.LayoutParams hideLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);

        // 댓글 쓴 Member
        Member member = comment.getMember();

        // 프로필 이미지
        //String profileImageUrl = member.getProfileImgUrl();
        String profileImageUrl = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png";
        Glide.with(mContext).load(profileImageUrl).into(holder.profileImageView);

        // 닉네임
        String nickName = member.getNickName();
        holder.nickNameTextView.setText(nickName);

        // Buttons Layout Visibility
        if(comment.getMember().getId().equals(CommonUtils.getMember().getId())) {
            // 댓글이 현재 유저의 댓글이라면 ~
            holder.buttonsLayout.setVisibility(View.VISIBLE);
        } else {
            holder.buttonsLayout.setVisibility(View.INVISIBLE);
        }

        // 수정 버튼
        holder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2020-04-23 CommentActivity 의 EditText를 안보이게 하거나, 스타일을 완전 바꾸거나
                // TextView hide
                holder.contentTextView.setVisibility(View.INVISIBLE);
                holder.contentTextView.setLayoutParams(hideLayoutParams);

                // EditText show
                holder.contentEditText.setVisibility(View.VISIBLE);
                holder.contentEditText.setLayoutParams(showLayoutParams);

                // EditText setText
                holder.contentEditText.setText(holder.contentTextView.getText().toString());

                // EditText Focus
                CommonUtils.showKeyboard(mActivity, holder.contentEditText);

                // 수정 완료 버튼 리스너
                holder.updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // EditText 의 Text
                        String content = holder.contentEditText.getText().toString();

                        // 수정 후의 Content 가 변화가 없으면 ~
                        if(content.equals(comment.getContent())) {
                            // 뷰만 다시 변경해줌
                            holder.contentTextView.setVisibility(View.VISIBLE);
                            holder.contentTextView.setLayoutParams(showLayoutParams);
                            holder.contentEditText.setVisibility(View.INVISIBLE);
                            holder.contentEditText.setLayoutParams(hideLayoutParams);
                            holder.contentEditText.setText("");
                        } else {
                            // Content 변화가 있으면 서버에서 수정 작업
                            updateCommentApi(comment, content);
                        }
                    }
                });
            }
        });

        // 삭제 버튼
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 확인 버튼 Listener
                View.OnClickListener okBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteCommentApi(comment.getId());

                        horizontalTwoButtonDialog.dismiss();
                    }
                };

                // 취소 버튼 Listener
                View.OnClickListener cancelBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        horizontalTwoButtonDialog.dismiss();
                    }
                };

                horizontalTwoButtonDialog = new HorizontalTwoButtonDialog(mContext, okBtnListener, cancelBtnListener, "삭제하시겠습니까?", "확인", "취소");
                horizontalTwoButtonDialog.show();
            }
        });

        // 댓글 본문
        String content = comment.getContent();
        holder.contentTextView.setVisibility(View.VISIBLE);
        holder.contentTextView.setLayoutParams(showLayoutParams);
        holder.contentTextView.setText(content);
        holder.contentEditText.setVisibility(View.INVISIBLE);
        holder.contentEditText.setLayoutParams(hideLayoutParams);
        holder.contentEditText.setText("");

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

    private void updateCommentApi(Comment comment, String content) {
        Comment newComment = new Comment();
        newComment.setId(comment.getId());
        newComment.setRegDate(comment.getRegDate());
        newComment.setShareItem(comment.getShareItem());
        newComment.setMember(comment.getMember());
        newComment.setContent(content);

        Call<Void> call = RetrofitHelper.getInstance().getRetrofitService().updateComment(newComment);
        Callback<Void> callback = new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
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
