package com.gomawa.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gomawa.R;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.Constants;
import com.gomawa.dto.Member;
import com.gomawa.network.RetrofitHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NicknameActivity extends Activity {
    private Activity mActivity = this;

    private EditText editText;
    private Button deleteBtn;
    private TextView lengthTextView;
    private ImageButton backBtn;
    private Button okBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

        initView();
    }

    private void initView() {
        // 상단 타이틀의 Text 값
        TextView title = findViewById(R.id.activity_nickname_title);
        title.setText("닉네임 변경");

        // editText 와 deleteBtn 초기화
        editText = findViewById(R.id.activity_nickname_editText);
        deleteBtn = findViewById(R.id.activity_nickname_deleteBtn);
        lengthTextView = findViewById(R.id.activity_nickname_length_textView);

        // ConstraintLayout 터치 시 editText로 부터 포커스 빼앗고 키보드 내림
        // question: 왜 경고가 뜨는 지 모르겠슴니다
        ConstraintLayout constraintLayout = findViewById(R.id.activity_nickname_body);
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                editText.clearFocus();
                CommonUtils.hideKeyboard(mActivity, editText);

                return false;
            }
        });

        // editText의 Text를 현재 닉네임으로 변경
        editText.setText(CommonUtils.getMember().getNickName());

        // editText 포커스 전환 시 deleteBtn 나타나고 사라짐
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // Focused
                if(b) deleteBtn.setVisibility(View.VISIBLE);
                else deleteBtn.setVisibility(View.INVISIBLE);
            }
        });

        // editText Text 가 달라질 때 마다 length 계산해서 length_textView 에 출력
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                CommonUtils.printLength(editable, lengthTextView, Constants.NICKNAME_LIMIT);
            }
        });

        // 현재 닉네임의 길이를 계산하여 lengthTextView 에 출력 (이후에는 위 TextChangedListener 에서 진행함)
        lengthTextView.setText(CommonUtils.makeLengthString(CommonUtils.calculateLength(CommonUtils.getMember().getNickName()), Constants.NICKNAME_LIMIT));

        // deleteBtn Listener
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });

        // editText에 포커스 주고 키보드 올림
        CommonUtils.showKeyboard(mActivity, editText);

        // 백 버튼 - editText의 값과 관계없이 액티비티만 종료함
        backBtn = findViewById(R.id.activity_nickname_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 키보드를 띄운 상태로 누를 경우 키보드가 제거되지 않아서 여기에서 키보드를 내려줌
                CommonUtils.hideKeyboard(mActivity, editText);

                setResult(Constants.RESULT_SUCESS);
                finish();
            }
        });

        // 확인 버튼
        okBtn = findViewById(R.id.activity_nickname_okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 키보드를 띄운 상태로 누를 경우 키보드가 제거되지 않아서 여기에서 키보드를 내려줌
                CommonUtils.hideKeyboard(mActivity, editText);

                // 닉네임 길이 검사
                int length = CommonUtils.calculateLength(editText.getText().toString());

                if(length > Constants.NICKNAME_LIMIT) {
                    // 닉네임 길이 제한보다 길다면~~ 처리 (일단은 무시)
                    CommonUtils.hideKeyboard(mActivity, editText);
                } else if(length == 0) {
                    // 닉네임 길이가 0이라면~~ 처리 (일단은 무시)
                    CommonUtils.hideKeyboard(mActivity, editText);
                } else {
                    // 닉네임 길이가 적당하면 닉네임을 DB에 올린 후 CommonUtils의 Member의 NickName 값도 바꿔줌
                    new RequestApiTask().execute(editText.getText().toString());

                    setResult(Constants.RESULT_SUCESS_NICKNAME);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(Constants.RESULT_SUCESS);
        finish();
    }

    private class RequestApiTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            // 새로 저장될 닉네임
            String newNickName = objects[0].toString();

            // 현재 멤버를 복사해서 닉네임을 newNickName으로 변경
            Member member = CommonUtils.getMember();
            member.setNickName(newNickName);

            // 닉네임만 바뀐 멤버를 매개변수로 setNickName 통신 시작
            Call<Member> call = RetrofitHelper.getInstance().getRetrofitService().setNickName(member);
            Callback<Member> callback = new Callback<Member>() {
                @Override
                public void onResponse(Call<Member> call, Response<Member> response) {
                    Member receiveMember = response.body();

                    Log.d("반환받은 Member : ", receiveMember.toString());

                    CommonUtils.setMember(receiveMember);
                }

                @Override
                public void onFailure(Call<Member> call, Throwable t) {
                    Log.d("api 로그인 통신 실패", t.getMessage());
                }
            };
            call.enqueue(callback);

            return null;
        }
    }
}
