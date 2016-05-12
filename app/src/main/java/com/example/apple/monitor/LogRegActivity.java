package com.example.apple.monitor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;
import Fragments.LoginFragment;
import Fragments.ReginFragment;

/**
 * Created by apple on 15-5-12.
 */
public class LogRegActivity extends FragmentActivity {
    private List<Fragment> fragmentList = new ArrayList<>();
    private ViewPager viewPager;
    private FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
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
        setContentView(R.layout.activity_logreg);

        SharedPreferences mySharedPreferences= getSharedPreferences("log",
                Activity.MODE_PRIVATE);

        String requestJson = mySharedPreferences.getString("request", "");

        if(requestJson.length()!=0){
            Intent intent = new Intent(LogRegActivity.this,MainActivity.class);
            intent.putExtra("request",requestJson);
            startActivity(intent);
        }
        iniList();
    }



    private void iniList(){
        ReginFragment reginFragment = new ReginFragment(LogRegActivity.this);
        LoginFragment loginFragment = new LoginFragment(LogRegActivity.this);
        fragmentList.add(reginFragment);
        fragmentList.add(loginFragment);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentPagerAdapter);
    }
    @Override
    public void onBackPressed() {

    }
}
