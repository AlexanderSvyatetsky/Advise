package ua.dreambim.advise.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.AdviseActivity;
import ua.dreambim.advise.activities.SignActivity;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.network.asynctasks.SignUpPOSTAsyncTask;

/**
 * Created by MykhailoIvanov on 12/6/2016.
 */
public class SignUpFragment extends Fragment {

    private SignActivity activity;

    public void setParameters(SignActivity activity)
    {
        this.activity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    public void onStart() {
        super.onStart();

        (getView().findViewById(R.id.fragment_sign_button_ok)).setOnClickListener(new OnOkClickListener());
        (getView().findViewById(R.id.fragment_sign_button_sign_in)).setOnClickListener(new OnSignInClickListener());
    }

    private class OnSignInClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            activity.setSignInFragment();
        }
    }

    private class OnOkClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            EditText email = (EditText) getView().findViewById(R.id.fragment_sign_email);
            EditText nickname = (EditText) getView().findViewById(R.id.fragment_sign_nickname);
            EditText password = (EditText) getView().findViewById(R.id.fragment_sign_password);
            EditText confirm_password = (EditText) getView().findViewById(R.id.fragment_sign_confirmpassword);

            if (confirm_password.getText().toString().equals(password.getText().toString())){
                TheUser user = new TheUser();

                user.email = email.getText().toString();
                user.nickname = nickname.getText().toString();
                user.password = password.getText().toString();

                (new SignUpPOSTAsyncTask(activity)).execute(user);
            }
            else
                activity.showSnackbar("passwords do not match");
        }
    }
}
