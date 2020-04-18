package com.gomawa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gomawa.R;
import com.gomawa.dto.Comment;
import com.gomawa.dto.Member;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentRecyclerViewHolder> {
    // Context
    Context mContext = null;

    // Comment List
    List<Comment> commentList;

    public CommentRecyclerViewAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public class CommentRecyclerViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImageView;
        TextView nickNameTextView;
        TextView contentTextView;
        TextView dateTextView;

        public CommentRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageView = itemView.findViewById(R.id.recyclerView_profile_imageView);
            nickNameTextView = itemView.findViewById(R.id.recyclerView_nickName_textView);
            contentTextView = itemView.findViewById(R.id.recyclerView_content_textView);
            dateTextView = itemView.findViewById(R.id.recyclerView_date_textView);
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
    public void onBindViewHolder(@NonNull CommentRecyclerViewHolder holder, int position) {
        // Position Index Comment
        Comment comment = commentList.get(position);

        // 댓글 쓴 Member
        Member member = comment.getMember();

        // 프로필 이미지
        //String profileImageUrl = member.getProfileImgUrl();
        String profileImageUrl = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png";
        Glide.with(mContext).load(profileImageUrl).into(holder.profileImageView);

        // 닉네임
        String nickName = member.getNickName();
        holder.nickNameTextView.setText(nickName);

        // 댓글 본문
        String content = comment.getContent();
        holder.contentTextView.setText(content);

        // 댓글 날짜
        // TODO: 2020-04-18 DATEDATEDATE!!!!!!!!!!!!!
        //String date = comment.getRegDate().toString();
        holder.dateTextView.setText(null);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
