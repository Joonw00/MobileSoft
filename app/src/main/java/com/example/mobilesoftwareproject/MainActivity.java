package com.example.mobilesoftwareproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    final String TAG = this.getClass().getSimpleName();
    private HomeFragment homeFragment;
    private RegisterFragment registerFragment;
    private DiaryFragment diaryFragment;
    private AnalysisFragment analysisFragment;
    LinearLayout home_ly;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); // 객체 정의
        SettingListener(); // 리스너 등록
        // 프래그먼트 초기화
        homeFragment = new HomeFragment();
        registerFragment = new RegisterFragment();
        diaryFragment = new DiaryFragment();
        analysisFragment = new AnalysisFragment();


        // 맨 처음 시작할 탭 설정
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    private void init() {
        home_ly = findViewById(R.id.home_ly);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void SettingListener() {
        // 선택 리스너 등록
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }



    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            FragmentManager fragmentManager = getSupportFragmentManager();

            if (menuItem.getItemId() == R.id.home) {
                selectedFragment = homeFragment;
            } else if (menuItem.getItemId() == R.id.register) {
                selectedFragment = registerFragment;
            } else if (menuItem.getItemId() == R.id.diary) {
                selectedFragment = diaryFragment;
            } else if (menuItem.getItemId() == R.id.analysis) {
                selectedFragment = analysisFragment;
            }

            if (selectedFragment != null) {
                showFragment(selectedFragment);
                return true;
            }

            return false;
        }
        private void showFragment(Fragment fragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // 모든 프래그먼트 숨기기
            hideFragments(transaction);

            // 프래그먼트가 추가되지 않았으면 추가
            if (!fragment.isAdded()) {
                transaction.add(R.id.home_ly, fragment);
            }

            // 선택한 프래그먼트 보이기
            transaction.show(fragment);
            transaction.commit();
        }

        private void hideFragments(FragmentTransaction transaction) {
            if (homeFragment != null) {
                transaction.hide(homeFragment);
            }
            if (registerFragment != null) {
                transaction.hide(registerFragment);
            }
            if (diaryFragment != null) {
                transaction.hide(diaryFragment);
            }
            if (analysisFragment != null) {
                transaction.hide(analysisFragment);
            }
        }
    }

}
