package com.gomawa.common;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.gomawa.R;
import com.gomawa.dto.Member;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    /**
     * 1. Date 를 Default Format 의 String 으로 변환해주는 함수 ("yyyy-MM-dd'T'HH:mm:ss.SSS")
     * 2. 두 번째 매개변수로 Format 을 넣을 수도 있음
     */
    public static String convertFromDateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.KOREA);
        String str = format.format(date);

        return str;
    }
    public static String convertFromDateToString(Date date, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.KOREA);
        String str = format.format(date);

        return str;
    }

    /**
     * 텍스트 더보기 기능
     * @param view 더보기 기능을 추가할 텍스트뷰
     * @param text 내용
     * @param maxLine 라인수
     */
    public static void setReadMore(final TextView view, final String text, final int maxLine) {
        final Context context = view.getContext();
        final String expanedText = " ... 더보기";

        if (view.getTag() != null && view.getTag().equals(text)) { //Tag로 전값 의 text를 비교하여똑같으면 실행하지 않음.
            return;
        }
        view.setTag(text); //Tag에 text 저장
        view.setText(text); // setText를 미리 하셔야  getLineCount()를 호출가능
        view.post(new Runnable() { //getLineCount()는 UI 백그라운드에서만 가져올수 있음
            @Override
            public void run() {
                if (view.getLineCount() >= maxLine) { //Line Count가 설정한 MaxLine의 값보다 크다면 처리시작

                    int lineEndIndex = view.getLayout().getLineVisibleEnd(maxLine - 1); //Max Line 까지의 text length

                    String[] split = text.split("\n"); //text를 자름
                    int splitLength = 0;

                    String lessText = "";
                    for (String item : split) {
                        splitLength += item.length() + 1;
                        if (splitLength >= lineEndIndex) { //마지막 줄일때!
                            if (item.length() >= expanedText.length()) {
                                lessText += item.substring(0, item.length() - (expanedText.length())) + expanedText;
                            } else {
                                lessText += item + expanedText;
                            }
                            break; //종료
                        }
                        lessText += item + "\n";
                    }
                    SpannableString spannableString = new SpannableString(lessText);
                    spannableString.setSpan(new ClickableSpan() {//클릭이벤트
                        @Override
                        public void onClick(View v) {
                            view.setText(text);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) { //컬러 처리
                            ds.setColor(ContextCompat.getColor(context, R.color.inactiveColor));
                        }
                    }, spannableString.length() - expanedText.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    view.setText(spannableString);
                    view.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        });
    }
}
