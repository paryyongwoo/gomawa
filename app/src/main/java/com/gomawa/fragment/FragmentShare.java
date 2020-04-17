package com.gomawa.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gomawa.R;

public class FragmentShare extends Fragment {

    /**
     * 뷰
     */
    private ViewGroup rootView = null;
    private ListView shareListView = null;
    private ImageButton writeBtn = null;
    private ImageButton listBtn = null;
    private ImageButton myListBtn = null;

    /**
     * 글쓰기, 목록, 내글 프래그먼트
     */
    private Fragment writeFragment = null;
    private Fragment listFragment = null;

    /**
     * 프래그먼트 매니저
     */
    private FragmentManager fm = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /**
         * rootView 생성
         */
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_share, container, false);

        /**
         * 초기화 작업
         */
        initView();

        /**
         * FragmentManager 얻기
         */
        fm = getChildFragmentManager();

        if (listFragment == null) {
            /**
             * fm에 프래그먼트를 add 할때, tag를 지정해준다. (글작성 후에 listFragment의 데이터 로딩 함수를 호출하기 위해)
             */
            listFragment = new FragmentShareList();
            fm.beginTransaction().add(R.id.share_frame_layout, listFragment, "listFragment").commit();
        }

        return rootView;
    }

    /**
     * @description
     * 프래그먼트별 액션바 설정하는 방법
     * 1. 프래그먼트의 onCreate메소드를 오버라이딩해서 setHasOptionsMenu(true)를 통해 프래그먼트의 액션바 메뉴를 사용하겠다고 설정한다.
     * 2. onCreateOptionsMenu메소드를 오버라이딩해서 메뉴 리소스를 설정한다.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d("fragment", "onCreateShare");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // 메뉴 리소스 설정
        inflater.inflate(R.menu.menu_toolbar, menu);
    }

    private void initView() {

        /**
         * 헤더 텍스트 설정
         */
        String headerTitle = getResources().getString(R.string.header_title_share);
        String headerSubTitle = getResources().getString(R.string.sub_title_share);
        TextView headerText = rootView.findViewById(R.id.header_title);
        headerText.setText(headerTitle);
        TextView headerSubTitleText = rootView.findViewById(R.id.header_subtitle);
        headerSubTitleText.setText(headerSubTitle);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) headerSubTitleText.getLayoutParams();
        marginLayoutParams.setMargins(0, marginLayoutParams.topMargin - 20, 0, 0);

        /**
         * 헤더 버튼 (목록, 글쓰기, 내글보기)
         */
        listBtn = rootView.findViewById(R.id.list_btn);
        writeBtn = rootView.findViewById(R.id.write_btn);
        myListBtn = rootView.findViewById(R.id.my_list_btn);

        /**
         * 목록 (목록과 내글보기는 프래그먼트 전환시 구분자를 함께 전달해줘야함)
         */
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writeFragment != null) fm.beginTransaction().hide(writeFragment).commit();
                if (listFragment != null) fm.beginTransaction().show(listFragment).commit();
            }
        });

        /**
         * 글쓰기
         */
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writeFragment == null) {
                    writeFragment = new FragmentShareWrite();
                    fm.beginTransaction().add(R.id.share_frame_layout, writeFragment, "writeFragment").commit();
                }
                if (writeFragment != null) fm.beginTransaction().show(writeFragment).commit();
                if (listFragment != null) fm.beginTransaction().hide(listFragment).commit();
            }
        });

        /**
         * 내글보기 (목록과 내글보기는 프래그먼트 전환시 구분자를 함께 전달해줘야함)
         */
        myListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writeFragment != null) fm.beginTransaction().hide(writeFragment).commit();
                if (listFragment != null) fm.beginTransaction().show(listFragment).commit();
            }
        });
    }

    /**
     * 글작성 완료 후에, 최신 상태의 shareList를 가져오기 위한 함수
     */
    public void moveShareList() {
        FragmentShareList listFragment = (FragmentShareList) fm.findFragmentByTag("listFragment");
        listFragment.executeRequestApi();

        if (writeFragment != null) fm.beginTransaction().hide(writeFragment).commit();
        if (this.listFragment != null) fm.beginTransaction().show(this.listFragment).commit();
    }
}
