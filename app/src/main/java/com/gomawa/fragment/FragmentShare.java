package com.gomawa.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.gomawa.R;
import com.gomawa.dto.ShareItem;
import com.gomawa.viewpager.DepthPageTransformer;
import com.gomawa.viewpager.ScreenSlidePagerAdapter;

import java.util.ArrayList;

public class FragmentShare extends Fragment {

    private ViewGroup rootView = null;
    private ListView shareListView = null;

    private ArrayList<ShareItem> shareItemList = null;

    /**
     * 뷰페이저
     */
    private ViewPager mPager = null;
    private ScreenSlidePagerAdapter pagerAdapter = null;

    Fragment writeFragment = null;
    Fragment listFragment = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("fragment", "onAttach share");
    }

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

//        mPager = rootView.findViewById(R.id.thanksPager);
//        mPager.setOffscreenPageLimit(4);
//        pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
//        writeFragment = new FragmentShareWrite();
//        listFragment = new FragmentShareList();
//        pagerAdapter.addItem(writeFragment);
//        pagerAdapter.addItem(listFragment);
//        mPager.setAdapter(pagerAdapter);
//        // DepthPageTransformer 설정
//        mPager.setPageTransformer(true, new DepthPageTransformer());

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
    }
}
