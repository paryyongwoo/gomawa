package com.gomawa.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gomawa.R;
import com.gomawa.adapter.CommentRecyclerViewAdapter;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.Constants;
import com.gomawa.common.Data;
import com.gomawa.common.ImageUtils;
import com.gomawa.dto.Comment;
import com.gomawa.dto.Member;
import com.gomawa.dto.ShareItem;
import com.gomawa.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends Activity {
    // Context & Activity
    private Context mContext = this;
    private Activity mActivity = this;

    // Comment List
    private List<Comment> commentList = new ArrayList<>();

    // Parent ShareItem
    private ShareItem parentShareItem = null;
    private String parentShareItemRegDate = null;

    // RecyclerView
    private RecyclerView recyclerView = null;

    // RecyclerView Adapter
    private CommentRecyclerViewAdapter commentRecyclerViewAdapter = null;

    // EditText
    private EditText editText = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // 이전 액티비티에서 전달한 ShareItem 을 받아옴 - 필요한 정보만 담겨있는 ShareItem 으로 사용에 주의해야함
        Intent intent = getIntent();
        parentShareItem = getParentShareItem(intent);

        // 댓글 전부 가져오는 API
        getCommentByShareItemIdApi();

        // 뷰 설정
        initView();
    }

    // 이전 액티비티에서 보낸 Extras 를 가져와서 ShareItem 인스턴스를 생성하는 메소드
    private ShareItem getParentShareItem(Intent intent) {
        Long id = intent.getExtras().getLong("id");

        String memberNickName = intent.getExtras().getString("memberNickName");
        String memberProfileImgUrl = intent.getExtras().getString("memberProfileImgUrl");
        Member member = new Member();
        member.setNickName(memberNickName);
        member.setProfileImgUrl(memberProfileImgUrl);

        parentShareItemRegDate = intent.getExtras().getString("dateString");
        String content = intent.getExtras().getString("content");
        ShareItem shareItem = new ShareItem();
        shareItem.setId(id);
        shareItem.setContent(content);
        shareItem.setMember(member);

        return shareItem;
    }

    private void initView() {
        /**
         * Header
         */
        // 백 버튼
        ImageButton backBtn = findViewById(R.id.header_second_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 타이틀
        TextView titleTextView = findViewById(R.id.header_second_title);
        titleTextView.setTextColor(getResources().getColor(R.color.inactiveColor));
        titleTextView.setText("댓글");

        // 완료 버튼 비활성화
        Button headerOkBtn = findViewById(R.id.header_second_ok);
        headerOkBtn.setVisibility(View.INVISIBLE);

        /**
         * Body
         */
        // 프로필 이미지
        CircleImageView profileImageView = findViewById(R.id.activity_comment_body_profile_imageView);
        ImageUtils.setProfileImageOnCircleImageView(mContext, profileImageView, parentShareItem.getMember().getProfileImgUrl());

        // 닉네임
        TextView nickNameTextView = findViewById(R.id.activity_comment_body_nickName_textView);
        String nickName = parentShareItem.getMember().getNickName();
        nickNameTextView.setText(nickName);

        // 본문
        TextView contentTextView = findViewById(R.id.activity_comment_body_content_textView);
        String content = parentShareItem.getContent();
        contentTextView.setText(content);

        // 날짜
        TextView dateTextView = findViewById(R.id.activity_comment_body_date_textView);
        String regDateString = parentShareItemRegDate;
        dateTextView.setText(regDateString);

        /**
         * Recycler View
         */
        recyclerView = findViewById(R.id.activity_comment_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(commentList, this);
        recyclerView.setAdapter(commentRecyclerViewAdapter);

        /**
         * Bottom
         */
        // 댓글 쓰는 EditText
        editText = findViewById(R.id.activity_comment_bottom_editText);
        editText.setText("");
        editText.requestFocus();

        // 댓글 완료 버튼
        ImageButton okBtn = findViewById(R.id.activity_comment_bottom_ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 새 Comment 인스턴스를 만듬
                Comment comment = new Comment();
                //comment.setId(0l);
                comment.setContent(editText.getText().toString());
                comment.setMember(Data.getMember());
                comment.setShareItem(parentShareItem);
                comment.setRegDate(new Date());

                addCommentApi(comment);
            }
        });
    }

    /**
     * API
     */
    public void getCommentByShareItemIdApi() {
        Long shareItemId = parentShareItem.getId();

        Call<List<Comment>> call = RetrofitHelper.getInstance().getRetrofitService().getCommentByShareItemId(shareItemId);
        Callback<List<Comment>> callback = new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(response.isSuccessful()) {
                    // Comment List add 작업을 위해 초기화
                    commentList.clear();

                    // 서버에서 반환받은 Comment List
                    List<Comment> commentListReceived = response.body();
                    int size = commentListReceived.size();

                    for(int i=0; i<size; i++) {
                        commentList.add(commentListReceived.get(i));
                    }

                    if(commentList == null) {
                        // 응답이 NULL 일 때
                        Log.d("response.body() is null", "");
                    } else {
                        // 정보 갱신
                        commentRecyclerViewAdapter.notifyDataSetChanged();

                        // 새로고침 아이콘 제거
                        // swipeRefreshLayout.setRefreshing(false);

                        // 스크롤
                        recyclerView.smoothScrollToPosition(0);
                    }
                } else {
                    Log.d("api 응답은 왔으나 실패", "status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.d("api 로그인 통신 실패", t.getMessage());
            }
        };
        call.enqueue(callback);
    }

    private void addCommentApi(Comment comment) {
        Call<Comment> call = RetrofitHelper.getInstance().getRetrofitService().addComment(comment);
        Callback<Comment> callback = new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if(response.isSuccessful()) {
                    Comment comment = response.body();

                    if(comment == null) {
                        // 응답이 Null 일 때
                        Log.d("response.body() is null", "");
                    } else {
                        commentList.add(comment);

                        // 키보드 내리기
                        CommonUtils.hideKeyboard(mActivity, editText);

                        // editText 초기화
                        editText.setText("");

                        getCommentByShareItemIdApi();
                    }
                } else {
                    Log.d("api 응답은 왔으나 실패", "status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.d("api 로그인 통신 실패", t.getMessage());
            }
        };
        call.enqueue(callback);
    }
}
