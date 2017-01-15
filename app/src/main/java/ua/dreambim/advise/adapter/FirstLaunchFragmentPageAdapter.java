package ua.dreambim.advise.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Alexander on 13.01.2017.
 */

public class FirstLaunchFragmentPageAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments;

    public FirstLaunchFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setFragments(ArrayList fragments){
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return fragments.get(position);
        }else if(position == 1){
            return fragments.get(position);
        } else if(position == 2){
            return fragments.get(position);
        } else if(position == 3){
            return fragments.get(position);
        } else if(position == 4){
            return fragments.get(position);
        } else if(position == 5){
            return fragments.get(position);
        } else if(position == 6){
            return fragments.get(position);
        } else if(position == 7){
            return fragments.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 8;
    }


}
