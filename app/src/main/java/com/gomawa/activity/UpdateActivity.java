package com.gomawa.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.gomawa.R;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.Constants;
import com.gomawa.network.RetrofitHelper;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ShareItem 수정 액티비티
 */
public class UpdateActivity extends Activity {
    // Context, Activity
    private Context mContext = this;
    private Activity mActivity = this;

    // Background ImageView
    private ImageView backgroundImageView = null;

    // Content EditText
    private EditText contentEditText = null;

    // Background Image File
    private File imageFile = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        initView();
    }

    private void initView() {
        // ShareItem 의 정보가 담겨있는 Intent
        Intent intent = getIntent();

        // DB 작업을 위해 받아놓는 ShareItem ID
        final Long shareItemId = intent.getExtras().getLong("id");

        // 백 버튼 리스너
        View.OnClickListener backBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(Constants.RESULT_CANCEL, intent);
                finish();
            }
        };

        ImageButton backBtn = findViewById(R.id.activity_update_header_backBtn);
        backBtn.setOnClickListener(backBtnListener);

        // 타이틀 설정
        TextView titleTextView = findViewById(R.id.activity_update_header_title);
        titleTextView.setText("글 수정");

        // 프로필 이미지 설정
        CircleImageView profileImageView = findViewById(R.id.activity_update_shareItem_header_profile_imageView);
        //String profileImageUrl = CommonUtils.getMember().getProfileImgUrl();
        String profileImageUrl = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png"; // 기능 미 구현, 임시 이미지의 URL
        Glide.with(mContext).load(profileImageUrl).into(profileImageView);

        // 닉네임 설정
        TextView nickNameTextView = findViewById(R.id.activity_update_shareItem_header_nickName);
        String nickName = CommonUtils.getMember().getNickName();
        nickNameTextView.setText(nickName);

        // 날짜 설정
        TextView dateTextView = findViewById(R.id.activity_update_shareItem_header_date);
        String regDateString = intent.getExtras().getString("regDate");
        dateTextView.setText(regDateString);

        // 배경 이미지 설정
        backgroundImageView = findViewById(R.id.activity_update_shareItem_body_imageView);
        String backgroundImageUrl = intent.getExtras().getString("backgroundUrl");
        if(backgroundImageUrl == null) {
            backgroundImageView.setImageResource(R.drawable.share_item_background);
        } else {
            Picasso.get().load(backgroundImageUrl).into(backgroundImageView);
        }


        // 본문 설정
        contentEditText = findViewById(R.id.activity_update_shareItem_body_content);
        String content = intent.getExtras().getString("content");
        contentEditText.setText(content);

        // 배경 이미지 Listener
        backgroundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // File System.
                final Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*"); // 이미지 파일 호출
                galleryIntent.setAction(Intent.ACTION_PICK);

                // Chooser of file system options.
                final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Image");
                galleryIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(chooserIntent, Constants.PICK_FROM_GALLREY);

                CommonUtils.hideKeyboard(mActivity, contentEditText);
            }
        });

        // 확인 버튼 Listener
        Button okBtn = findViewById(R.id.activity_update_bottom_ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultipartBody.Part body = null;
                if(imageFile != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), imageFile);
                    body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);
                } else {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), "");
                    body = MultipartBody.Part.createFormData("file", "Not Exist", requestFile);
                }

                String content = contentEditText.getText().toString();
                Long id = shareItemId;

                RequestBody items = RequestBody.create(MediaType.parse("application/json"), "{content: \"" + content + "\", id: " + id + "}");

                updateShareItem(body, items);
            }
        });

        // 취소 버튼 Listener
        Button cancelBtn = findViewById(R.id.activity_update_bottom_cancel_button);
        cancelBtn.setOnClickListener(backBtnListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.PICK_FROM_GALLREY:
                if(resultCode == RESULT_OK) {
                    /**
                     * 앨범에서 사진 선택
                     */
                    Uri imageUri = data.getData();
                    Log.d("imageCapture", imageUri.getPath());

                    // 실제 파일이 존재하는 경로
                    String[] proj = { MediaStore.Images.Media.DATA };
                    Cursor cursor = mActivity.getContentResolver().query(imageUri, proj, null, null, null);
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();

                    String imagePath = cursor.getString(columnIndex);

                    // 이미지뷰에 보여주기
                    imageFile = new File(imagePath);
                    Picasso.get().load(imageFile).fit().into(backgroundImageView);
                }
        }
    }

    // API
    private void updateShareItem(final MultipartBody.Part body, final RequestBody items) {
        Call<Void> call = RetrofitHelper.getInstance().getRetrofitService().updateShareItem(body, items);
        Callback<Void> callback = new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Intent intent = new Intent();
                    setResult(Constants.RESULT_OK, intent);
                    finish();
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
