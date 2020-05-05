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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.gomawa.R;
import com.nex3z.flowlayout.FlowLayout;

public class FragmentMyThanksFirst extends Fragment {

    private ViewGroup rootView = null;

    /**
     * 고마운 일들 태그를 담고있는 레이아웃
     */
    private FlowLayout flowLayout = null;
    private EditText editText = null;
    private Button nextBtn = null;

    /**
     * 태그
     */
    private String[] tags = {
            "#가족", "#친구들", "#첫출근", "#화창한날씨", "#즐거운데이트", "#불금", "#월급날", "#포근한이불속", "#귀여운강아지", "#친절한사람들"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_my_thanks_main, container, false);

//        // 다음 버튼 이벤트 추가
//        Button button = rootView.findViewById(R.id.daily_thanks_next_btn);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentMyThanks fragmentMyThanks = (FragmentMyThanks) getParentFragment();
//                fragmentMyThanks.moveChapter();
//            }
//        });

        //        editText = (EditText) rootView.findViewById(R.id.fragment_my_thanks_edit_text);
//        editText.requestFocus();
//
//        // 키보드 보이게 하는 부분
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        initView();
        return rootView;
    }

    private void initView() {
        flowLayout = rootView.findViewById(R.id.flow_layout);
        editText = rootView.findViewById(R.id.fragment_my_thanks_first_edit_text);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (String tag : tags) {
            final String t = tag;
            final Button btn = new Button(getContext());
            btn.setLayoutParams(layoutParams);
            btn.setText(tag);
            btn.setTextColor(getResources().getColor(R.color.inactiveColor));
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            btn.setPadding(10, 10, 10, 10);
            btn.setBackground(null);
            btn.setGravity(Gravity.LEFT);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * 태그 클릭
                     * 1. 버튼 색상 변경
                     * 2. EditText에 태그 추가
                     */
                    editText.setText(editText.getText() + t);
                    btn.setTextColor(getResources().getColor(R.color.mainColor));
                }
            });

            flowLayout.addView(btn);
        }

        /**
         * 다음 버튼
         * 1. write 프래그먼트로 이동
         * 2. write 프래그먼트의 tagsTextView에 사용자가 입력한 tag 설정
         */
        nextBtn = rootView.findViewById(R.id.daily_thanks_next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMyThanks fragmentMyThanks = (FragmentMyThanks) getParentFragment();
                fragmentMyThanks.moveChapter();
                fragmentMyThanks.setTags(1);
            }
        });
    }

    /**
     * 입력한 태그를 반환하는 함수
     */
    public String getTags() {
        return editText.getText().toString();
    }
}
