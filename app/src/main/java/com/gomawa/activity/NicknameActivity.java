package com.gomawa.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gomawa.R;
import com.gomawa.common.Global;

public class NicknameActivity extends Activity {
    Activity mActivity = this;

    EditText editText;
    Button deleteBtn;
    TextView lengthTextView;
    ImageButton backBtn;
    Button okBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);

        initView();
    }

    private void initView() {
        // FragmentSetting 에서 받아온 인텐트. 현재 닉네임이 "nowNickname"에 담겨 있음
        Intent i = getIntent();

        // editText 와 deleteBtn 초기화
        editText = findViewById(R.id.activity_nickname_editText);
        deleteBtn = findViewById(R.id.activity_nickname_deleteBtn);
        lengthTextView = findViewById(R.id.activity_nickname_length_textView);

        // ConstraintLayout 터치 시 editText로 부터 포커스 빼앗고 키보드 내림
        // question : 왜 경고가 뜨는 지 모르겠슴니다
        ConstraintLayout constraintLayout = findViewById(R.id.activity_nickname_body);
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                editText.clearFocus();
                Global.hideKeyboard(mActivity, editText);

                return false;
            }
        });

        // editText의 Text를 현재 닉네임으로 변경
        editText.setText(i.getExtras().getString("nowNickname"));

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
                Global.printLength(editable, lengthTextView, Global.NICKNAME_LIMIT);
            }
        });

        // 현재 닉네임의 길이를 계산하여 lengthTextView 에 출력 (이후에는 위 TextChangedListener 에서 진행함)
        lengthTextView.setText(Global.makeLengthString(Global.nickname.getBytes().length, Global.NICKNAME_LIMIT));

        // deleteBtn Listener
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });

        // editText에 포커스 주고 키보드 올림
        Global.showKeyboard(mActivity, editText);

        // 백 버튼 - editText의 값과 관계없이 액티비티만 종료함
        backBtn = findViewById(R.id.activity_nickname_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 키보드를 띄운 상태로 누를 경우 키보드가 제거되지 않아서 여기에서 키보드를 내려줌
                Global.hideKeyboard(mActivity, editText);

                setResult(Global.RESULT_SUCESS);
                finish();
            }
        });

        // 확인 버튼 - editText의 값을 인텐트에 실어서 보냄
        okBtn = findViewById(R.id.activity_nickname_okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 키보드를 띄운 상태로 누를 경우 키보드가 제거되지 않아서 여기에서 키보드를 내려줌
                Global.hideKeyboard(mActivity, editText);

                // 닉네임 길이 검사
                int length = editText.getText().toString().getBytes().length;

                if(length > Global.NICKNAME_LIMIT) {
                    // 닉네임 길이 제한보다 길다면~~ 처리 (일단은 무시)
                } else if(length == 0) {
                    // 닉네임 길이가 0이라면~~ 처리 (일단은 무시)
                } else {
                    // 이도저도 아니라면 인텐트에 editText의 Text 값을 실어서 Setting Fragment 로 보냄.
                    // 실질적인 닉네임 변경 작업은 Setting Fragment에서 이루어짐.
                    // todo : 그냥 여기서 닉네임 변경 작업까지 한 후에 fragment 에서는 textView의 Text 값만 바꿔주는 형태로 바꾸는 게 좋을 듯
                    Intent intent = new Intent();
                    intent.putExtra("newNickname", editText.getText().toString());
                    setResult(Global.RESULT_SUCESS_NICKNAME, intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(Global.RESULT_SUCESS);
        finish();
    }
}
