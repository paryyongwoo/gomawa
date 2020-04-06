package com.gomawa.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.gomawa.R;

public class VerticalTwoButtonDialog extends Dialog {
    // 버튼 리스너
    private View.OnClickListener firstBtnListener;
    private View.OnClickListener secondBtnListener;

    // 버튼 텍스트
    private String firstBtnText;
    private String secondBtnText;

    // 생성자 - 버튼 리스너와 텍스트를 가져와서 초기화해줌
    public VerticalTwoButtonDialog(@NonNull Context context, View.OnClickListener fromGalleryBtnListener, View.OnClickListener fromCameraBtnListener, String firstBtnText, String secondBtnText) {
        super(context);
        this.firstBtnListener = fromGalleryBtnListener;
        this.secondBtnListener = fromCameraBtnListener;
        this.firstBtnText = firstBtnText;
        this.secondBtnText = secondBtnText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_vertical_two_button);

        // 두 버튼에 리스너 연결
        Button firstButton = findViewById(R.id.dialog_vertical_two_button_first_button);
        firstButton.setOnClickListener(firstBtnListener);
        firstButton.setText(firstBtnText);

        Button secondButton = findViewById(R.id.dialog_vertical_two_button_second_button);
        secondButton.setOnClickListener(secondBtnListener);
        secondButton.setText(secondBtnText);
    }
}
