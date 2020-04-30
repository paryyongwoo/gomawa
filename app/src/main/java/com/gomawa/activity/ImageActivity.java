package com.gomawa.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.chrisbanes.photoview.PhotoView;
import com.gomawa.R;
import com.squareup.picasso.Picasso;

public class ImageActivity extends Activity {
    private String url = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // intent 에 담겨있는 url 가져오기
        Intent intent = getIntent();
        url = intent.getExtras().getString("url");

        initView();
    }

    private void initView() {
        // 백 버튼
        ImageButton backBtn = findViewById(R.id.activity_image_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 타이틀
        TextView titleTextView = findViewById(R.id.activity_image_title);
        titleTextView.setText("");

        // 이미지 뷰
        PhotoView photoView = findViewById(R.id.activity_image_photoView);
        Picasso.get().load(url).into(photoView);
    }
}
