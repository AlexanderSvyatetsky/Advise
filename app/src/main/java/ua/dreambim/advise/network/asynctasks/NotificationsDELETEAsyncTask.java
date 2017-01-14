package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.fragments.NotificationFragment;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;

/**
 * Created by MykhailoIvanov on 12/21/2016.
 */
public class NotificationsDELETEAsyncTask extends AsyncTask<Void, Void, Integer>{

    private String PATH = "/notifications";

    private NotificationFragment fragment;

    public NotificationsDELETEAsyncTask(NotificationFragment fragment)
    {
        this.fragment = fragment;
    }

    private String errorMessage;

    @Override
    protected Integer doInBackground(Void... voids) {

        ConnectivityManager connectivityManager = (ConnectivityManager) fragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URL url = new URL(Host.getHost() + PATH);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty(Host.KEY_TOKEN, Host.getToken(fragment.getActivity()));

            int status = urlConnection.getResponseCode();
            if (!((status >= 200) && (status < 300)))
                errorMessage = JSONParser.getJSONObject(urlConnection.getInputStream()).getString("error");


            urlConnection.disconnect();

            return status;

        }catch(Exception e){return null;}

    }

    @Override
    protected void onPostExecute(Integer status) {
        if (status == null){}
        else if ((status >= 200) && (status < 300))
            (new NotificationsGETAsyncTask(fragment)).execute();
    }
}
