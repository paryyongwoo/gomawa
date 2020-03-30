package com.gomawa.common;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.gomawa.dto.Member;

public class Global {
    // 액티비티 전환 간 Intent 를 위한 상수
    public static final int REQUEST_RESULT = 0;
    public static final int RESULT_SUCESS = 1;
    public static final int RESULT_SUCESS_NICKNAME = 2;

    public static final int NICKNAME_LIMIT = 10;

    // 닉네임이 어디있는 지 몰라서 임시로 만든 변수
    public static String nickname = "park";

    private static Member member;

    public static void setMember(Member m) {
        member = m;
    }

    public static Member getMember() {
        return member;
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
     * editText의 글자 수 viewText에 출력
     */
    public static void printLength(Editable editable, TextView textView, int limit) {
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
}
