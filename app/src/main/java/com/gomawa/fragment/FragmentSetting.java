package com.gomawa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gomawa.R;
import com.gomawa.common.Global;
import com.gomawa.activity.NicknameActivity;

public class FragmentSetting extends Fragment {
    ViewGroup rootView;

    TextView nicknameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);

        initView();

        return rootView;
    }

    private void initView() {
        nicknameTextView = rootView.findViewById(R.id.fragment_setting_nickname_textView);

        // 닉네임을 DB에서 가져와서 표시 (DB 없어서 임시로 Global에서 가져옴)
        nicknameTextView.setText(Global.nickname);

        // 닉네임 변경 버튼 Listener
        ImageButton nicknameBtn = rootView.findViewById(R.id.fragment_setting_nickname_btn);
        nicknameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), NicknameActivity.class);
                i.putExtra("nowNickname", Global.nickname);
                startActivityForResult(i, Global.REQUEST_RESULT);
            }
        });

        // 헤더 타이틀과 서브타이틀 Text 초기화
        TextView headerTitle = rootView.findViewById(R.id.header_title);
        headerTitle.setText("설정");
        TextView headerSubTitle = rootView.findViewById(R.id.header_subtitle);
        headerSubTitle.setText("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // NicknameActivity 종료 시 호출
        if(requestCode == Global.REQUEST_RESULT) {
            switch(resultCode) {
                case Global.RESULT_SUCESS: // NicknameActivity - BackBtn
                    break;
                case Global.RESULT_SUCESS_NICKNAME: // NicknameActivity - okBtn
                    Global.nickname = data.getExtras().getString("newNickname");
                    nicknameTextView.setText(Global.nickname);
                    break;
                default: // 비정상적인 종료
                    Toast.makeText(getContext(), "NicknameActivity가 비정상적으로 종료됨.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
