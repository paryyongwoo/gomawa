package com.gomawa.common;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gomawa.dto.Member;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class CommonUtils {
    // 멤버
    private static Member member;

    // 뒤로가기 버튼 시간 측정 변수
    private static long backKeyPressedTime = 0;

    public static void setMember(Member m) {
        member = m;
    }

    public static Member getMember() {
        return member;
    }

    /**
     * 뒤로가기 한 번 더 확인한 후에 액티비티를 종료해주는 함수
     */
    public static void onBackPressedCheck(Context mContext, Activity mActivity) {
        if(System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(mContext, "뒤로가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();
            return;
        } else {
            if(AuthUtils.getServices() == AuthUtils.Services.NONE) {
                // 로그인이 되어있지 않다면 로그아웃 하지 않음
            } else {
                // 로그인이 되어있다면 로그아웃 처리
                AuthUtils.logout(mContext);
            }

            // 살아있는 액티비티가 하나 밖에 없으므로 finish 하면 앱이 종료됨
            mActivity.finish();
        }
    }

    /**
     * 문자열의 Length 를 계산해서 int 값으로 반환해주는 함수 (한글의 경우 2Byte)
     */
    public static int calculateLength(String string) {
        int length = -1;

        try {
            // getBytes의 인자로 "euc-kr" 을 넣으면 한글을 2Byte로 계산함
            length = string.getBytes("euc-kr").length;
        } catch (UnsupportedEncodingException e) {
            // todo: UnsupportedEncodingException 예외 처리
        }

        // 예외 발생 시 -1이 반환됨
        return length;
    }

    /**
     * editText의 글자 수를 viewText에 출력해주는 함수
     * addTextChangedListener >> afterTextChanged 에서 사용되기 때문에 매개변수가 editText 가 아니라 editable 일 수 밖에 없음
     */
    public static void printLength(Editable editable, TextView textView, int limit) {
        int length = calculateLength(editable.toString());

        if(length == -1) {
            // Length 계산에서 예외 발생
            textView.setText("글자 수 계산 중 오류가 발생했습니다.");
        } else if(length > limit) {
            // 글자 수 제한 초과
            textView.setText("글자 수 제한을 초과하였습니다.");
        } else {
            // 글자 수 정상
            String str =  makeLengthString(length, limit);
            textView.setText(str);
        }
    }

    /**
     * 문자열 Length 출력을 위한 문자열 합성 함수
     * NicknameActivity 에서도 한 번 사용되기 때문에 따로 분리해놓음
     */
    public static String makeLengthString(int length, int limit) {
        return length + " / " + limit;
    }

    /**
     * EditText에 포커스 주면서 키보드 올리기
     */
    public static void showKeyboard(Activity activity, EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 키보드 내리기
     */
    public static void hideKeyboard(Activity activity, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 파일 복사 함수
     */
    public static File fileCopy(String input, String output) {
        File file = new File(input);
        File newFile = new File(output);

        try{
            newFile.createNewFile();
        }catch (IOException e) {
            e.printStackTrace();
        }

        if(file!=null && file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream(newFile);

                int readCnt = 0;
                byte[] buffer = new byte[1024];

                while((readCnt = fis.read(buffer,0,1024)) != -1) { fos.write(buffer, 0, readCnt); }

                fos.flush();
                fos.close();
                fis.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            // file이 없을 때 처리
        }

        return newFile;
    }
}
