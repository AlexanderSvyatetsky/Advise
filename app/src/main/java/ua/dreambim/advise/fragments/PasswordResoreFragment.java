package ua.dreambim.advise.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.SignActivity;
import ua.dreambim.advise.network.asynctasks.UserPasswordRestoreGETAsyncTask;

/**
 * Created by MykhailoIvanov on 1/12/2017.
 */
public class PasswordResoreFragment extends Fragment {

    private SignActivity activity;

    public void setActivity(SignActivity activity){
        this.activity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passwordrestore, container, false);
    }

    public void onStart(){
        super.onStart();

        getView().findViewById(R.id.fragment_sign_button_ok).setOnClickListener(new OnOkClickListener());
        getView().findViewById(R.id.fragment_sign_button_sign_in).setOnClickListener(new OnSignInClickListener());
    }

    private class OnOkClickListener implements View.OnClickListener{
        public void onClick(View v){
            EditText emailEditText = (EditText) PasswordResoreFragment.this.getView().findViewById(R.id.fragment_passwordrestore_email);
            if (emailEditText.getText().toString() != null && emailEditText.getText().toString().length() != 0)
                (new UserPasswordRestoreGETAsyncTask(activity)).execute(emailEditText.getText().toString());
            else
                activity.showSnackbar("empty field");
        }
    }

    private class OnSignInClickListener implements View.OnClickListener{
        public void onClick(View v){
            activity.setSignInFragment();
        }
    }
}
