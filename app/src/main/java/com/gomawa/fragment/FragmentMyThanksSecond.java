package com.gomawa.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gomawa.R;
import com.gomawa.dto.DailyThanks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 뷰페이저의 마지막 화면에 보여줄 프래그먼트
 * 고마운일들 3가지를 예쁘게 모아놓은 화면
 */
public class FragmentMyThanksSecond extends Fragment {

    private TextView today = null;
    private TextView thanksText1 = null;
    private TextView thanksText2 = null;
    private TextView thanksText3 = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /**
         * rootView 생성
         */
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_my_thanks_second, container, false);

        initView(rootView);

        return rootView;
    }

    private void initView(ViewGroup rootView) {
        /**
         * 오늘 날짜 설정
         */
        today = rootView.findViewById(R.id.fragment_my_thanks_today);
        SimpleDateFormat format = new SimpleDateFormat("YYYY.MM.dd");
        Date t = Calendar.getInstance().getTime();
        today.setText(format.format(t));

        /**
         * thanksText 설정
         */
        thanksText1 = rootView.findViewById(R.id.fragment_my_thanks_second_content_text1);
        thanksText2 = rootView.findViewById(R.id.fragment_my_thanks_second_content_text2);
        thanksText3 = rootView.findViewById(R.id.fragment_my_thanks_second_content_text3);
    }

    public void setThanksText(DailyThanks dailyThanks) {
        thanksText1.setText(dailyThanks.getContent1());
        thanksText2.setText(dailyThanks.getContent2());
        thanksText3.setText(dailyThanks.getContent3());
    }

}
