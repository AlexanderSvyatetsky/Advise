package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.activities.CommentsActivity;
import ua.dreambim.advise.activities.SignActivity;
import ua.dreambim.advise.activities.UserProfileActivity;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;
import ua.dreambim.advise.network.ResponseCode;
import ua.dreambim.advise.network.URLParams;

/**
 * Created by MykhailoIvanov on 1/12/2017.
 */
public class UserPasswordRestoreGETAsyncTask extends AsyncTask<String, Void, Integer> {

    private final String PATH = "/users/passwordrestore";

    /*
    strings[0] - email
     */

    private SignActivity activity;
    private String error;

    public UserPasswordRestoreGETAsyncTask(SignActivity activity){
        this.activity = activity;
    }

    public Integer doInBackground(String... strings){
        if (activity == null)
            return null;

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URLParams urlParams = new URLParams();
            urlParams.add(TheUser.KEY_email, strings[0]);
            URL url = new URL(Host.getHost() + PATH + urlParams.getURLParamsString());
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            int status = urlConnection.getResponseCode();

            if (!ResponseCode.isSuccess(status))
                error = JSONParser.getJSONObject(urlConnection.getInputStream()).getString("error");

            urlConnection.disconnect();

            return status;
        }catch(Exception e){return null;}
    }

    @Override
    protected void onPostExecute(Integer status) {
        if (status != null && (ResponseCode.isSuccess(status)))
            activity.setSignInFragment();
        else if (error != null)
            activity.showSnackbar(error);
        else
            activity.showSnackbar("failed");
    }
}
