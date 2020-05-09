package com.gomawa.fragment;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gomawa.R;
import com.gomawa.common.Data;
import com.gomawa.dto.DailyThanks;
import com.nex3z.flowlayout.FlowLayout;

public class FragmentMyThanksComplete extends Fragment {

    private ViewGroup rootView = null;
    private TextView tagsTextView = null;
    private TextView completeTextView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Toast.makeText(getContext(), "com", Toast.LENGTH_SHORT).show();
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_my_thanks_complete, container, false);

        initView();
        return rootView;
    }

    private void initView() {
        tagsTextView = rootView.findViewById(R.id.tags);
        tagsTextView = rootView.findViewById(R.id.tags);
        completeTextView = rootView.findViewById(R.id.complete_text_view);

        /**
         * 이미 오늘 날짜에 작성한 DailyThanks가 있다면 내용을 뷰에 표시
         */
        DailyThanks dailyThanks = Data.getDailyThanks();
        if (dailyThanks != null) {
            completeTextView.setText(dailyThanks.getContent());
        }
    }

    /**
     * tagsTextView에 사용자가 입력한 tag를 설정해주는 함수
     */
    public void setTags(String tags) {
        tagsTextView.setText(tags);
    }
}
