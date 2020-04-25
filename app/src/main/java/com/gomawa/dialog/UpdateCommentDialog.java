package com.gomawa.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gomawa.R;
import com.gomawa.activity.CommentActivity;
import com.gomawa.common.CommonUtils;
import com.gomawa.dto.Comment;
import com.gomawa.network.RetrofitHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 댓글 수정에만 사용하는 액티비티
 */
public class UpdateCommentDialog extends Dialog {
    // getComment ByShareItemId 호출을 위해 가져오는 Activity (CommentActivity)
    private CommentActivity mActivity;

    private Comment comment;

    // 생성자
    public UpdateCommentDialog(@NonNull Context context, CommentActivity mActivity, Comment comment) {
        super(context);

        this.mActivity = mActivity;

        // 수정하려는 Comment
        this.comment = comment;

        // 모서리를 둥글게 만든 부분에 뜨는 검은 배경을 지워주는 코드
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update_comment);

        // EditText Text 설정
        final EditText editText = findViewById(R.id.dialog_update_comment_editText);
        editText.setText(comment.getContent());

        // EditText Focus & 키보드 올리기
        CommonUtils.showKeyboard(mActivity, editText);

        // 확인 버튼 설정
        Button okBtn = findViewById(R.id.dialog_update_comment_first_button);
        okBtn.setText("확인");
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newContent = editText.getText().toString();

                updateCommentApi(comment, newContent);
            }
        });

        // 취소 버튼 설정
        Button cancelBtn = findViewById(R.id.dialog_update_comment_second_button);
        cancelBtn.setText("취소");
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }



    /**
     * API
     */
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

                    dismiss();
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
