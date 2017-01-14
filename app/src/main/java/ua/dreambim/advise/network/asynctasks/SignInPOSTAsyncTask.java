package ua.dreambim.advise.network.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.activities.AdviseActivity;
import ua.dreambim.advise.activities.SignActivity;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;

/**
 * Created by MykhailoIvanov on 12/15/2016.
 */
public class SignInPOSTAsyncTask extends AsyncTask<TheUser, Void, String[]> {

    /*
    requires only theUsers[0] with nickname and password fields filled
     */

    private final String PATH = "/sign_in/";

    private SignActivity signInActivity;
    private Activity activity;
    private String password;

    public SignInPOSTAsyncTask(SignActivity activity){
        this.activity = activity;
        signInActivity = activity;
    }

    public SignInPOSTAsyncTask(AdviseActivity activity){
        this.activity = activity;
        signInActivity = null;
    }


    private int status;

    @Override
    protected String[] doInBackground(TheUser... theUsers) {

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URL url = new URL(Host.getHost() + PATH);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            JSONParser jsonParser = new JSONParser();
            jsonParser.addField(TheUser.KEY_nickname, theUsers[0].nickname);
            jsonParser.addField(TheUser.KEY_password, theUsers[0].password);
            this.password = theUsers[0].password;

            byte[] bytes = jsonParser.getStringForResponseBody().getBytes("UTF-8");
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(bytes);
            outputStream.close();

            status = urlConnection.getResponseCode();

            JSONObject response = JSONParser.getJSONObject(urlConnection.getInputStream());
            urlConnection.disconnect();

            String[] responseString;

            if (status == 200){
                responseString = new String[3];
                responseString[0] = response.getString(Host.KEY_TOKEN);
                responseString[1] = response.getString(Host.KEY_EXPIRES_IN);
                responseString[2] = theUsers[0].nickname;
            }
            else{
                responseString = new String[1];
                responseString[0] = response.getString(Host.KEY_ERROR_COMMENT);
            }

            return responseString;

        }catch (Exception e){return null;}
    }

    @Override
    protected void onPostExecute(String[] strings) {

        if (signInActivity == null)
            return;

        if (strings == null) {
            signInActivity.showSnackbar("failed");
            return;
        }

        if (status == 200) {
            Host.putToken(activity, strings[0]);
            (new UserGETAsyncTask(signInActivity, true, true, password)).execute(strings[2]);
        }
        else
            signInActivity.showSnackbar(strings[0]);

    }
}
