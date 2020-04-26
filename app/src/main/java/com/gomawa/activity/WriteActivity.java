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
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.gomawa.R;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.Constants;
import com.gomawa.dto.ShareItem;
import com.gomawa.fragment.FragmentShare;
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
 * ShareItem 글쓰기 액티비티
 * todo 수정 액티비티와 합쳐보기
 */
public class WriteActivity extends Activity {
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
        setContentView(R.layout.activity_write);

        initView();
    }

    private void initView() {
        // 백 버튼 Listener
        ImageButton backBtn = findViewById(R.id.activity_write_header_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(Constants.RESULT_CANCEL, intent);
                finish();
            }
        });

        // 타이틀 설정
        TextView titleTextView = findViewById(R.id.activity_write_header_title);
        titleTextView.setText("글쓰기");

        // 프로필 이미지 설정
        CircleImageView profileImageView = findViewById(R.id.activity_write_shareItem_header_profile_imageView);
        //String profileImageUrl = CommonUtils.getMember().getProfileImgUrl();
        String profileImageUrl = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=https://k.kakaocdn.net/dn/EShJF/btquPLT192D/SRxSvXqcWjHRTju3kHcOQK/img.png"; // 기능 미 구현, 임시 이미지의 URL
        Glide.with(mContext).load(profileImageUrl).into(profileImageView);

        // 닉네임 설정
        TextView nickNameTextView = findViewById(R.id.activity_write_shareItem_header_nickName);
        String nickName = CommonUtils.getMember().getNickName();
        nickNameTextView.setText(nickName);

        // 날짜 설정 ( 글쓰기에선 사용되지 않음 )
//        TextView dateTextView = findViewById(R.id.activity_write_shareItem_header_date);
//        String regDateString =
//        dateTextView.setText(regDateString);

        // 배경 이미지 설정
        backgroundImageView = findViewById(R.id.activity_write_shareItem_body_imageView);
        backgroundImageView.setImageResource(R.drawable.share_item_background);


        // 본문 설정
        contentEditText = findViewById(R.id.activity_write_shareItem_body_content);
        contentEditText.setText("");

        // EditText Focus & 키보드 올리기
        CommonUtils.showKeyboard(mActivity, contentEditText);

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

        // 완료 버튼 Listener
        Button okBtn = findViewById(R.id.activity_write_header_okBtn);
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

                RequestBody items = RequestBody.create(MediaType.parse("application/json"), "{content: \"" + content + "\", key: " + CommonUtils.getMember().getKey() + "}");

                addShareItem(body, items);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Constants.RESULT_CANCEL, intent);
        finish();
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
    private void addShareItem(final MultipartBody.Part body, final RequestBody items) {
        Call<ShareItem> call = RetrofitHelper.getInstance().getRetrofitService().addShareItem(body, items);
        Callback<ShareItem> callback = new Callback<ShareItem>() {
            @Override
            public void onResponse(Call<ShareItem> call, Response<ShareItem> response) {
                if (response.isSuccessful()) {
                    // 글 입력에 성공하면 액티비티 종료
                    Intent intent = new Intent();
                    setResult(Constants.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(mContext, "setShareItem failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShareItem> call, Throwable t) {
                Toast.makeText(mContext, "failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("api", t.getMessage());
            }
        };
        call.enqueue(callback);
    }
}
