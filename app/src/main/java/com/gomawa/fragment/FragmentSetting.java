package com.gomawa.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.gomawa.R;
import com.gomawa.common.Global;
import com.gomawa.activity.NicknameActivity;
import com.gomawa.dialog.PickImageDialog;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentSetting extends Fragment {
    // mActivity & mContext
    Activity mActivity;
    Context mContext;

    ViewGroup rootView;

    // 닉네임이 표시되는 TextView
    TextView nicknameTextView;

    // 갤러리 또는 카메라를 선택하는 다이얼로그
    PickImageDialog pickImageDialog;

    // 선택하고, Crop된 이미지 파일이 임시로 저장되는 변수
    File tempFile = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);

        mActivity = getActivity();
        mContext = getContext();

        initView();

        return rootView;
    }

    private void initView() {
        nicknameTextView = rootView.findViewById(R.id.fragment_setting_nickname_textView);

        // 닉네임을 DB에서 가져와서 표시 (DB 없어서 임시로 Global에서 가져옴)
        nicknameTextView.setText(Global.nickname);

        // 프로필 사진 변경 버튼 Listener
        ImageButton profileImageBtn = rootView.findViewById(R.id.fragment_setting_profileImage_btn);
        profileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 갤러리에서 가져오기 버튼 Listener
                View.OnClickListener fromGalleryBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, Global.PICK_FROM_GALLREY);
                    }
                };

                // 카메라에서 가져오기 버튼 Listener
                View.OnClickListener fromCameraBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                };

                // 다이얼로그 인스턴스를 생성한 후에 띄워줌
                pickImageDialog = new PickImageDialog(mContext, fromGalleryBtnListener, fromCameraBtnListener);
                pickImageDialog.show();
            }
        });

        // 닉네임 변경 버튼 Listener
        ImageButton nicknameBtn = rootView.findViewById(R.id.fragment_setting_nickname_btn);
        nicknameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), NicknameActivity.class);
                i.putExtra("nowNickname", Global.nickname);
                startActivityForResult(i, Global.REQUEST_RESULT);
            }
        });

        // 헤더 타이틀과 서브타이틀 Text 초기화
        TextView headerTitle = rootView.findViewById(R.id.header_title);
        headerTitle.setText("설정");
        TextView headerSubTitle = rootView.findViewById(R.id.header_subtitle);
        headerSubTitle.setText("");

        // 평점주기 버튼 Listener
        ImageButton rateBtn = rootView.findViewById(R.id.fragment_setting_rate_btn);
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // todo: 아래 주석 처리 된 두 라인은 향후에 플레이스토어에 우리 어플이 등록되면 사용하면 됨.
                // 바로 우리 어플로 이어짐
                //String packageName = mActivity.getPackageName();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setData(Uri.parse("market://details?id=" + packageName));
                intent.setData(Uri.parse("http://play.google.com/store/search?q=" + Global.APPNAME + "&c=apps"));

                startActivity(intent);
            }
        });

        // 문의하기 버튼 Listener
        ImageButton emailBtn = rootView.findViewById(R.id.fragment_setting_email_btn);
        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/Text");
                email.putExtra(Intent.EXTRA_EMAIL, Global.EMAIL);
                startActivity(email);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // NicknameActivity 종료 시 호출
        if (requestCode == Global.REQUEST_RESULT) {
            switch (resultCode) {
                // NicknameActivity - BackBtn
                case Global.RESULT_SUCESS:
                    break;
                // NicknameActivity - okBtn
                case Global.RESULT_SUCESS_NICKNAME:
                    Global.nickname = data.getExtras().getString("newNickname");
                    nicknameTextView.setText(Global.nickname);
                    break;
                // 비정상적인 종료
                default:
                    Toast.makeText(getContext(), "NicknameActivity가 비정상적으로 종료됨.", Toast.LENGTH_LONG).show();
            }
        // 갤러리에서 이미지 가져온 후 호출됨
        } else if(requestCode == Global.PICK_FROM_GALLREY) {
            if(resultCode == Activity.RESULT_CANCELED) {
                // 아무 이미지도 선택하지 않았을 때 (백 버튼)
            } else {
                // 이미지를 선택했을 때

                // uri를 path로 전환
                Uri photoUri = data.getData();

                Cursor cursor = null;

                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = mActivity.getContentResolver().query(photoUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                // 선택한 이미지를 tempFile에 저장
                String tempPath = cursor.getString(column_index);
                tempFile = new File(cursor.getString(column_index));

                // 선택한 이미지를 copiedFile에 복사
                String copiedPath = tempPath.replace(".", "2.");
                File copiedFile = Global.fileCopy(tempPath, copiedPath);

                if(copiedFile.exists()) {
                    // 원본 이미지 보호를 위해 복사된 이미지 파일로 CROP 과정이 진행됨
                    tempFile = copiedFile;
                    Uri uri = FileProvider.getUriForFile(mContext, "com.gomawa.fileprovider", tempFile);

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(uri, "image/*");
                    // 접근 권한을 주는 부분!
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 200);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

                    // CROP 액티비티 실행
                    startActivityForResult(intent, Global.CROP_FROM_GALLREY);
                }
            }

            // 다이얼로그 종료
            pickImageDialog.dismiss();
        // CROP 액티비티 후에 실행
        }else if(requestCode == Global.CROP_FROM_GALLREY) {
            // 프로필 이미지 파일에 tempFile 을 대입
            Global.profileImageFile = tempFile;

            // 프로필 이미지뷰의 이미지를 바꿔줌
            CircleImageView imageView = rootView.findViewById(R.id.fragment_setting_profileImage_imageView);
            Picasso.get().load(tempFile).into(imageView);

            // todo: 복사된 이미지 파일의 삭제가 이루어져야함. 나중에 S3에 복사된 이미지를 저장한 후에 삭제하는 코드를 적으면 되지 않을까 싶음
            // 지금 기능으로는 복사된 이미지 파일의 삭제가 이루어지지 않으니, 일일이 삭제해야함
        }
    }
}