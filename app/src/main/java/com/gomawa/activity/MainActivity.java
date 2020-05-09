package com.gomawa.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gomawa.R;
import com.gomawa.common.AuthUtils;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.Data;
import com.gomawa.dto.DailyThanks;
import com.gomawa.dto.Member;
import com.gomawa.network.RetrofitHelper;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.Utility;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    // 상수
    private static final String OAUTH_CLIENT_ID = "bTO1NhHHokGVVEH3Ispo";
    private static final String OAUTH_CLIENT_SECRET = "gKxHB66vuC";
    private static final String OAUTH_CLIENT_NAME = "Gomawa";

    // 네이버 로그인 관련 인스턴스
    public static OAuthLogin mOAuthLoginModule = OAuthLogin.getInstance();

    // 카카오 로그인 콜백 클래스
    private SessionCallback sessionCallback;

    // 컨텍스트 & 액티비티
    private Context mContext;
    private Activity mActivity;

    // 뷰객체
    private Button loginBtnNaver;

    // 로그인을 처리할 핸들러
    private OAuthLoginHandler mOAuthLoginHandler;

    public MainActivity() {
        mOAuthLoginHandler = new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                // Naver 로그인 성공 & Token을 받는 데 성공
                if (success) {
                    String accessToken = mOAuthLoginModule.getAccessToken(mContext);

                    // AuthUtils.services 의 값을 네이버로 변경
                    AuthUtils.setServices(AuthUtils.Services.NAVER, mActivity);

                    // 발급받은 accessToken을 가지고 네이버 프로필 정보를 얻는 요청을 보낸다.
                    new RequestApiTask().execute(accessToken);
                } else {
                    // TODO: 2020-02-02 로그인 실패 처리
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 스플래쉬 이미지 설정
        setTheme(R.style.SplashTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mActivity = this;

        initView();

        /**
         * 퍼미션 설정
         */
        tedPermission();

        // services 변수 접근을 위한 accessibleActivity 설정
        AuthUtils.setAccessibleActivity(this);

        // 네이버 로그인 초기화 작업
        mOAuthLoginModule.init(
                MainActivity.this
                ,OAUTH_CLIENT_ID
                ,OAUTH_CLIENT_SECRET
                ,OAUTH_CLIENT_NAME
        );

        // 카카오 로그인 초기화 작업
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        // 자동 로그인 - 카카오로 로그인한 후 로그아웃 없이 종료할 경우 자동으로 로그인 됨
        Session.getCurrentSession().checkAndImplicitOpen();


        // 네이버 자동로그인을 막기 위해 SharePreferences 입력
        SharedPreferences sharedPreferences = getSharedPreferences("isLogin", MODE_PRIVATE);
        Boolean isLoginNaver = sharedPreferences.getBoolean("isLoginNaver", false);
        if(isLoginNaver) {
            mOAuthLoginModule.startOauthLoginActivity(MainActivity.this, mOAuthLoginHandler);
        }

        // 키 해시 값을 로그로 출력함
        Log.d("KeyHash", getKeyHash(mContext));
    }

    private void initView() {
        loginBtnNaver = (Button)findViewById(R.id.loginBtnNaver);
        loginBtnNaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOAuthLoginModule.startOauthLoginActivity(MainActivity.this, mOAuthLoginHandler);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        CommonUtils.onBackPressedCheck(mContext, mActivity);
    }
    /**
     * 네이버 액세스 토큰 받아온 후, 프로필 정보 가져올 태스크
     */
    private class RequestApiTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            String header = "Bearer " + objects[0];

            try {
                final String apiURL = "https://openapi.naver.com/v1/nid/me"; // 네이버 프로필 정보 얻어오는 주소
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", header);
                int responseCode = con.getResponseCode();

                BufferedReader br = new BufferedReader(new InputStreamReader(responseCode == 200 ? con.getInputStream() : con.getErrorStream()));

                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }

                br.close();

                    Long key;
                    String email = null;
                    String gender = null;

                    // response decoding
                    JSONObject object = new JSONObject(response.toString());
                    JSONObject innerJson = new JSONObject(object.get("response").toString());
                    Log.d("login inner", innerJson.toString());

                    /**
                     * @description
                     * 이메일 제공에 동의하지 않은 경우 > 다이얼로그를 띄우고 로그아웃 및 세션을 삭제 요청을 한다
                     * 이메일 제공에 동의한 경우 > ShareActivity로 이동한다.
                     */
                    if (innerJson.has("id")) {
                        key = innerJson.getLong("id");

                        if (innerJson.has("email")) email = innerJson.getString("email");
                        if (innerJson.has("gender")) gender = innerJson.getString("gender");

                        // 완성된 사용자 객체
                        final Member member = new Member();
                        member.setKey(key);
                        member.setEmail(email);
                        member.setGender(gender);
                        // 현재 날짜를 저장함
                        member.setRegDate(new Date());

                        // 디폴트 닉네임
                        String[] emailForSplit = email.split("@");

                        member.setNickName(emailForSplit[0]);

                        // DB에 멤버 등록 및 서버로부터 DailyThanks 정보 가져오기
                        boolean isSuccess = addMemberAndDailyThanks(member);

                        return isSuccess;
                    } else {
                        // 로그인 실패
                        // 네이버 로그아웃
                        mOAuthLoginModule.logout(mContext);
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            /**
             * 로그인 성공 여부
             * 1. 로그인 성공: 액티비티 전환
             * 2. 로그인 실패: 에러 메시지 출력
             */
            boolean isSuccess = (boolean)o;
            if (isSuccess) {
                // 액티비티 이동
                Intent intent = new Intent(mContext, ShareActivity.class);
                startActivity(intent);
            } else {
                // TODO: 2020/05/09 에러 문구를 다이얼로그로 표시하게 변경하기
                Toast.makeText(mContext, "사용자 정보를 불러오는데 실패했습니다. 앱을 재실행 해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 카카오 로그인 콜백 클래스
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            // 로그인 세션이 정상적으로 열렸을 때
            // 유저 정보를 받아오는 함수 = me()
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    // 로그인이 실패했을 때
                    int result = errorResult.getErrorCode();

                    if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(mContext, "네트워크 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "로그인 도중 오류가 발생했습니다. : " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    // 로그인 도중 세션이 비정상적인 이유로 닫혔을 때 - 거의 없다고 함
                    Toast.makeText(mContext, "로그인 도중 오류가 발생했습니다. : " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    // 로그인 성공
                    // services 를 KAKAO 로 변경 후 Task 실행
                    AuthUtils.setServices(AuthUtils.Services.KAKAO, mActivity);
                    new KakaoLoginTask().execute(result);
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 로그인 세션이 정상적으로 열리지 않았을 때
            // todo: 예외 처리
            Toast.makeText(mContext, "카카오 로그인 세션이 비정상적으로 실행되었습니다. " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //
    private class KakaoLoginTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                // 로그인 성공 시 카카오에서 받아온 결과
                MeV2Response result = (MeV2Response) objects[0];

                // 카카오 유저 정보가 담겨있음
                UserAccount userAccount = result.getKakaoAccount();

                Member member = new Member();

                // Properties 에 닉네임 값이 담겨있음.
                Map<String, String> properties = result.getProperties();
                member.setNickName(properties.get("nickname"));
                member.setProfileImgUrl(properties.get("profile_image")); // 프로필 이미지가 기본 이미지면 null 값이 입력됨
                member.setKey(result.getId());

                // 현재 날짜를 회원 가입 날짜로 저장
                member.setRegDate(new Date());

                // 이메일 과 성별 수집 동의 여부 - 동의하지 않았으면 "null" 입력
                if(userAccount.emailNeedsAgreement() == OptionalBoolean.FALSE) {
                    member.setEmail(userAccount.getEmail());
                } else {
                    member.setEmail("null");
                }

                if(userAccount.genderNeedsAgreement() == OptionalBoolean.FALSE) {
                    member.setGender(userAccount.getGender().getValue());
                } else {
                    member.setGender("null");
                }

                // DB에 멤버 등록 및 서버로부터 DailyThanks 정보 가져오기
                boolean isSuccess = addMemberAndDailyThanks(member);

                return isSuccess;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            /**
             * 로그인 성공 여부
             * 1. 로그인 성공: 액티비티 전환
             * 2. 로그인 실패: 에러 메시지 출력
             */
            boolean isSuccess = (boolean)o;
            if (isSuccess) {
                // 액티비티 이동
                Intent intent = new Intent(mContext, ShareActivity.class);
                startActivity(intent);
            } else {
                // TODO: 2020/05/09 에러 문구를 다이얼로그로 표시하게 변경하기
                Toast.makeText(mContext, "사용자 정보를 불러오는데 실패했습니다. 앱을 재실행 해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 1. 네이버, 카카오 로그인 성공시 가져온 사용자 정보를 Member 객체로 만들어서 서버에 저장 및 Data에 저장
     * 2. 서버에서 DailyThanks 정보를 받아와서 Data에 저장
     */
    private boolean addMemberAndDailyThanks(Member member) throws IOException {
        // 1. 네이버, 카카오 로그인 성공시 가져온 사용자 정보를 Member 객체로 만들어서 서버에 저장 및 Data에 저장
        Call<Member> call = RetrofitHelper.getInstance().getRetrofitService().addMemberOnStart(member);
        Response<Member> callRes = call.execute();
        if (callRes.isSuccessful()) {
            Member memberReceived = callRes.body();
            Data.setMember(memberReceived);

            // 2. 서버에서 DailyThanks 정보를 받아와서 Data에 저장
            Call<DailyThanks> getDailyThanksCall = RetrofitHelper.getInstance().getRetrofitService().getDailyThanks(memberReceived.getId());
            Response<DailyThanks> getDailyThanksCallRes = getDailyThanksCall.execute();

            if (getDailyThanksCallRes.isSuccessful()) {
                DailyThanks dailyThanks = getDailyThanksCallRes.body();
                Data.setDailyThanks(dailyThanks);

                return true;
            }
        } else {
            // todo: 예외 처리
            Log.d("api 응답은 왔으나 실패", "status: " + callRes.code());
            int status = callRes.code();
            if (status == 403) {
                Toast.makeText(MainActivity.this, "로그인 인증 실패", Toast.LENGTH_SHORT).show();
            }

            // 카카오 로그아웃
            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {}
            });
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 카카오 로그인 액티비티에서 돌아올 때
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void tedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 카카오 로그인을 위해 열어둔 Callback 종료
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    // 디버깅용 키 해시 값 얻는 메소드 - 카카오 로그인 시 등록 필요함
    // 복사 붙여넣기 해서 코드의 상세내용은 모름
    public String getKeyHash(final Context context) {
        PackageInfo packageInfo = Utility.getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;
        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
