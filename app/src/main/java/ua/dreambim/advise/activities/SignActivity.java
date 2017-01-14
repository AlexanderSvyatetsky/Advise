package ua.dreambim.advise.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ua.dreambim.advise.R;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.fragments.PasswordResoreFragment;
import ua.dreambim.advise.fragments.SignInFragment;
import ua.dreambim.advise.fragments.SignUpFragment;

/**
 * Created by MykhailoIvanov on 12/6/2016.
 */
public class SignActivity extends AppCompatActivity {

    private SignInFragment signInFragment;
    private SignUpFragment signUpFragment;
    private PasswordResoreFragment passwordResoreFragment;

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

        setContentView(R.layout.activity_sign);


        signInFragment = new SignInFragment();
        signInFragment.setParameters(this);
        signUpFragment = new SignUpFragment();
        signUpFragment.setParameters(this);
        passwordResoreFragment = new PasswordResoreFragment();
        passwordResoreFragment.setActivity(this);

        setSignInFragment();
    }

    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.down_in, R.anim.down_out);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){

        finish();
        overridePendingTransition(R.anim.down_in, R.anim.down_out);
        return true;
    }


    public void setSignInFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_sign_frame, signInFragment);
        fragmentTransaction.commit();
    }

    public void setSignUpFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_sign_frame, signUpFragment);
        fragmentTransaction.commit();
    }

    public void setPasswordRestoreFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_sign_frame, passwordResoreFragment);
        fragmentTransaction.commit();
    }

    public void showSnackbar(String outputValue){

        if (outputValue == null)
            return;

        Snackbar.make(findViewById(R.id.activity_sign_frame), outputValue, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }
}
