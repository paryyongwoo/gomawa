package com.gomawa.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.gomawa.R;
import com.gomawa.common.CommonUtils;
import com.gomawa.common.ImageUtils;
import com.gomawa.fragment.FragmentMyThanks;
import com.gomawa.fragment.FragmentSetting;
import com.gomawa.fragment.FragmentShare;
import com.gomawa.fragment.FragmentShareWrite;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShareActivity extends AppCompatActivity {

    private FragmentManager fm = getSupportFragmentManager();
    private FragmentShare fragmentShare = null;
    private FragmentMyThanks fragmentMyThanks = null;
    private FragmentSetting fragmentSetting = null;

    private Toolbar toolbar = null;
    private BottomNavigationView bottomNavigationView = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 액션바가 기본으로 제공하는 타이틀 미사용 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // 툴바를 액션바로 설정
//        toolbar = findViewById(R.id.activity_share_toolbar);
//        setSupportActionBar(toolbar);

        /**
         * 첫화면에 보여줄 프래그먼트 설정
         * 하단의 네비게이션으로 프래그먼트를 전환한 후에도 각 프래그먼트의 데이터를 유지하기 위해
         * transaction.replace()를 사용하지 않고 add를 통해 프래그먼트를 쌓고,
         * show, hide 메소드를 통해 보여줄 프래그먼트만 화면에 표시한다.
         */
        if (fragmentMyThanks == null) {
//            fragmentShare = new FragmentShare();
//            FragmentShareWrite fragmentShareWrite = new FragmentShareWrite();
            fragmentMyThanks = new FragmentMyThanks();
            fm.beginTransaction().add(R.id.bottom_menu_framelayout, fragmentMyThanks).commit();
//            fm.beginTransaction().add(R.id.share_frame_layout, fragmentShareWrite).commit();
        }

        // 뷰설정
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // 뷰에 핸들러 추가
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.menu_my_thanks: {
                        if (fragmentMyThanks != null) fm.beginTransaction().show(fragmentMyThanks).commit();
                        if (fragmentShare != null) fm.beginTransaction().hide(fragmentShare).commit();
                        if (fragmentSetting != null) fm.beginTransaction().hide(fragmentSetting).commit();
                        break;
                    }
                    case R.id.menu_share:
                        if (fragmentShare == null) {
                            fragmentShare = new FragmentShare();
                            fm.beginTransaction().add(R.id.bottom_menu_framelayout, fragmentShare).commit();
                        }
                        if (fragmentMyThanks != null) fm.beginTransaction().hide(fragmentMyThanks).commit();
                        if (fragmentShare != null) fm.beginTransaction().show(fragmentShare).commit();
                        if (fragmentSetting != null) fm.beginTransaction().hide(fragmentSetting).commit();
                        break;
                    case R.id.menu_setting:
                        if (fragmentSetting == null) {
                            fragmentSetting = new FragmentSetting();
                            fm.beginTransaction().add(R.id.bottom_menu_framelayout, fragmentSetting).commit();
                        }
                        if (fragmentMyThanks != null) fm.beginTransaction().hide(fragmentMyThanks).commit();
                        if (fragmentShare != null) fm.beginTransaction().hide(fragmentShare).commit();
                        if (fragmentSetting != null) fm.beginTransaction().show(fragmentSetting).commit();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        CommonUtils.onBackPressedCheck(this.getApplicationContext(), this);
    }
}