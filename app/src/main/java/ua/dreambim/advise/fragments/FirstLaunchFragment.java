package ua.dreambim.advise.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ua.dreambim.advise.R;

/**
 * Created by Alexander on 13.01.2017.
 */

public class FirstLaunchFragment extends Fragment {

    private int mImageResourseId;
    private String title;
    private String body;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first_launch,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TextView) getView().findViewById(R.id.title)).setText(title);
        ((TextView) getView().findViewById(R.id.body)).setText(body);
        ((ImageView) getView().findViewById(R.id.screenshot)).setImageResource(mImageResourseId);
    }

    public void setParameters(int resId, String title, String body){
        mImageResourseId = resId;
        this.title = title;
        this.body= body;
    }
}
