package com.gomawa.common;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gomawa.dto.Member;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CommonUtils {
    // 닉네임이 어디있는 지 몰라서 임시로 만든 변수
    public static String nickname = "park";

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
            mActivity.finish();
        }
    }

    /**
     * editText의 글자 수 viewText에 출력
     */
    public static void printLength(Editable editable, TextView textView, int limit) {
        // todo: 한글 문자열을 어떻게 처리하나? 영어는 몇 글자 까지?
        int length = editable.toString().getBytes().length;

        if(length > limit) {
            textView.setText("글자 수 제한을 초과하였습니다.");
        } else {
            String str =  makeLengthString(length, limit);
            textView.setText(str);
        }
    }

    /**
     * 문자열 Length 출력을 위한 문자열 합성 함수
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
