package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.AdviseActivity;
import ua.dreambim.advise.fragments.NotificationFragment;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;

/**
 * Created by MykhailoIvanov on 12/21/2016.
 */
public class NotificationsCheckGETAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final String PATH = "/notifications/check";
    private final String KEY_NOTIFICATION_FLAG = "notifications";

    private AdviseActivity activity;

    public NotificationsCheckGETAsyncTask(AdviseActivity activity) {
        this.activity = activity;
    }

    private String errorMessage;
    private int status = 0;

    protected Boolean doInBackground(Void... voids) {

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try {
            URL url = new URL(Host.getHost() + PATH);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty(Host.KEY_TOKEN, Host.getToken(activity));

            status = urlConnection.getResponseCode();

            Boolean notifications = false;

            if (!((status >= 200) && (status < 300)))
                errorMessage = JSONParser.getJSONObject(urlConnection.getInputStream()).getString("error");
            else
                notifications = JSONParser.getJSONObject(urlConnection.getInputStream()).getBoolean(KEY_NOTIFICATION_FLAG);


            urlConnection.disconnect();

            return notifications;

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Boolean notifications) {
        if (notifications == null)
            return;

        if ((activity == null) || (activity.findViewById(R.id.nav_view) == null) || (((NavigationView) activity.findViewById(R.id.nav_view)).getMenu() == null))
            return;

        MenuItem notificationItem = ((NavigationView) activity.findViewById(R.id.nav_view)).getMenu().findItem(R.id.navigation_item_notifications);

        if (notificationItem == null)
            return;;

        if (notifications)
            notificationItem.setIcon(R.drawable.ic_notifications_active_48dp);
        else
            notificationItem.setIcon(R.drawable.ic_notifications_black_48dp);
    }
}
