package com.gomawa.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.gomawa.dto.Member;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Global {
    // 액티비티 전환 간 Intent 를 위한 상수 - 현재는 닉네임 액티비티 불러오는데 쓰이고 있음
    public static final int REQUEST_RESULT = 0;
    public static final int RESULT_SUCESS = 1;
    public static final int RESULT_SUCESS_NICKNAME = 2;

    // 갤러리&카메라에서 이미지 가져올 때 사용하는 상수
    public static final int PICK_FROM_GALLREY = 11;
    public static final int CROP_IMAGE = 22;
    public static final int PICK_FROM_CAMERA = 33;

    // 뒤로가기 버튼 시간 측정 변수
    private static long backKeyPressedTime = 0;

    // 닉네임 글자 수 제한
    public static final int NICKNAME_LIMIT = 10;

    // 개발자 이메일
    public static final String[] EMAIL = { "apfhd5620@gmail.com" };

    // 어플 이름 ( 플레이스토어 검색을 위한 임시 변수 )
    public static final String APPNAME = "고마와";

    // 닉네임이 어디있는 지 몰라서 임시로 만든 변수
    public static String nickname = "park";

    // 프로필 이미지 파일 저장 변수
    public static File profileImageFile;

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
     * 빈 이미지 파일 만드는 함수
     */
    public static File createImageFile() throws IOException {
        // 이미지 파일 이름 / "newProfileImage" 대신 알맞은 문자열 넣어줘야함
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "newProfileImage_" + timeStamp + "_";

        // 이미지 저장 폴더
        // todo: gomawa_temp 폴더는 임시이므로 어떻게 해야 할 지 난 잘 모르겠다
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/gomawa_temp");
        if(!storageDir.exists()) storageDir.mkdir();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    /**
     * 이미지를 CROP하기 위한 INTENT를 설정하고 반환하는 함수
     */
    public static Intent setIntentToCrop(Context mContext, File file) {
        Uri uri = FileProvider.getUriForFile(mContext, "com.gomawa.fileprovider", file);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 접근 권한을 주는 부분!
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

        return intent;
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
}
