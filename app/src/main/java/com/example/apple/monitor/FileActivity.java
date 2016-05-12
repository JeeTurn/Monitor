package com.example.apple.monitor;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

import Fragments.PicFragment;
import Fragments.VideoFragment;

/**
 * Created by apple on 16/5/3.
 */
public class FileActivity extends FragmentActivity {
    List fragmentList = new ArrayList<>();
    ViewPager fileviewpager;
    FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return (Fragment) fragmentList.get(position);
        }
        @Override
        public int getCount() {
            return fragmentList.size();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_file);
        fileviewpager = (ViewPager)findViewById(R.id.fileviewpager);
        fragmentList.add(new PicFragment(FileActivity.this));
        fragmentList.add(new VideoFragment(FileActivity.this));
        fileviewpager.setAdapter(fragmentPagerAdapter);
    }
}
