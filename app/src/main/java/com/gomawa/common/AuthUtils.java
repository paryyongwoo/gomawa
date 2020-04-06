package com.gomawa.common;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.data.OAuthLoginState;

public class AuthUtils {
    // OAuthLoginModule
    private static OAuthLogin mOAuthLoginModule = OAuthLogin.getInstance();

    // 로그인이 되어 있는 지 확인하는 메소드 - 로그인이 되어 있으면 true 반환 - Module 때문에 여기로 빼놓음
    public static boolean checkLoginState(Context mContext) {
        boolean result = true;

        if(mOAuthLoginModule.getState(mContext) == OAuthLoginState.NEED_LOGIN) { result = false; }

        return result;
    }

    // 로그아웃 메소드 - Module 때문에 여기로 빼놓음
    public static void logout(Context mContext) {
        mOAuthLoginModule.logout(mContext);
    }



    /**
     * @description
     * 로그아웃 및 토큰 삭제
     */
    public static class DeleteTokenTask extends AsyncTask<Context, Void, Void> {
        boolean isSuccessDeleteToken = true;

        @Override
        protected Void doInBackground(Context... contexts) {
            // contexts[0] 에는 매개변수로 받아온 Context가 들어있음
            isSuccessDeleteToken = mOAuthLoginModule.logoutAndDeleteToken(contexts[0]);
            Log.d("로그아웃", "성공");

            Log.d("login", "토큰삭제완료");
            if (!isSuccessDeleteToken) {
                // 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
                // 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
                Log.d("login", "errorCode:" + mOAuthLoginModule.getLastErrorCode(contexts[0]));
                Log.d("login", "errorDesc:" + mOAuthLoginModule.getLastErrorDesc(contexts[0]));
            }

            return null;
        }
    }
}
