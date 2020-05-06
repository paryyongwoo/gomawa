package com.gomawa.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gomawa.R;
import com.gomawa.activity.UpdateActivity;
import com.gomawa.adapter.ShareRecyclerViewAdapter;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.Constants;
import com.gomawa.common.Data;
import com.gomawa.common.ImageUtils;
import com.gomawa.dto.ShareItem;
import com.gomawa.network.RetrofitHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentShareList extends Fragment {
    // refresh 메소드 사용을 위한 Fragment 참조 변수
    private FragmentShareList mFragment = this;

    // ALL or MY or LIKE
    private int type;

    // 현재 게시물 페이지
    private int page = 0;

    private ViewGroup rootView = null;

    private List<ShareItem> shareItemList = new ArrayList<>();

    // Recycler View
    private RecyclerView recyclerView = null;

    // Recycler View Layout Manager
    private LinearLayoutManager layoutManager = null;

    // Recycler View Adapter
    private ShareRecyclerViewAdapter shareRecyclerViewAdapter = null;

    // 스와이프 새로고침 레이아웃
    private SwipeRefreshLayout swipeRefreshLayout = null;

    // 게시글이 존재하지 않습니다 문구
    private TextView noContentTextView = null;

    // 이 리스트의 Type을 가져오기 위한 생성자
    public FragmentShareList(int type) {
        this.type = type;
    }

    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_share_list, container, false);

        // 뷰 초기화
        initView();

        // Task 후 return 함수
        return rootView;
    }

    /**
     * Type 별로 ShareItem List 를 가져오는 메소드
     * 1. DB 에서 List 를 가져와서 Data.ShareItemList 에 저장
     * 2. refreshList() 를 호출하여 현재 FragmentShareList 의 shareItemList 에 주소값 저장
     * 3. shareItemList 를 이용하여 어댑터 갱신
     *
     * @param paramPage 불러오고자 하는 페이지 ( 0 입력 시 List 를 초기화 )
     * @param doScroll 리스트 제일 위로 스크롤 할 지 여부
     */
    public void getShareItemList(int paramPage, boolean doScroll) {
        switch(type) {
            case Constants.ALL_LIST:
                Data.getShareItemApi(mFragment, paramPage, doScroll);
                break;
            case Constants.MY_LIST:
                Data.getShareItemByMemberKeyApi(mFragment);
                break;
            case Constants.LIKE_LIST:
                Data.getShareItemByLikeApi(mFragment);
                break;
        }
    }

    /**
     * Recycler View Adapter 를 새로고침 해주는 메소드
     */
    public void refreshList(int paramPage, boolean doScroll) {
        // 정보 갱신
        shareRecyclerViewAdapter.notifyDataSetChanged();

        // 새로고침 아이콘 제거
        swipeRefreshLayout.setRefreshing(false);

        // 현재 페이지를 불러온 페이지로 수정
        page = paramPage;

        // doScroll 이 true 면 스크롤
        if (doScroll) {
            recyclerView.smoothScrollToPosition(0);
        }

        // List 가 비어있으면 TextView 표시
        if(shareItemList.size() == 0) {
            noContentTextView.setVisibility(View.VISIBLE);
        } else {
            noContentTextView.setVisibility(View.GONE);
        }
    }

    private void initView() {
        // 스와이프 새로고침을 위한 레이아웃
        swipeRefreshLayout = rootView.findViewById(R.id.share_recycler_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getShareItemList(0, true);
            }
        });

        //
        switch(type) {
            case Constants.ALL_LIST:
                shareItemList = Data.getShareItemList();
                break;
            case Constants.MY_LIST:
                shareItemList = Data.getMyShareItemList();
                break;
            case Constants.LIKE_LIST:
                shareItemList = Data.getLikeShareItemList();
                break;
        }

        // 레이아웃 매니저 설정
        recyclerView = rootView.findViewById(R.id.share_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // 어댑터 설정
        shareRecyclerViewAdapter = new ShareRecyclerViewAdapter(shareItemList, getActivity(), mFragment);
        recyclerView.setAdapter(shareRecyclerViewAdapter);

        // 게시글 없을때 문구
        noContentTextView = rootView.findViewById(R.id.no_content);

        /**
         * 리사이클러뷰에 스크롤 이벤트 리스너 추가
         * 일단은 전체 글 보기에서만 작동함
         */
        if(type == Constants.ALL_LIST) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!isLoading) {
                        if (layoutManager != null && layoutManager.findLastVisibleItemPosition() == shareItemList.size() - 1) {
                            Toast.makeText(getContext(), "로딩" + page, Toast.LENGTH_SHORT).show();
                            loadMore();
                            isLoading = true;
                        }
                    }
                }
            });
        }

        // type 별로 ShareItem 가져오기 + 어댑터 새로고침
        getShareItemList(0, true);
    }

    /**
     * 추가 게시글 로딩 함수
     * 딜레이를 주기 위해 핸들러 사용
     */
    private void loadMore() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getShareItemList(++page, false);
                isLoading = false;
            }
        }, 2000);
    }

    /**
     * 프래그먼트가 show, hide 될 때 호출됨
     * @param hidden false: 프래그먼트가 보여질 때
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden) getShareItemList(0, true);
    }

    /**
     * 다른 액티비티가 종료되고 다시 돌아왔을 때 호출됨
     * ex) 댓글 액티비티 종료 시 호출
     */
    @Override
    public void onResume() {
        getShareItemList(page, false);

        super.onResume();
    }

    // 수정 액티비티를 띄워주는 메소드 ( 어댑터에서는 액티비티에서 값을 반환받을 수 없음 )
    public void startUpdateActivity(ShareItem shareItem) {
        Intent intent = new Intent(getActivity(), UpdateActivity.class);

        // DB 작업을 위해서 보냄
        intent.putExtra("id", shareItem.getId());
        // VIEW 작업을 위해서 보냄
        Date regDate = shareItem.getRegDate();
        String regDateString = CommonUtils.convertFromDateToString(regDate, "YYYY.MM.dd");
        intent.putExtra("regDate", regDateString);
        intent.putExtra("content", shareItem.getContent());
        intent.putExtra("backgroundUrl", shareItem.getBackgroundUrl());

        startActivityForResult(intent, Constants.REQUEST_UPDATE);
    }

    // 수정 액티비티에서 돌아왔을 때 호출
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Constants.REQUEST_UPDATE:
                if(resultCode == Constants.RESULT_OK) {
                    getShareItemList(0, true);
                } else if(resultCode == Constants.RESULT_CANCEL) {

                }
        }
    }
}
