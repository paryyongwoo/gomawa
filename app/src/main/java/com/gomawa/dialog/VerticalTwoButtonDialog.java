package com.gomawa.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gomawa.R;

public class VerticalTwoButtonDialog extends Dialog {
    // 버튼 리스너
    private View.OnClickListener firstBtnListener = null;
    private View.OnClickListener secondBtnListener = null;

    // 텍스트 뷰 텍스트
    private String textViewText = null;

    // 버튼 텍스트
    private String firstBtnText = null;
    private String secondBtnText = null;

    // 생성자 - 버튼 리스너와 텍스트를 가져와서 초기화해줌
    public VerticalTwoButtonDialog(@NonNull Context context, View.OnClickListener firstBtnListener, View.OnClickListener secondBtnListener, String textViewText, String firstBtnText, String secondBtnText) {
        super(context);
        this.firstBtnListener = firstBtnListener;
        this.secondBtnListener = secondBtnListener;
        this.textViewText = textViewText;
        this.firstBtnText = firstBtnText;
        this.secondBtnText = secondBtnText;

        // 모서리를 둥글게 만든 부분에 뜨는 검은 배경을 지워주는 코드
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_vertical_two_button);

        // 텍스트뷰에 텍스트 연결
        TextView textView = findViewById(R.id.dialog_vertical_two_button_textView);
        textView.setText(textViewText);

        // 두 버튼에 리스너 와 텍스트 연결
        Button firstBtn = findViewById(R.id.dialog_vertical_two_button_first_button);
        firstBtn.setOnClickListener(firstBtnListener);
        firstBtn.setText(firstBtnText);

        Button secondBtn = findViewById(R.id.dialog_vertical_two_button_second_button);
        secondBtn.setOnClickListener(secondBtnListener);
        secondBtn.setText(secondBtnText);
    }
}
