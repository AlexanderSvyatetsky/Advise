package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.activities.UserProfileActivity;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.ResponseCode;

/**
 * Created by MykhailoIvanov on 1/12/2017.
 */
public class UserAvatarUploadPOSTAsyncTask extends AsyncTask<Void, Void, Integer> {
    private UserProfileActivity activity;
    private byte[] imageBytes;

    private String PATH = "/upload";

    public UserAvatarUploadPOSTAsyncTask(UserProfileActivity activity, byte[] imageBytes){
        this.activity = activity;
        this.imageBytes = imageBytes;
    }

    protected Integer doInBackground(Void... voids) {

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URL url = new URL(Host.getHost() + PATH);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type", "multipart/form-data");
            urlConnection.addRequestProperty(Host.KEY_TOKEN, Host.getToken(activity));

            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(imageBytes);
            outputStream.close();

            int status = urlConnection.getResponseCode();

            urlConnection.disconnect();

            return status;

        }catch(Exception e){}
        return null;
    }

    @Override
    protected void onPostExecute(Integer status) {

        if (activity == null || status == null)
            return;

        if (ResponseCode.isSuccess(status))
            (new UserGETAsyncTask(activity)).execute(activity.get_user_nickname());
    }
}
