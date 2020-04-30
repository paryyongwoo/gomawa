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
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.gomawa.R;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.Constants;
import com.gomawa.common.ImageUtils;
import com.gomawa.dialog.VerticalTwoButtonDialog;
import com.gomawa.network.RetrofitHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

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

    // 사진 가져오기 다이얼로그
    private VerticalTwoButtonDialog getImageDialog = null;

    // 선택하고, Crop된 이미지 파일이 임시로 저장되는 변수
    private File tempFile = null;

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

        // 백 버튼 Listener
        ImageButton backBtn = findViewById(R.id.activity_update_header_backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(Constants.RESULT_CANCEL, intent);
                finish();
            }
        });

        // 타이틀 설정
        TextView titleTextView = findViewById(R.id.activity_update_header_title);
        titleTextView.setText("글 수정");

        // 프로필 이미지 설정
        CircleImageView profileImageView = findViewById(R.id.activity_update_shareItem_header_profile_imageView);
        ImageUtils.setProfileImageOnCircleImageView(mContext, profileImageView, CommonUtils.getMember().getProfileImgUrl());

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

        // EditText Focus & 키보드 올리기
        CommonUtils.showKeyboard(mActivity, contentEditText);

        // 배경 이미지 Listener
        backgroundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 갤러리에서 가져오기 버튼 Listener
                View.OnClickListener fromGalleryBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /**
                         * 갤러리에서 가져오기 로직
                         * 1. 갤러리 액티비티에서 이미지 가져옴 ( Uri 로 가져옴 )
                         * 2. Uri 를 이용해 originalFile 에 가져온 이미지 저장
                         * 3. gomawa_temp 폴더에 새로운 이미지 파일을 생성 ( tempFile 에 저장 )
                         * 4. originalFile => tempFile ( Copy )
                         * 5. tempFile 을 매개로 CROP 진행
                         * 6. CROP 여부와 관계없이 tempFile 을 서버로 전달, S3 에 저장하고 DB에 url 저장 ( API will return Member DTO )
                         * 7. Member DTO 를 이용해 세팅 프래그먼트의 프로필 이미지 뷰 설정
                         * 8. 임시 이미지 파일 삭제
                         */
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, Constants.PICK_FROM_GALLREY);

                        // 다이얼로그 종료
                        getImageDialog.dismiss();
                    }
                };

                // 카메라에서 가져오기 버튼 Listener
                View.OnClickListener fromCameraBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /**
                         * 카메라에서 가져오기 로직
                         * 1. gomawa_temp 폴더에 새로운 이미지 파일을 생성 ( tempFile 에 저장 )
                         * 2. 사진 촬영 액티비티에서 사진 촬영
                         * 3. 촬영 취소 ~ tempFile 을 이용해 만든 이미지 파일 삭제
                         * 4. 촬영 완료 ~ tempFile 을 매개로 CROP 진행
                         * 5. CROP 여부와 관계없이 tempFile 을 서버로 전달, S3 에 저장하고 DB에 url 저장 ( API will return Member DTO )
                         * 6. Member DTO 를 이용해 세팅 프래그먼트의 프로필 이미지 뷰 설정
                         * 7. 이미지 파일 삭제
                         * Result : S3 에 이미지 저장, DB 에 url 저장, Storage 에는 남은 게 없음
                         */
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        // 빈 이미지 파일 만들기
                        try {
                            tempFile = ImageUtils.createShareItemBackgroundImageFile();
                        } catch(IOException e) {
                            Toast.makeText(mContext, "이미지 파일 생성 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                            getImageDialog.dismiss();
                        }

                        if(tempFile != null) {
                            Uri uri = FileProvider.getUriForFile(mContext, "com.gomawa.fileprovider", tempFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            startActivityForResult(intent, Constants.PICK_FROM_CAMERA);
                        }

                        // 다이얼로그 종료
                        getImageDialog.dismiss();
                    }
                };

                // 다이얼로그 인스턴스를 생성한 후에 띄워줌
                getImageDialog = new VerticalTwoButtonDialog(mContext, fromCameraBtnListener, fromGalleryBtnListener, "프로필 사진", "카메라에서 가져오기", "갤러리에서 가져오기");
                getImageDialog.show();
            }
        });

        // 완료 버튼 Listener
        Button okBtn = findViewById(R.id.activity_update_header_okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultipartBody.Part body = null;
                if(tempFile != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), tempFile);
                    body = MultipartBody.Part.createFormData("file", tempFile.getName(), requestFile);
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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Constants.RESULT_CANCEL, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.PICK_FROM_GALLREY) {
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(mContext, "이미지 선택이 취소되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                // 이미지를 선택했을 때
                // uri를 path로 전환
                Uri photoUri = data.getData();

                Cursor cursor = null;

                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = mActivity.getContentResolver().query(photoUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                // 선택한 이미지를 originalFile 에 저장
                File originalFile = new File(cursor.getString(column_index));

                // 새로운 이미지 파일 생성 ( 임시 작업용 )
                try {
                    tempFile = ImageUtils.createShareItemBackgroundImageFile();

                    // 선택한 이미지를 tempFile에 복사
                    File copiedFile = CommonUtils.copyFile(originalFile, tempFile);

                    if (copiedFile.exists()) {
                        // 원본 이미지 보호를 위해 복사된 이미지 파일로 CROP 과정이 진행됨
                        Intent intent = ImageUtils.setIntentToCrop(mContext, tempFile);

                        // CROP 액티비티 실행
                        startActivityForResult(intent, Constants.CROP_IMAGE);
                    }
                } catch(IOException e) {
                    Toast.makeText(mContext, "이미지 파일 생성 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            // 카메라에서 이미지 가져온 후 호출됨
        }else if(requestCode == Constants.PICK_FROM_CAMERA) {
            if(resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(mContext, "사진 촬영이 취소되었습니다.", Toast.LENGTH_SHORT).show();

                // 임시 이미지 파일 삭제
                tempFile.delete();
            } else {
                if(tempFile.exists()) {
                    // 원본 이미지 보호를 하지 않음 = 찍은 사진이 CROP되어 저장됨
                    Intent intent = ImageUtils.setIntentToCrop(mContext, tempFile);

                    // CROP 액티비티 실행
                    startActivityForResult(intent, Constants.CROP_IMAGE);
                }
            }
            // CROP 액티비티 후에 실행
        }else if(requestCode == Constants.CROP_IMAGE) {
            // 이미지 뷰
            Picasso.get().load(tempFile).into(backgroundImageView);
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
