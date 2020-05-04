package com.gomawa.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.gomawa.R;
import com.gomawa.activity.ShareActivity;
import com.gomawa.common.CommonUtils;
import com.gomawa.dto.DailyThanks;
import com.gomawa.dto.Member;
import com.gomawa.network.RetrofitHelper;
import com.gomawa.viewpager.DepthPageTransformer;
import com.gomawa.viewpager.ScreenSlidePagerAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 프래그먼트 생성에 관한 나만의 팁
 * 1. onCreateView 첫 줄에 rootView를 선언한다.
 * 2. 해당 프래그먼트의 부모 액티비티를 얻기 위한 방법: getActivity()
 */
public class FragmentMyThanks extends Fragment {

    // 고마운 일 작성은 최대 3개
    private static final int NUM_PAGES = 4;
    // 현재 뷰페이저의 위치
    private int currentPosition = 0;

    private ShareActivity activity = null;
    // 터치를 통한 스와이프 불가능하게 막은 CustomViewPager 사용
    private ViewPager mPager;
    private ScreenSlidePagerAdapter pagerAdapter;

    // rootView
    private ViewGroup rootView = null;
    // 툴바의 메뉴 버튼

    // 뷰페이저에 보여줄 프래그먼트들
    private Fragment fragmentFirst = null;
    private Fragment fragmentSecond = null;
    private Fragment fragmentThird = null;
    private Fragment fragmentFourth = null;

    /**
     * 헤더의 메뉴 버튼 (화면 표시 안함) 및 서브 타이틀
     */
    private ImageButton headerMenuBtn = null;
    private TextView subTitleTextView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /**
         * rootView 생성
         */
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_my_thanks_work, container, false);

        /**
         * activity 얻기
         */
        activity = ((ShareActivity)getActivity());

        /**
         * view 초기화
         */
        initView();

        /**
         * ViewPager 인스턴스화 작업
         *
         * ScreenSlidePagerAdapter의 생성자에 getChildFragmentManager()를 통해 현재 이 프래그먼트의 fragmentManager를 넘기는 이유
         *  - 원래는 현재 프래그먼트 상단의 액티비티에 있는 fragmetManager를 넘겨서 관리 권한을 상위 액티비티에게 주는게 맞으나,
         *    지금의 경우는 현재 이 프래그먼트가 뷰페이저 내부에 또 다른 프래그먼트들을 관리하는 구조이기 때문에 getChildFragmentManager()를
         *    통해 관리 권한을 자신이 갖는다.
         */
        mPager = rootView.findViewById(R.id.thanksPager);
        mPager.setOffscreenPageLimit(4);
        pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        fragmentFirst = new FragmentMyThanksFirst();
        fragmentSecond = new FragmentMyThanksFirst();
        fragmentThird = new FragmentMyThanksFirst();
        fragmentFourth = new FragmentMyThanksSecond();
        pagerAdapter.addItem(fragmentFirst);
        pagerAdapter.addItem(fragmentSecond);
        pagerAdapter.addItem(fragmentThird);
        pagerAdapter.addItem(fragmentFourth);
        mPager.setAdapter(pagerAdapter);
        // DepthPageTransformer 설정
        mPager.setPageTransformer(true, new DepthPageTransformer());

        return rootView;
    }

    public void moveChapter() {
        Log.d("methodTest", "moveChapter");
        if (currentPosition < 3) {
            movePage(currentPosition + 1);
        }
    }

    private void initView() {

        /**
         * 헤더 텍스트 설정
         */
        ImageView headerImageView = rootView.findViewById(R.id.header_imageView);
        headerImageView.setVisibility(View.INVISIBLE);

        String headerTitle = getResources().getString(R.string.header_title);
        TextView headerText = rootView.findViewById(R.id.header_title);
        headerText.setText(headerTitle);

        headerMenuBtn = rootView.findViewById(R.id.header_menu_button);
        headerMenuBtn.setVisibility(View.GONE);

        ImageButton headerMenuBtn = rootView.findViewById(R.id.header_menu_button);
        headerMenuBtn.setVisibility(View.INVISIBLE);

        subTitleTextView = rootView.findViewById(R.id.header_sub_title);
        subTitleTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * 화면 이동 해주는 메소드
     * @param want 이동하려는 페이지 번호
     */
    private void movePage(int want) {

        /**
         * 마지막 페이지로 이동할 경우 서버에 DailyThanks 저장
         */
        if (want == 3) {
            DailyThanks dailyThanks = setLastPage();
            sendDailyThanks(dailyThanks);
        }

        // keyboard 내리기
        hideKeyboad(currentPosition);

        // 현재 위치 재설정
        currentPosition = want;

        // 프래그먼트 전환
        mPager.setCurrentItem(currentPosition, true);
    }

    /**
     * 뷰페이저의 마지막 페이지에 사용자가 작성한 고마운일 3가지를 세팅
     * 및 DailyThanks객체 생성 후 반환
     * @return dailyThanks: 사용자가 작성한 고마운일 3가지를 담고 있는 객체, 서버에 전달할 용도
     */
    private DailyThanks setLastPage() {
        /**
         * 각 프래그먼트별 textview의 값들 설정
         */
        String firstThanksText;
        String secondThanksText;
        String thirdThanksText;
        TextView textView = fragmentFirst.getView().findViewById(R.id.fragment_my_thanks_first_edit_text);
        firstThanksText = textView.getText().toString();
        textView = fragmentSecond.getView().findViewById(R.id.fragment_my_thanks_first_edit_text);
        secondThanksText = textView.getText().toString();
        textView = fragmentThird.getView().findViewById(R.id.fragment_my_thanks_first_edit_text);
        thirdThanksText = textView.getText().toString();

        // DailyThanks 객체 생성
        DailyThanks dailyThanks = new DailyThanks();
        dailyThanks.setContent1(firstThanksText);
        dailyThanks.setContent2(secondThanksText);
        dailyThanks.setContent3(thirdThanksText);

        /**
         * 뷰페이저의 마지막 페이지를 얻어서 텍스트 세팅
         */
        FragmentMyThanksSecond fragment = (FragmentMyThanksSecond) pagerAdapter.instantiateItem(mPager, 3);
        fragment.setThanksText(dailyThanks);

        return dailyThanks;
    }

    private void sendDailyThanks(DailyThanks dailyThanks) {
        /**
         * dailyThanks 객체를 api로 보내기
         * api/dailyThanks
         */
        Member member = CommonUtils.getMember();
        if (member.getKey() < 0) {
            Toast.makeText(activity, "재로그인필요", Toast.LENGTH_SHORT).show();
        } else {
            dailyThanks.setRegMember(member);
            Call<DailyThanks> call = RetrofitHelper.getInstance().getRetrofitService().addDailyThanks(dailyThanks);
            Callback<DailyThanks> callback = new Callback<DailyThanks>() {
                @Override
                public void onResponse(Call<DailyThanks> call, Response<DailyThanks> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "setDailyThanks success" + response.body(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "setDailyThanks failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DailyThanks> call, Throwable t) {
                    Toast.makeText(getContext(), "failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("api", t.getMessage());
                }
            };
            call.enqueue(callback);
        }
    }

    /**
     * 뷰페이저의 페이지 이동시 키모드 내리는 함수
     */
    private void hideKeyboad(int position) {
        Fragment fragment = pagerAdapter.getItem(position);
        View rootView = fragment.getView();
        EditText editText = rootView.findViewById(R.id.fragment_my_thanks_first_edit_text);
        if (editText != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}
