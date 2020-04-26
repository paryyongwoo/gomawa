package com.gomawa.fragment;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gomawa.R;
import com.gomawa.activity.WriteActivity;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.Constants;
import com.gomawa.dialog.OnlyVerticalThreeButtonDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentShare extends Fragment {

    /**
     * 뷰
     */
    private ViewGroup rootView = null;
    private ListView shareListView = null;
    private ImageButton writeBtn = null;
    private ImageButton listBtn = null;
    private ImageButton myListBtn = null;
    private TextView writeTextView = null;
    private TextView listTextView = null;
    private TextView myListTextView = null;
    private TextView pageTextView = null;

    /**
     * 글쓰기, 목록, 내글 프래그먼트
     */
    private Fragment writeFragment = null;
    private Fragment allListFragment = null;
    private Fragment myListFragment = null;

    /**
     * 프래그먼트 매니저
     */
    private FragmentManager fm = null;

    /**
     * 헤더 메뉴 버튼 다이얼로그
     */
    private OnlyVerticalThreeButtonDialog headerMenuDialog = null;

    /**
     * 현재 선택한 메뉴 값
     */
    private final String WRITE = "WRITE";
    private final String LIST = "LIST";
    private final String MY_LIST = "MY_LIST";

    public boolean isWrite = false;

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

        if (allListFragment == null) {
            /**
             * fm에 프래그먼트를 add 할때, tag를 지정해준다. (글작성 후에 listFragment의 데이터 로딩 함수를 호출하기 위해)
             */
            allListFragment = new FragmentShareList(Constants.ALL_LIST);
            fm.beginTransaction().add(R.id.share_frame_layout, allListFragment, "allListFragment").commit();
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
        TextView headerText = rootView.findViewById(R.id.header_title);
        headerText.setText(headerTitle);

        /**
         * 헤더 버튼 설정
         */
        final ImageButton headerMenuBtn = rootView.findViewById(R.id.header_menu_button);
        headerMenuBtn.setVisibility(View.VISIBLE);
        headerMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 모든 글 보기
                View.OnClickListener allListBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(allListFragment == null) {
                            Toast.makeText(getContext(), "allList 진입", Toast.LENGTH_SHORT).show();

                            // allListFragment 가 한 번도 만들어지지 않았을 때
                            allListFragment = new FragmentShareList(Constants.ALL_LIST);
                            fm.beginTransaction().add(R.id.share_frame_layout, allListFragment, "allListFragment").commit();
                        } else {
                            if(!(allListFragment.isHidden())) {
                                // allListFragment 가 이미 보여지고 있을 때 ~ 새로고침
                                FragmentShareList fragment = (FragmentShareList) fm.findFragmentByTag("allListFragment");
                                fragment.getShareItems(0);
                            }
                        }

                        if(allListFragment != null) { fm.beginTransaction().show(allListFragment).commit(); }
                        if(myListFragment != null) { fm.beginTransaction().hide(myListFragment).commit(); }

                        headerMenuDialog.dismiss();
                    }
                };

                // 내 글 보기
                View.OnClickListener myListBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(myListFragment == null) {
                            Toast.makeText(getContext(), "myList 진입", Toast.LENGTH_SHORT).show();

                            // myListFragment 가 한 번도 만들어지지 않았을 때
                            myListFragment = new FragmentShareList(Constants.MY_LIST);
                            fm.beginTransaction().add(R.id.share_frame_layout, myListFragment, "myListFragment").commit();
                        } else {
                            if(!(myListFragment.isHidden())) {
                                // allListFragment 가 이미 보여지고 있을 때 ~ 새로고침
                                FragmentShareList fragment = (FragmentShareList) fm.findFragmentByTag("myListFragment");
                                fragment.getShareItems(0);
                            }
                        }

                        if(myListFragment != null) { fm.beginTransaction().show(myListFragment).commit(); }
                        if(allListFragment != null) { fm.beginTransaction().hide(allListFragment).commit(); }

                        headerMenuDialog.dismiss();
                    }
                };

                // 좋아요 글 보기
                View.OnClickListener likeListBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: 2020-04-26 좋아요 누른 글 보기
                        headerMenuDialog.dismiss();
                    }
                };

                // 다이얼로그
                headerMenuDialog = new OnlyVerticalThreeButtonDialog(getContext(), allListBtnListener, myListBtnListener, likeListBtnListener, "모든 글", "내가 쓴 글", "좋아요 누른 글");
                headerMenuDialog.show();
            }
        });

        /**
         * Floating Button
         */
        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.share_floating_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WriteActivity.class);
                startActivityForResult(intent, Constants.REQUEST_WRITE);
            }
        });
    }

    /**
     * 글작성 완료 후에, 최신 상태의 shareList를 가져오기 위한 함수
     */
    public void moveShareList() {
        isWrite = true;
        if (writeFragment != null) fm.beginTransaction().hide(writeFragment).commit();
        if (this.allListFragment != null) fm.beginTransaction().show(this.allListFragment).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_WRITE) {
            if(resultCode == Constants.RESULT_OK) {
                FragmentShareList allListFragment = (FragmentShareList) fm.findFragmentByTag("allListFragment");
                allListFragment.getShareItems(0);

                if(myListFragment != null) {
                    FragmentShareList myListFragment = (FragmentShareList) fm.findFragmentByTag("myListFragment");
                    myListFragment.getShareItems(0);
                }

            }
        }
    }
}
