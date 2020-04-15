package com.gomawa.application;

import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;

// 카카오 SDK 를 init 해주기 위한 Application
public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSDK.init(new KakaoAdapter() {
            @Override
            public IApplicationConfig getApplicationConfig() {
                return new IApplicationConfig() {
                    @Override
                    public Context getApplicationContext() {
                        return GlobalApplication.this;
                    }
                };
            }

            public ISessionConfig getSessionConfig() {
                return new ISessionConfig() {
                    @Override
                    public AuthType[] getAuthTypes() {
                        return new AuthType[] {AuthType.KAKAO_LOGIN_ALL};
                    }

                    // 웹뷰로 로그인할 때 타이머를 설정한다
                    @Override
                    public boolean isUsingWebviewTimer() {
                        return false;
                    }

                    // 로그인시 access token과 refresh token을 저장할 때의 암호화 여부를 결정한다.
                    @Override
                    public boolean isSecureMode() {
                        return false;
                    }

                    @Nullable
                    @Override
                    public ApprovalType getApprovalType() {
                        return null;
                    }

                    @Override
                    public boolean isSaveFormData() {
                        return false;
                    }
                };
            }
        });
    }
}