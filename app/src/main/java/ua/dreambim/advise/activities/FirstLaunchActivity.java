package ua.dreambim.advise.activities;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import ua.dreambim.advise.R;
import ua.dreambim.advise.adapter.FirstLaunchFragmentPageAdapter;
import ua.dreambim.advise.fragments.FirstLaunchEndFragment;
import ua.dreambim.advise.fragments.FirstLaunchFragment;
import ua.dreambim.advise.fragments.FirstLaunchIntroFragment;

/**
 * Created by Alexander on 13.01.2017.
 */

public class FirstLaunchActivity extends AppCompatActivity {

    private FirstLaunchFragmentPageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        ((Button) findViewById(R.id.btn_next)).setOnClickListener(new OnNextButtonClickListener());
        ((Button) findViewById(R.id.btn_prev)).setOnClickListener(new OnPrevButtonClickListener());
        ((Button) findViewById(R.id.btn_skip)).setOnClickListener(new OnSkipButtonClickListener());

        viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new FirstLaunchFragmentPageAdapter(getFragmentManager());
        adapter.setFragments(initFragments());

        viewPager.setAdapter(adapter);
    }


    private ArrayList<Fragment> initFragments(){

        ArrayList<Fragment> fragments = new ArrayList<>();

        FirstLaunchIntroFragment introFragment = new FirstLaunchIntroFragment();
        fragments.add(introFragment);

        FirstLaunchFragment fragment1 = new FirstLaunchFragment();
        fragment1.setParameters(R.drawable.screen_1,getString(R.string.screen_1_title),getString(R.string.screen_1_body));
        fragments.add(fragment1);

        FirstLaunchFragment fragment2 = new FirstLaunchFragment();
        fragment2.setParameters(R.drawable.screen_2,getString(R.string.screen_2_title),getString(R.string.screen_2_body));
        fragments.add(fragment2);

        FirstLaunchFragment fragment3 = new FirstLaunchFragment();
        fragment3.setParameters(R.drawable.screen_3,getString(R.string.screen_3_title),getString(R.string.screen_3_body));
        fragments.add(fragment3);

        FirstLaunchFragment fragment4 = new FirstLaunchFragment();
        fragment4.setParameters(R.drawable.screen_4,getString(R.string.screen_4_title),getString(R.string.screen_4_body));
        fragments.add(fragment4);

        FirstLaunchFragment fragment5 = new FirstLaunchFragment();
        fragment5.setParameters(R.drawable.screen_5,getString(R.string.screen_5_title),getString(R.string.screen_5_body));
        fragments.add(fragment5);

        FirstLaunchFragment fragment6 = new FirstLaunchFragment();
        fragment6.setParameters(R.drawable.screen_6,getString(R.string.screen_6_title),getString(R.string.screen_6_body));
        fragments.add(fragment6);

        FirstLaunchEndFragment endFragment = new FirstLaunchEndFragment();
        fragments.add(endFragment);

        return fragments;
    }

    private class OnNextButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            viewPager.setCurrentItem(getItem(1),false);
        }

        private int getItem(int i) {
            return viewPager.getCurrentItem() + i;
        }
    }

    private class OnPrevButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            viewPager.setCurrentItem(getItem(1),false);
        }

        private int getItem(int i) {
            return viewPager.getCurrentItem() - i;
        }
    }

    private class OnSkipButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(FirstLaunchActivity.this, AdviseActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
