package com.gomawa.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gomawa.R;
import com.gomawa.activity.ImageActivity;
import com.gomawa.activity.NicknameActivity;
import com.gomawa.activity.NoticeActivity;
import com.gomawa.adapter.SettingRecyclerViewAdapter;
import com.gomawa.common.AuthUtils;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.Constants;
import com.gomawa.common.Data;
import com.gomawa.common.ImageUtils;
import com.gomawa.dialog.HorizontalTwoButtonDialog;
import com.gomawa.dialog.VerticalTwoButtonDialog;
import com.gomawa.dto.Member;
import com.gomawa.network.RetrofitHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentSetting extends Fragment {
    // mActivity & mContext
    private Activity mActivity;
    private Context mContext;

    private ViewGroup rootView;

    // 프로필 이미지가 표시되는 ImageView
    private ImageView headerImageView = null;

    /**
     * 헤더의 메뉴 버튼 (화면 표시 안함) 및 서브 타이틀
     */
    private ImageButton headerMenuBtn = null;
    private TextView subTitleTextView = null;

    // 수평 2 버튼 다이얼로그
    private HorizontalTwoButtonDialog horizontalTwoButtonDialog = null;

    // 수직 2 버튼 다이얼로그
    private VerticalTwoButtonDialog verticalTwoButtonDialog = null;

    // 선택하고, Crop된 이미지 파일이 임시로 저장되는 변수
    private File tempFile = null;

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
        // 헤더 서브 텍스트 및 메뉴 화면 미표시 처리
        headerImageView = rootView.findViewById(R.id.header_imageView);
        headerImageView.setVisibility(View.VISIBLE);
        headerMenuBtn = rootView.findViewById(R.id.header_menu_button);
        headerMenuBtn.setVisibility(View.INVISIBLE);

        if(Data.getMember().getProfileImgUrl() == null) {
            headerImageView.setImageResource(R.drawable.share_item_background); // todo: 프로필 이미지
        } else {
            Picasso.get().load(Data.getMember().getProfileImgUrl()).fit().centerCrop().into(headerImageView);

            // 프로필 이미지 클릭 시 이미지 액티비티 실행
            headerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ImageActivity.class);
                    intent.putExtra("url", Data.getMember().getProfileImgUrl());
                    startActivity(intent);
                }
            });
        }

        // 헤더 타이틀 Text 초기화
        TextView headerTitle = rootView.findViewById(R.id.header_title);
        headerTitle.setText(getResources().getString(R.string.fragment_setting_header_title));

        // 헤더 서브 타이틀 Text 초기화
        subTitleTextView = rootView.findViewById(R.id.header_sub_title);
        subTitleTextView.setText(makeSubTitle());

        // 리사이클러 뷰에 들어갈 OnClickListener
        List<View.OnClickListener> onClickListenerList = new ArrayList<>();

        // Position 0: 프로필 사진 변경 Listener
        onClickListenerList.add(new View.OnClickListener() {
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
                        verticalTwoButtonDialog.dismiss();
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
                            tempFile = ImageUtils.createProfileImageFile();
                        } catch(IOException e) {
                            Toast.makeText(mContext, "이미지 파일 생성 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                            e.printStackTrace();

                            verticalTwoButtonDialog.dismiss();
                        }

                        if(tempFile != null) {
                            Uri uri = FileProvider.getUriForFile(mContext, "com.gomawa.fileprovider", tempFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            startActivityForResult(intent, Constants.PICK_FROM_CAMERA);
                        }

                        // 다이얼로그 종료
                        verticalTwoButtonDialog.dismiss();
                    }
                };

                // 다이얼로그 인스턴스를 생성한 후에 띄워줌
                verticalTwoButtonDialog = new VerticalTwoButtonDialog(mContext, fromCameraBtnListener, fromGalleryBtnListener, "프로필 사진", "카메라에서 가져오기", "갤러리에서 가져오기");
                verticalTwoButtonDialog.show();
            }
        });

        // Position 1: 닉네임 변경
        onClickListenerList.add(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), NicknameActivity.class);
                i.putExtra("nowNickname", Data.getMember().getNickName());
                startActivityForResult(i, Constants.REQUEST_RESULT);
            }
        });

        // Position 2: 공지사항
        onClickListenerList.add(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 공지사항 버튼은 데이터를 주고 받을 게 없으므로 startActivity 메소드를 사용함
                Intent intent = new Intent(mContext, NoticeActivity.class);
                startActivity(intent);
            }
        });

        // Position 3: 앱 평점 주기
        onClickListenerList.add(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // todo: 아래 주석 처리 된 두 라인은 향후에 플레이스토어에 우리 어플이 등록되면 사용하면 됨.
                // 바로 우리 어플로 이어짐
                //String packageName = mActivity.getPackageName();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setData(Uri.parse("market://details?id=" + packageName));
                intent.setData(Uri.parse("http://play.google.com/store/search?q=" + Constants.APPNAME + "&c=apps"));

                startActivity(intent);
            }
        });

        // Position 4: 문의하기
        onClickListenerList.add(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/Text");
                email.putExtra(Intent.EXTRA_EMAIL, Constants.EMAIL);
                startActivity(email);
            }
        });

        // Position 5: 로그아웃
        onClickListenerList.add(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 확인 버튼 Listener
                View.OnClickListener okBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 다이얼로그 종료
                        horizontalTwoButtonDialog.dismiss();

                        // 로그아웃
                        AuthUtils.logout(mContext, mActivity);
                    }
                };

                // 취소 버튼 Listener
                View.OnClickListener cancelBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        horizontalTwoButtonDialog.dismiss();
                    }
                };

                // 다이얼로그 인스턴스를 생성한 후에 띄워줌
                horizontalTwoButtonDialog = new HorizontalTwoButtonDialog(mContext, okBtnListener, cancelBtnListener, "로그아웃 하시겠습니까?", "확인", "취소");
                // 다이얼로그 애니메이션
                horizontalTwoButtonDialog.getWindow().setGravity(Gravity.BOTTOM);
                horizontalTwoButtonDialog.getWindow().setWindowAnimations(R.style.AnimationPopupStyle);
                horizontalTwoButtonDialog.show();
            }
        });

        // Position 6: (임시) 회원 탈퇴
        onClickListenerList.add(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUtils.unLink(mContext, mActivity);
            }
        });

        // 리사이클러 뷰
        RecyclerView recyclerView = rootView.findViewById(R.id.fragment_setting_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);

        SettingRecyclerViewAdapter recyclerViewAdapter = new SettingRecyclerViewAdapter(onClickListenerList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    // 서브타이틀 문자열을 만들어주는 메소드
    private String makeSubTitle() { return "안녕하세요, " + Data.getMember().getNickName() + "님!"; }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // NicknameActivity 종료 시 호출
        if (requestCode == Constants.REQUEST_RESULT) {
            switch (resultCode) {
                // NicknameActivity - BackBtn
                case Constants.RESULT_SUCESS:
                    break;
                // NicknameActivity - okBtn - textView 의 Text 값을 현재 닉네임으로 변경
                case Constants.RESULT_SUCESS_NICKNAME:
                    subTitleTextView.setText(makeSubTitle());
                    break;
                // 비정상적인 종료
                default:
                    Toast.makeText(getContext(), "NicknameActivity가 비정상적으로 종료됨.", Toast.LENGTH_LONG).show();
            }
        // 갤러리에서 이미지 가져온 후 호출됨
        } else if(requestCode == Constants.PICK_FROM_GALLREY) {
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(mContext, "프로필 이미지 변경이 취소되었습니다.", Toast.LENGTH_SHORT).show();
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
                    tempFile = ImageUtils.createProfileImageFile();

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
                Toast.makeText(mContext, "프로필 이미지 변경이 취소되었습니다.", Toast.LENGTH_SHORT).show();

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
            // 프로필 이미지 파일 서버로 보낼 준비
            MultipartBody.Part body = null;
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), tempFile);
            body = MultipartBody.Part.createFormData("file", tempFile.getName(), requestFile);

            RequestBody items = RequestBody.create(MediaType.parse("application/json"), "{id: " + Data.getMember().getId() + "}");

            Call<Member> call = RetrofitHelper.getInstance().getRetrofitService().updateMemberProfileImageUrl(body, items);
            Callback<Member> callback = new Callback<Member>() {
                @Override
                public void onResponse(Call<Member> call, Response<Member> response) {
                    if(response.isSuccessful()) {
                        Member member = response.body();

                        Data.setMember(member);

                        // 세팅 프래그먼트의 프로필 이미지를 바꿔줌
                        headerImageView.setImageResource(ImageUtils.DEFAULT_PROFILE_IMAGE);
                        Picasso.get().load(Data.getMember().getProfileImgUrl()).fit().centerCrop().into(headerImageView);
                    } else {
                        Log.d("api 응답은 왔으나 실패", "status: " + response.code());
                    }

                    // 임시 이미지 파일 삭제
                    tempFile.delete();
                }

                @Override
                public void onFailure(Call<Member> call, Throwable t) {
                    Log.d("api 로그인 통신 실패", t.getMessage());

                    // 임시 이미지 파일 삭제
                    tempFile.delete();
                }
            };
            call.enqueue(callback);
        }
    }
}