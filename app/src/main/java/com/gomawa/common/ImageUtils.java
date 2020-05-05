package com.gomawa.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.gomawa.R;
import com.gomawa.dto.Member;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageUtils {

    // 프로필 이미지 디폴트 값
    public static final int DEFAULT_PROFILE_IMAGE = R.drawable.user_btn_disable;
    public static final int DEFAULT_BACKGROUND_IMAGE = R.drawable.share_item_background;

    /**
     * Circle ImageView 에 프로필 이미지 설정해주는 함수
     * 프로필 이미지가 null 이면 default Drawable 이 설정됨
     */
    public static void setProfileImageOnCircleImageView(Context mContext, CircleImageView view, String url) {
        if(url == null) {
            view.setImageResource(DEFAULT_PROFILE_IMAGE);
        } else {
            Glide.with(mContext).load(url).into(view);
        }
    }

    /**
     * 빈 이미지 파일 만드는 함수
     * 1. ProfileIamge
     * 2. ShareItem BackgroundImage
     */
    public static File createProfileImageFile() throws IOException {
        // 이미지 파일 이름
        String timeStamp = new SimpleDateFormat("YYYYMMddHHmmssSSS", Locale.KOREA).format(new Date());
        String imageFileName = Data.getMember().getId() + "_ProfileImage_" + timeStamp + "_";

        // 이미지 저장 폴더 ( gomawa_temp )
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/gomawa_temp");
        if(!storageDir.exists()) storageDir.mkdir();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }
    public static File createShareItemBackgroundImageFile() throws IOException {
        // 이미지 파일 이름
        String timeStamp = new SimpleDateFormat("YYYYMMddHHmmssSSS", Locale.KOREA).format(new Date());
        String imageFileName = Data.getMember().getId() + "_BackgroundImage_" + timeStamp + "_";

        // 이미지 저장 폴더 ( gomawa_temp )
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
//        intent.putExtra("outputX", 200);
//        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

        return intent;
    }

    /**
     * 이미지 다운로드 클래스
     */
    public static class ImageDownload extends AsyncTask {
        // mActivity
        private Activity mActivity = null;

        // 다운받아질 File
        private File file = null;

        @Override
        protected Object doInBackground(Object[] objects) {
            // Index 0: Url
            String url = (String) objects[0];
            // Index 1: mActivity for Toast
            mActivity = (Activity) objects[1];

            // 다운로드 경로
            String savePath = Environment.getExternalStorageDirectory() + "/gomawa_file";

            File dir = new File(savePath);
            if(!(dir.exists())) {
                dir.mkdir();
            }

            // 이미지 파일 이름 todo: 파일 이름 포맷 정하기
            String timeStamp = new SimpleDateFormat("YYYYMMddHHmmssSSS", Locale.KOREA).format(new Date());
            String imageFileName = Data.getMember().getId() + "_Image_" + timeStamp;

            // 저장될 파일의 경로
            String filePath = savePath + "/" + imageFileName + ".jpg";

            try{
                URL imgUrl = new URL(url);

                HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
                int len = conn.getContentLength();
                byte[] tmpByte = new byte[len];

                InputStream is = conn.getInputStream();

                file = new File(filePath);

                FileOutputStream fos = new FileOutputStream(file);
                int read;

                while(true) {
                    read = is.read(tmpByte);

                    if(read <= 0) {
                        break;
                    }

                    fos.write(tmpByte, 0, read);
                }

                is.close();
                fos.close();
                conn.disconnect();
            } catch(Exception e) {
                e.printStackTrace();
            }

            return mActivity;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            Toast.makeText(mActivity, "이미지 다운로드 완료", Toast.LENGTH_SHORT).show();

            mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        }
    }

}