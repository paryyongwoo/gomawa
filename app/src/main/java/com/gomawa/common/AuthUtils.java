package com.gomawa.common;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gomawa.activity.MainActivity;
import com.gomawa.dto.Member;
import com.gomawa.network.RetrofitHelper;
import com.kakao.network.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.nhn.android.naverlogin.OAuthLogin;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthUtils {
    // 로그인 서비스 Enum
    public enum Services {
        NONE, NAVER, KAKAO;
    }

    // 현재 어느 서비스에 로그인 되어있는 지 기록되는 변수 ( 초기값 NONE ) - NONE 은 로그인이 되어있지 않음을 의미하기도 함
    private static Services services = Services.NONE;

    // services setter ( 로그인하는 액티비티에서만 접근 가능 ) - 함수를 호출하는 Activity 를 받아와서 검사함
    public static void setServices(Services services, Activity mActivity) {
        if(mActivity == accessibleActivity) {
            // 로그인 기능이 있는 액티비티에서 접근했을 경우 정상 처리
            AuthUtils.services = services;
        } else {
            // 그 외 액티비티에서 접근했을 경우 ( 비정상적인 접근 )
            // TODO: 2020-04-11 다른 액티비티에서 services 변수를 set 하려 했을 때의 처리
            Toast.makeText(mActivity, "비정상적인 접근", Toast.LENGTH_SHORT).show();
        }
    }

    // services getter
    public static Services getServices() {
        return services;
    }

    // services 변수의 setter 함수 호출이 가능한 Activity ( 로그인 기능이 있는 Activity ) + accessibleActivity 의 setter
    private static Activity accessibleActivity = null;
    public static void setAccessibleActivity(Activity mActivity) {
        accessibleActivity = mActivity;
    }

    // OAuthLoginModule
    private static OAuthLogin mOAuthLoginModule = OAuthLogin.getInstance();

    // 로그아웃
    public static void logout(Context mContext, final Activity mActivity) {
        switch (services) {
            case NONE:
                // 로그인이 되어 있지 않은 상황에서 로그아웃 호출 : 에러
                Toast.makeText(mContext, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                return;
            case NAVER:
                // 네이버 로그아웃
                mOAuthLoginModule.logout(mContext);

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("isLogin", Context.MODE_PRIVATE);
                // 카카오는 자동 로그인이 존재하므로 네이버만
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoginNaver", false);

                editor.commit();

                // 액티비티 이동
                Intent intent = new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(intent);
                mActivity.finish();
                break;
            case KAKAO:
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        // 로그아웃이 완료되었을 때의 처리

                        // 액티비티 이동
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        mActivity.startActivity(intent);
                        mActivity.finish();
                    }
                });
                break;
        }

        // 로그아웃이 완료되면 services 값 변경
        services = Services.NONE;
    }

    // 회원 탈퇴
    public static void unLink(final Context mContext, final Activity mActivity) {
        switch (services) {
            case NONE:
                // 로그인이 되어 있지 않은 상황에서 회원 탈퇴 호출 : 에러
                Toast.makeText(mContext, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                break;
            case NAVER:
                // 네이버 회원 탈퇴는 asyncTask 를 이용해야함 - 그렇지 않으면 android.os.NetworkOnMainThreadException 발생
                // 무조건 로그아웃은 진행되니 회원 탈퇴 여부와 관계없이 isUnLinkSuccess 를 true 로 줘 액티비티를 이동시킴
                // SharedPreference
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("isLogin", Context.MODE_PRIVATE);
                // 카카오는 자동 로그인이 존재하므로 네이버만
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoginNaver", false);

                editor.commit();

                // 로그아웃 & 회원탈퇴
                new DeleteTokenTask().execute(mContext, mActivity);
                break;
            case KAKAO:
                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                    @Override
                    public void onSuccess(Long result) {
                        // DB 에서 Member 삭제 후 액티비티 이동
                        deleteMemberByKey(mContext, mActivity);
                    }

                    // TODO: 2020-04-11 카카오 회원 탈퇴 실패 시 예외 처리
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        // 회원 탈퇴 실패 ( 인터넷 문제의 경우 여기서 처리 )
                        int result = errorResult.getErrorCode();

                        if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                            Toast.makeText(mContext, "네트워크 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "회원 탈퇴 실패 : " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        // 회원 탈퇴 실패 ( 로그인이 안 되어 있을 경우가 여기 속함 )
                        Toast.makeText(mContext, "회원 탈퇴 실패 : " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNotSignedUp() {
                        // 회원 탈퇴 실패 ( 가입되어있지 않은 아이디로 로그인 했을 경우 )
                        Toast.makeText(mContext, "회원 탈퇴 실패 : 가입되지 않은 아이디입니다.", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    // 네이버 회원 탈퇴 Task
    public static class DeleteTokenTask extends AsyncTask {
        boolean isSuccessDeleteToken = true;

        @Override
        protected Void doInBackground(Object[] objects) {
            // mContext & mActivity
            Context mContext = (Context) objects[0];
            Activity mActivity = (Activity) objects[1];

            isSuccessDeleteToken = mOAuthLoginModule.logoutAndDeleteToken(mContext);
            Log.d("로그아웃", "성공");

            Log.d("login", "토큰삭제완료");
            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
                // 회원 탈퇴 실패 후 유저에게 보여주는 메세지
                Toast.makeText(mContext, "회원 탈퇴에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();

                Log.d("login", "errorCode:" + mOAuthLoginModule.getLastErrorCode(mContext));
                Log.d("login", "errorDesc:" + mOAuthLoginModule.getLastErrorDesc(mContext));
            }

            // DB 에서 Member 삭제
            deleteMemberByKey(mContext, mActivity);

            return null;
        }
    }

    // DB 에서 Member 삭제해주는 메소드
    private static void deleteMemberByKey(final Context mContext, final Activity mActivity) {
        // 삭제할 멤버 ( 현재 로그인 되어 있는 멤버 )
        Member member = Data.getMember();

        // 해당 멤버의 Key 값
        Long key = member.getKey();

        // 서버와 통신
        Call<Void> call = RetrofitHelper.getInstance().getRetrofitService().deleteMemberByKey(key);
        Callback<Void> callback = new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    // services 변경
                    services = Services.NONE;

                    // 회원 탈퇴 성공
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mActivity.startActivity(intent);

                    mActivity.finish();
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
