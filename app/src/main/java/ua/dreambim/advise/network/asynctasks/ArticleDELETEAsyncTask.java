package ua.dreambim.advise.network.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.activities.AdviseActivity;
import ua.dreambim.advise.activities.UserProfileActivity;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;
import ua.dreambim.advise.network.ResponseCode;
import ua.dreambim.advise.network.URLParams;

/**
 * Created by MykhailoIvanov on 12/11/2016.
 */
public class ArticleDELETEAsyncTask extends AsyncTask<String, Void, Integer> {

    private final String PATH = "/articles/";

    private UserProfileActivity userProfileActivity;
    private AdviseActivity adviseActivity;
    private Activity activity;

    public ArticleDELETEAsyncTask(UserProfileActivity activity)
    {
        userProfileActivity = activity;
        this.adviseActivity = null;
        this.activity = activity;
    }

    public ArticleDELETEAsyncTask(AdviseActivity activity){
        userProfileActivity = null;
        this.adviseActivity = activity;
        this.activity = activity;
    }

    @Override
    protected Integer doInBackground(String... strings) {

        if (Host.getToken(activity) == null)
            return null;

        String article_id = strings[0];

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URL url = new URL(Host.getHost() + PATH + article_id);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestProperty(Host.KEY_TOKEN, Host.getToken(activity));
            urlConnection.setRequestProperty(Host.KEY_APP_CODE, Host.app_code);
            urlConnection.setRequestMethod("DELETE");

            int responseCode = urlConnection.getResponseCode();

            urlConnection.disconnect();

            return responseCode;

        }catch(Exception e){return null;}
    }

    protected void onPostExecute(Integer responseCode) {

        if ((adviseActivity != null) && (responseCode != null) && (ResponseCode.isSuccess(responseCode))){
            adviseActivity.setNewFeedFragment();
        }
        else if ((userProfileActivity != null) && (responseCode != null) && (ResponseCode.isSuccess(responseCode)) && (userProfileActivity.get_user_nickname() != null))
            (new UsersArticlesGETAsyncTask(userProfileActivity)).execute(userProfileActivity.get_user_nickname());
    }
}
