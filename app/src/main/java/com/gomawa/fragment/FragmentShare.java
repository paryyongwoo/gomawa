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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gomawa.R;
import com.gomawa.activity.WriteActivity;
import com.gomawa.common.Constants;
import com.gomawa.dialog.OnlyVerticalThreeButtonDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentShare extends Fragment {

    /**
     * 뷰
     */
    private ViewGroup rootView = null;

    private TextView headerSubText = null;

    /**
     * 글쓰기, 목록, 내글 프래그먼트
     */
    private Fragment allListFragment = null;
    private Fragment myListFragment = null;
    private Fragment likeListFragment = null;

    /**
     * 프래그먼트 매니저
     */
    private FragmentManager fm = null;

    /**
     * 헤더 메뉴 버튼 다이얼로그
     */
    private OnlyVerticalThreeButtonDialog headerMenuDialog = null;

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

    /**
     * type 을 인자로 넣으면 알맞는 문자열로 subTitle 을 설정해주는 메소드
     */
    public void setSubTitle(int type) {
        String text = "";

        switch (type) {
            case Constants.ALL_LIST:
                text = "전체 글";
                break;
            case Constants.MY_LIST:
                text = "내 글";
                break;
            case Constants.LIKE_LIST:
                text = "좋아요 누른 글";
                break;
        }

        headerSubText.setText(text);
    }

    private void initView() {
        /**
         * 헤더 텍스트 설정
         */
        ImageView headerImageView = rootView.findViewById(R.id.header_imageView);
        headerImageView.setVisibility(View.INVISIBLE);

        String headerTitle = getResources().getString(R.string.header_title_share);
        TextView headerText = rootView.findViewById(R.id.header_title);
        headerText.setText(headerTitle);

        headerSubText = rootView.findViewById(R.id.header_sub_title);
        setSubTitle(Constants.ALL_LIST);

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
                        if(likeListFragment != null) { fm.beginTransaction().hide(likeListFragment).commit(); }

                        setSubTitle(Constants.ALL_LIST);

                        headerMenuDialog.dismiss();
                    }
                };

                // 내 글 보기
                View.OnClickListener myListBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(myListFragment == null) {
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
                        if(likeListFragment != null) { fm.beginTransaction().hide(likeListFragment).commit(); }

                        setSubTitle(Constants.MY_LIST);

                        headerMenuDialog.dismiss();
                    }
                };

                // 좋아요 글 보기
                View.OnClickListener likeListBtnListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(likeListFragment == null) {
                            // likeListFragment 가 한 번도 만들어지지 않았을 때
                            likeListFragment = new FragmentShareList(Constants.LIKE_LIST);
                            fm.beginTransaction().add(R.id.share_frame_layout, likeListFragment, "likeListFragment").commit();
                        } else {
                            if(!(likeListFragment.isHidden())) {
                                // likeListFragment 가 이미 보여지고 있을 때 ~ 새로고침
                                FragmentShareList fragment = (FragmentShareList) fm.findFragmentByTag("likeListFragment");
                                fragment.getShareItems(0);
                            }
                        }

                        if(likeListFragment != null) { fm.beginTransaction().show(likeListFragment).commit(); }
                        if(allListFragment != null) { fm.beginTransaction().hide(allListFragment).commit(); }
                        if(myListFragment != null) { fm.beginTransaction().hide(myListFragment).commit(); }

                        setSubTitle(Constants.LIKE_LIST);

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
//    public void moveShareList() {
//        isWrite = true;
//        if (writeFragment != null) fm.beginTransaction().hide(writeFragment).commit();
//        if (this.allListFragment != null) fm.beginTransaction().show(this.allListFragment).commit();
//    }

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

                if(likeListFragment != null) {
                    FragmentShareList likeListFragment = (FragmentShareList) fm.findFragmentByTag("likeListFragment");
                    likeListFragment.getShareItems(0);
                }

            }
        }
    }
}
