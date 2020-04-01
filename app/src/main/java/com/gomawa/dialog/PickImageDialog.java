package com.gomawa.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.gomawa.R;

public class PickImageDialog extends Dialog {
    // Dialog 내의 버튼 리스너
    View.OnClickListener fromGalleryBtnListener;
    View.OnClickListener fromCameraBtnListener;

    // 버튼 리스너 두 개를 추가로 받는 생성자
    public PickImageDialog(@NonNull Context context, View.OnClickListener fromGalleryBtnListener, View.OnClickListener fromCameraBtnListener) {
        super(context);
        this.fromGalleryBtnListener = fromGalleryBtnListener;
        this.fromCameraBtnListener = fromCameraBtnListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pick_image);

        // 두 버튼에 리스너 연결
        Button fromGalleryBtn = findViewById(R.id.dialog_pick_image_from_gallery_button);
        fromGalleryBtn.setOnClickListener(fromGalleryBtnListener);
        Button fromCameraBtn = findViewById(R.id.dialog_pick_image_from_camera_button);
        fromCameraBtn.setOnClickListener(fromCameraBtnListener);
    }
}
