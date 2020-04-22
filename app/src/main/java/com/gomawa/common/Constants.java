package com.gomawa.common;

public class Constants {
    // 액티비티 전환 간 Intent 를 위한 상수 - 현재는 닉네임 액티비티 불러오는데 쓰이고 있음
    public static final int REQUEST_RESULT = 0;
    public static final int RESULT_SUCESS = 1;
    public static final int RESULT_SUCESS_NICKNAME = 2;

    // 갤러리&카메라에서 이미지 가져올 때 사용하는 상수
    public static final int PICK_FROM_GALLREY = 11;
    public static final int CROP_IMAGE = 22;
    public static final int PICK_FROM_CAMERA = 33;

    // ShareItem Fragment 에서 All List 인지 My List 인지 구분할 때 쓰는 상수
    public static final int ALL_LIST = 99;
    public static final int MY_LIST = 999;

    // ShareItem Update 액티비티 request code & result code
    public static final int REQUEST_UPDATE = 55;
    public static final int RESULT_OK = 66;
    public static final int RESULT_CANCEL = 77;

    // 닉네임 글자 수 제한
    public static final int NICKNAME_LIMIT = 10;

    // 개발자 이메일
    public static final String[] EMAIL = { "apfhd5620@gmail.com" };

    // 어플 이름 ( 플레이스토어 검색을 위한 임시 상수 )
    public static final String APPNAME = "고마와";
}
