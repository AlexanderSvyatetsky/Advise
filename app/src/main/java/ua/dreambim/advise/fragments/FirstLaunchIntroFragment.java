package ua.dreambim.advise.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.dreambim.advise.R;

/**
 * Created by Alexander on 14.01.2017.
 */

public class FirstLaunchIntroFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first_launch_intro,container,false);
    }
}
