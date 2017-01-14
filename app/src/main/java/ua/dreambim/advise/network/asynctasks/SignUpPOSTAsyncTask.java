package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.activities.SignActivity;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;
import ua.dreambim.advise.network.ResponseCode;

/**
 * Created by MykhailoIvanov on 12/15/2016.
 */
public class SignUpPOSTAsyncTask extends AsyncTask<TheUser, Void, Integer> {

    private final String PATH = "/sign_up";

    private SignActivity activity;

    public SignUpPOSTAsyncTask(SignActivity activity){
        this.activity = activity;
    }

    @Override
    protected Integer doInBackground(TheUser... theUsers) {

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URL url = new URL(Host.getHost() + PATH);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            JSONParser jsonParser = new JSONParser();
            jsonParser.addField(TheUser.KEY_email, theUsers[0].email);
            jsonParser.addField(TheUser.KEY_nickname, theUsers[0].nickname);
            jsonParser.addField(TheUser.KEY_password, theUsers[0].password);


            byte[] bytes = jsonParser.getStringForResponseBody().getBytes("UTF-8");
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(bytes);
            outputStream.close();

            int status = urlConnection.getResponseCode();

            if (!ResponseCode.isSuccess(status))
                errorMessage = (JSONParser.getJSONObject(urlConnection.getInputStream())).getString(Host.KEY_ERROR_COMMENT);
            urlConnection.disconnect();

            return status;

        }catch (Exception e){return null;}
    }

    private String errorMessage;

    @Override
    protected void onPostExecute(Integer responseCode) {
       if ((responseCode != null) && (ResponseCode.isSuccess(responseCode)))
           activity.setSignInFragment();
       else if (errorMessage != null)
           activity.showSnackbar(errorMessage);
       else
           activity.showSnackbar("failed");
    }
}
