package ua.dreambim.advise.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.SignActivity;

/**
 * Created by Alexander on 14.01.2017.
 */

public class FirstLaunchEndFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first_launch_end,container,false);
    }


    @Override
    public void onStart() {
        super.onStart();
        ((Button) getView().findViewById(R.id.sign_in_up_btn)).setOnClickListener(new OnSignInButtonClickListener());

    }

    private class OnSignInButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //TODO: To SignInActivity
            Intent intent = new Intent(getActivity().getApplicationContext(), SignActivity.class);
            startActivity(intent);
        }
    }
}
