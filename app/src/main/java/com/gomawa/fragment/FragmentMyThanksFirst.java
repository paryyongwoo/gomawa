package com.gomawa.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gomawa.R;

public class FragmentMyThanksFirst extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_my_thanks_first, container, false);

        // 다음 버튼 이벤트 추가
        Button button = rootView.findViewById(R.id.daily_thanks_next_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMyThanks fragmentMyThanks = (FragmentMyThanks) getParentFragment();
                fragmentMyThanks.moveChapter();
            }
        });

        //        editText = (EditText) rootView.findViewById(R.id.fragment_my_thanks_edit_text);
//        editText.requestFocus();
//
//        // 키보드 보이게 하는 부분
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        return rootView;
    }
}
