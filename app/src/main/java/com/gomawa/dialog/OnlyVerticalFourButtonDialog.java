package com.gomawa.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.gomawa.R;

public class OnlyVerticalFourButtonDialog extends Dialog {
    // 버튼 리스너
    private View.OnClickListener firstBtnListener = null;
    private View.OnClickListener secondBtnListener = null;
    private View.OnClickListener thirdBtnListener = null;
    private View.OnClickListener fourthBtnListener = null;

    // 버튼 텍스트
    private String firstBtnText = null;
    private String secondBtnText = null;
    private String thirdBtnText = null;
    private String fourthBtnText = null;

    // 생성자 - 버튼 리스너와 텍스트를 가져와서 초기화해줌
    public OnlyVerticalFourButtonDialog(@NonNull Context context, View.OnClickListener firstBtnListener, View.OnClickListener secondBtnListener, View.OnClickListener thirdBtnListener, View.OnClickListener fourthBtnListener,
                                        String firstBtnText, String secondBtnText, String thirdBtnText, String fourthBtnText) {
        super(context);
        this.firstBtnListener = firstBtnListener;
        this.secondBtnListener = secondBtnListener;
        this.thirdBtnListener = thirdBtnListener;
        this.fourthBtnListener = fourthBtnListener;

        this.firstBtnText = firstBtnText;
        this.secondBtnText = secondBtnText;
        this.thirdBtnText = thirdBtnText;
        this.fourthBtnText = fourthBtnText;

        // 모서리를 둥글게 만든 부분에 뜨는 검은 배경을 지워주는 코드
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_only_vertical_four_button);

        // 네 버튼에 리스너 와 텍스트 연결
        Button firstBtn = findViewById(R.id.dialog_only_vertical_four_button_first_button);
        firstBtn.setOnClickListener(firstBtnListener);
        firstBtn.setText(firstBtnText);

        Button secondBtn = findViewById(R.id.dialog_only_vertical_four_button_second_button);
        secondBtn.setOnClickListener(secondBtnListener);
        secondBtn.setText(secondBtnText);

        Button thirdBtn = findViewById(R.id.dialog_only_vertical_four_button_third_button);
        thirdBtn.setOnClickListener(thirdBtnListener);
        thirdBtn.setText(thirdBtnText);

        Button fourthBtn = findViewById(R.id.dialog_only_vertical_four_button_fourth_button);
        fourthBtn.setOnClickListener(fourthBtnListener);
        fourthBtn.setText(fourthBtnText);
    }
}
