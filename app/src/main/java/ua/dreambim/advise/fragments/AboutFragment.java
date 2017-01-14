package ua.dreambim.advise.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.dreambim.advise.R;

/**
 * Created by MykhailoIvanov on 1/2/2017.
 */
public class AboutFragment extends Fragment {

    private final String DREAMBIM_WEBSITE = "https://dreambim.com";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getView().findViewById(R.id.logo_imageview).setOnClickListener(new OnLogoClickListener());
    }


    private class OnLogoClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(DREAMBIM_WEBSITE));
            startActivity(intent);
        }
    }
}
