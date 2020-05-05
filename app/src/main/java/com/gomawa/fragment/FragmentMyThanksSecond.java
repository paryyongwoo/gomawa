package com.gomawa.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gomawa.R;
import com.gomawa.common.CommonUtils;
import com.gomawa.dto.DailyThanks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 뷰페이저의 마지막 화면에 보여줄 프래그먼트
 * 고마운일들 3가지를 예쁘게 모아놓은 화면
 */
public class FragmentMyThanksSecond extends Fragment {

    /**
     * 고마운 일을 적은 에딧텍스트
     */
    private EditText editText = null;
    private TextView tagsTextView = null;
    /**
     * '완료' 버튼
     */
    private Button completeBtn = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /**
         * rootView 생성
         */
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_my_thanks_write, container, false);

        initView(rootView);

        return rootView;
    }

    private void initView(ViewGroup rootView) {
        editText = rootView.findViewById(R.id.edit_text);
        tagsTextView = rootView.findViewById(R.id.tags);
        completeBtn = rootView.findViewById(R.id.complet_btn);
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMyThanks fragmentMyThanks = (FragmentMyThanks) getParentFragment();
                fragmentMyThanks.moveChapter();
                fragmentMyThanks.setTags(2);
                DailyThanks dailyThanks = new DailyThanks();
                dailyThanks.setContent(editText.getText().toString());
                dailyThanks.setRegMember(CommonUtils.getMember());
                fragmentMyThanks.sendDailyThanks(dailyThanks);
            }
        });
    }

    /**
     * tagsTextView에 사용자가 입력한 tag를 설정해주는 함수
     */
    public void setTags(String tags) {
        tagsTextView.setText(tags);
    }
}
