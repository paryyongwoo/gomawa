package com.gomawa.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gomawa.R;
import com.gomawa.adapter.CommentRecyclerViewAdapter;
import com.gomawa.common.CommonUtils;
import com.gomawa.dto.Comment;
import com.gomawa.dto.Member;
import com.gomawa.dto.ShareItem;
import com.gomawa.network.RetrofitHelper;

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
    private List<Comment> commentList = null;

    // Parent ShareItem
    private ShareItem parentShareItem = null;

    // RecyclerView Adapter
    CommentRecyclerViewAdapter adapter = null;

    // EditText
    EditText editText = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // 이전 액티비티에서 전달한 ShareItem 을 받아옴
        Intent intent = getIntent();
        parentShareItem = getParentShareItem(intent);

        // DB 에서 ShareItemId 를 이용해 댓글 리스트를 가져옴
        new GetCommentByShareItemIdApi().execute();
    }

    // 이전 액티비티에서 보낸 Extras 를 가져와서 ShareItem 인스턴스를 생성하는 메소드
    private ShareItem getParentShareItem(Intent intent) {
        Long id = intent.getExtras().getLong("id");

        Long memberKey = intent.getExtras().getLong("memberKey");
        String memberEmail = intent.getExtras().getString("memberEmail");
        String memberGender = intent.getExtras().getString("memberGender");
        String memberNickName = intent.getExtras().getString("memberNickName");
        //String memberDateString = intent.getExtras().getString("memberDateString");
        // TODO: 2020-04-18 Date To String 작업
        // Date memberDate =
        String memberProfileImgUrl = intent.getExtras().getString("memberProfileImgUrl");
        Member member = new Member(id, memberKey, memberEmail, memberGender, memberNickName, null, memberProfileImgUrl);

        //String dateString = intent.getExtras().getString("dateString");
        // TODO: 2020-04-18 Date To String 작업
        // Date date =

        String content = intent.getExtras().getString("content");
        String backgroundUrl = intent.getExtras().getString("backgroundUrl");
        int likeNum = intent.getExtras().getInt("likeNum");

        ShareItem shareItem = new ShareItem(id, member, null, content, backgroundUrl, likeNum);

        return shareItem;
    }

    private void initView() {
        /**
         * Header
         */
        // 백 버튼
        ImageButton backBtn = findViewById(R.id.activity_comment_header_back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2020-04-18 CommentActivity Back Button Listener
                finish();
            }
        });

        // 타이틀
        TextView titleTextView = findViewById(R.id.activity_comment_header_title_textView);
        titleTextView.setText("댓글");

        /**
         * Body
         */
        // 프로필 이미지
        CircleImageView profileImageView = findViewById(R.id.activity_comment_body_profile_imageView);
        String imageUrl = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png"; // 기능 미 구현, 임시 이미지의 URL
        Glide.with(mContext).load(imageUrl).into(profileImageView);

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
        // TODO: 2020-04-18 날짜
        // String date = parentShareItem.getDate().toString();
        String date = null;
        dateTextView.setText(date);

        /**
         * Recycler View
         */
        RecyclerView recyclerView = findViewById(R.id.activity_comment_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new CommentRecyclerViewAdapter(commentList);
        recyclerView.setAdapter(adapter);

        /**
         * Bottom
         */
        // 댓글 쓰는 EditText
        editText = findViewById(R.id.activity_comment_bottom_editText);
        editText.setText("");
        // TODO: 2020-04-18 CommnetActivity EditText Focusable

        // 댓글 완료 버튼
        ImageButton okBtn = findViewById(R.id.activity_comment_bottom_ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 새 Comment 인스턴스를 만듬
                Comment comment = new Comment();
                comment.setId(0l);
                comment.setContent(editText.getText().toString());
                comment.setMember(CommonUtils.getMember());
                comment.setShareItem(parentShareItem);
                // TODO: 2020-04-18 Date 를 서버로 보내는 방법
                comment.setRegDate(null);

                new AddCommentApi().execute(comment);
            }
        });
    }

    private class GetCommentByShareItemIdApi extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            Long shareItemId = parentShareItem.getId();

            Call<List<Comment>> call = RetrofitHelper.getInstance().getRetrofitService().getCommentByShareItemId(shareItemId);
            Callback<List<Comment>> callback = new Callback<List<Comment>>() {
                @Override
                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                    if(response.isSuccessful()) {
                       commentList = response.body();

                       if(commentList == null) {
                           // 응답이 NULL 일 때
                           Log.d("response.body() is null", "");
                       } else {
                           initView();
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

            return null;
        }
    }

    private class AddCommentApi extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            // Index 0: Comment comment
            Comment comment = (Comment) objects[0];

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

                            adapter.notifyDataSetChanged();
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

            return null;
        }
    }
}
