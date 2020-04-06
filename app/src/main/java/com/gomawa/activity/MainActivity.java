package com.gomawa.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gomawa.R;
import com.gomawa.common.CommonUtils;
import com.gomawa.dto.Member;
import com.gomawa.network.RetrofitHelper;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // 상수
    private static final String OAUTH_CLIENT_ID = "bTO1NhHHokGVVEH3Ispo";
    private static final String OAUTH_CLIENT_SECRET = "gKxHB66vuC";
    private static final String OAUTH_CLIENT_NAME = "Gomawa";

    // 로그인 관련 인스턴스
    public static OAuthLogin mOAuthLoginModule = OAuthLogin.getInstance();

    // 컨텍스트
    private Context mContext;

    // 뷰객체
    private Button loginBtnNaver;

    // objectMapper
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 스플래쉬 이미지 설정
        setTheme(R.style.SplashTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        initView();

        /**
         * 퍼미션 설정
         */
        tedPermission();

        // 네이버 로그인 초기화 작업
        mOAuthLoginModule.init(
                MainActivity.this
                ,OAUTH_CLIENT_ID
                ,OAUTH_CLIENT_SECRET
                ,OAUTH_CLIENT_NAME
        );

        // 카카오 로그인 초기화 작업
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
        CommonUtils.onBackPressedCheck(mContext, this);
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

    // 로그인을 처리할 핸들러
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            // Naver 로그인 성공 & Token을 받는 데 성공
            if (success) {
                String accessToken = mOAuthLoginModule.getAccessToken(mContext);

                // 발급받은 accessToken을 가지고 네이버 프로필 정보를 얻는 요청을 보낸다.
                new RequestApiTask().execute(accessToken);
            } else {
                // TODO: 2020-02-02 로그인 실패 처리
            }
        }
    };

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

                Map<String, Object> map = new HashMap<>();
                map.put("response", response);

                return map;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object map) {
            super.onPostExecute(map);

            // 로그인 처리 완료 후에 실행될 로직
            processAuthResult((Map)map);
        }
    }

    private void processAuthResult(Map map) {
        try {
            Long key;
            String email = null;
            String gender = null;

            // response decoding
            String response = map.get("response").toString();
            JSONObject object = new JSONObject(response);
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

                String[] emailForSplit = email.split("@");

                member.setNickName(emailForSplit[0]);

                Log.d("초기 닉네임", emailForSplit[0]);

                // Member 객체 생성 todo: 왜 이 타이밍에 CommonUtils 에 등록하는가
                // CommonUtils.setMember(member);

                // addMemberOnStart 메소드 호출로 addMember와 동시에 닉네임을 설정함
                Call<Member> call = RetrofitHelper.getInstance().getRetrofitService().addMemberOnStart(member);
                Callback<Member> callback = new Callback<Member>() {
                    @Override
                    public void onResponse(Call<Member> call, Response<Member> response) {
                        if (response.isSuccessful()) {
                            // DB 작업이 성공적이면 받은 Member 를 CommonUtils의 Member로 설정함
                            Member receiveMember = response.body();
                            CommonUtils.setMember(receiveMember);

                            Log.d("##### Member ", CommonUtils.getMember().toString());

                            Intent intent = new Intent(mContext, ShareActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d("api 응답은 왔으나 실패", "status: " + response.code());
                            int status = response.code();
                            if (status == 403) {
                                Toast.makeText(MainActivity.this, "로그인 인증 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Member> call, Throwable t) {
                        Log.d("api 로그인 통신 실패", t.getMessage());

                        // 개발용 : 로그인 실패해도 액티비티 전환
                        // Intent intent = new Intent(mContext, ShareActivity.class);
                        // startActivity(intent);
                    }
                };
                call.enqueue(callback);

            } else {
                // 로그인 실패
                new DeleteTokenTask().execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description
     * 로그아웃 및 토큰 삭제
     */
    private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
        boolean isSuccessDeleteToken = true;
        @Override
        protected Void doInBackground(Void... voids) {
            isSuccessDeleteToken = mOAuthLoginModule.logoutAndDeleteToken(mContext);

            Log.d("login", "토큰삭제완료");
            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
                Log.d("login", "errorCode:" + mOAuthLoginModule.getLastErrorCode(mContext));
                Log.d("login", "errorDesc:" + mOAuthLoginModule.getLastErrorDesc(mContext));
            }
            return null;
        }
    }
}
