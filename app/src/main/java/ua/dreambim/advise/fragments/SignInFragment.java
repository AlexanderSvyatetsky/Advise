package ua.dreambim.advise.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.AdviseActivity;
import ua.dreambim.advise.activities.SignActivity;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.network.asynctasks.SignInPOSTAsyncTask;

/**
 * Created by MykhailoIvanov on 12/6/2016.
 */
public class SignInFragment extends Fragment {

    private SignActivity activity;

    public void setParameters(SignActivity activity)
    {
        this.activity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){

        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    public void onStart() {
        super.onStart();

        (getView().findViewById(R.id.fragment_sign_button_ok)).setOnClickListener(new OnOkClickListener());
        (getView().findViewById(R.id.fragment_sign_button_sign_up)).setOnClickListener(new OnSignUpClickListener());
        getView().findViewById(R.id.restorepassword_button).setOnClickListener(new OnResorePasswordButtonClickListener());
    }

    private class OnSignUpClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            activity.setSignUpFragment();
        }
    }

    private class OnOkClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            EditText nickname = (EditText) getView().findViewById(R.id.fragment_sign_nickname_or_email);
            EditText password = (EditText) getView().findViewById(R.id.fragment_sign_password);

            TheUser user = new TheUser();
            user.nickname = nickname.getText().toString();
            user.password = password.getText().toString();

            SignInPOSTAsyncTask asyncTask = new SignInPOSTAsyncTask(activity);
            asyncTask.execute(user);
        }
    }

    private class OnResorePasswordButtonClickListener implements View.OnClickListener{

        public void onClick(View view) {
            activity.setPasswordRestoreFragment();
        }
    }
}
