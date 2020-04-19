package com.gomawa.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 레트로핏 설정 객체
 */
public class RetrofitHelper {

    private Retrofit retrofit;
    private RetrofitService retrofitService;
    public static RetrofitHelper retrofitHelper = new RetrofitHelper();

    public RetrofitHelper() {

        /**
         * 로깅작업 추가
         */
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 파일 업로드의 경우 타임아웃을 늘려주는게 좋음, 기본 레트로핏 타임아웃은 10초
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS);

        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        /**
         * gson 빌더
         */
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .setLenient()
                .create();

        /**
         * retrofit 생성
         * converterFactory는 적용 순서가 있는듯.. 위에서부터 적용
         */
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.35.145:8080") // TODO: 2020-01-29 문자열 리소스로 분리하기
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        retrofitService = retrofit.create(RetrofitService.class);
    }

    public static RetrofitHelper getInstance() {
        return retrofitHelper;
    }

    public RetrofitService getRetrofitService() {
        return retrofitService;
    }
}
