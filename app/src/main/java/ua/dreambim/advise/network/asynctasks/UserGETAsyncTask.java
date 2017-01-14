package ua.dreambim.advise.network.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.activities.UserProfileActivity;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;

/**
 * Created by MykhailoIvanov on 12/15/2016.
 */
public class UserGETAsyncTask extends AsyncTask<String, Void, TheUser> {

    private final String PATH = "/users/";

    private Activity activity;
    private boolean finishActivityAfterProcess;
    private boolean isItAuthUser;
    private String password;

    public UserGETAsyncTask(Activity activity, boolean finishActivityAfterProcess, boolean isItAuthUser){
        this.activity = activity;
        this.finishActivityAfterProcess = finishActivityAfterProcess;
        this.isItAuthUser = isItAuthUser;
    }

    public UserGETAsyncTask(Activity activity, boolean finishActivityAfterProcess, boolean isItAuthUser, String password){
        this.activity = activity;
        this.finishActivityAfterProcess = finishActivityAfterProcess;
        this.isItAuthUser = isItAuthUser;
        this.password = password;
    }

    private UserProfileActivity userProfileActivity;

    public UserGETAsyncTask(UserProfileActivity userProfileActivity){
        this.userProfileActivity = userProfileActivity;
        activity = userProfileActivity;
    }

    @Override
    protected TheUser doInBackground(String... strings) {

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URL url = new URL(Host.getHost() + PATH + strings[0]);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            int status = urlConnection.getResponseCode();

            JSONObject jsonObject = JSONParser.getJSONObject(urlConnection.getInputStream());
            if (status == 200){
                TheUser user = new TheUser();
                user.email = jsonObject.getString(TheUser.KEY_email);
                user.nickname = jsonObject.getString(TheUser.KEY_nickname);
                if (this.password != null)
                    user.password = this.password;

//                jsonObject.has(TheUser.KEY_avatarURL);
//                    user.avatarURL = jsonObject.getString(TheUser.KEY_avatarURL);
                user.articlesTotal = jsonObject.getInt(TheUser.KEY_articlesTotal);
                user.articlesLikes = jsonObject.getInt(TheUser.KEY_articlesLikes);
                user.articlesLiked = jsonObject.getInt(TheUser.KEY_articlesLiked);
                return user;
            }
            urlConnection.disconnect();

        }catch(Exception e){}

        return null;
    }

    @Override
    protected void onPostExecute(TheUser theUser) {

        if (theUser != null){

            if (userProfileActivity != null)
                userProfileActivity.setHead(theUser);

            if (isItAuthUser && theUser != null)
                TheUser.saveAuthorizedUser(activity, theUser);
            if (finishActivityAfterProcess && activity != null)
                activity.finish();
        }
    }
}
