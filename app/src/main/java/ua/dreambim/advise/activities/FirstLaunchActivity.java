package ua.dreambim.advise.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import ua.dreambim.advise.R;
import ua.dreambim.advise.adapter.FirstLaunchFragmentPageAdapter;

/**
 * Created by Alexander on 13.01.2017.
 */

public class FirstLaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        FirstLaunchFragmentPageAdapter adapter = new FirstLaunchFragmentPageAdapter(getFragmentManager());
        adapter.setFragments(initFragments());

        viewPager.setAdapter(adapter);
    }


    private ArrayList<Fragment> initFragments(){
        return null;
    }
}
