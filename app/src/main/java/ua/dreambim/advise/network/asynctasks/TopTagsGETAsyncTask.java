package ua.dreambim.advise.network.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.widget.SearchView;

import org.json.JSONArray;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.activities.AdviseActivity;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.fragments.SearchFragment;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;
import ua.dreambim.advise.network.URLParams;

/**
 * Created by MykhailoIvanov on 12/15/2016.
 */
public class TopTagsGETAsyncTask extends AsyncTask<Integer, Void, String[]> {

    /*
        params[0] - language
        params[1] - limit
     */

    private final String PATH = "/top_tags";

    private Activity activity;
    private AdviseActivity adviseActivity;
    private SearchFragment searchFragment;

    public TopTagsGETAsyncTask(AdviseActivity activity){
        this.activity = activity;
        this.adviseActivity = activity;

        searchFragment = null;
    }

    public TopTagsGETAsyncTask(AdviseActivity activity, SearchFragment searchFragment){
        this.activity = activity;
        this.adviseActivity = activity;
        this.searchFragment = searchFragment;
    }

    public TopTagsGETAsyncTask(Activity activity){
        this.activity = activity;
    }

    @Override
    protected String[] doInBackground(Integer... params) {

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URLParams urlParams = new URLParams();
            urlParams.add(AdviseActivity.KEY_LANGUAGE, params[0]);
            urlParams.add(Host.KEY_LIMIT, params[1]);


            URL url = new URL(Host.getHost() + PATH + urlParams.getURLParamsString());
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");

            JSONArray jsonArray = JSONParser.getJSONArray(urlConnection.getInputStream());

            urlConnection.disconnect();

            String[] tags = new String[jsonArray.length()];
            for (int i = 0; i < tags.length; i++)
                tags[i] = jsonArray.getString(i);

            return tags;

        }catch(Exception e){return null;}
    }

    @Override
    protected void onPostExecute(String[] tags) {
        if (tags != null) {

            if (searchFragment == null)
                TheArticle.tagsForAutocompleteText = tags;
            else
                searchFragment.showTopTags(tags);
        }
        else if (adviseActivity != null)
            adviseActivity.showSnackbar("check connection");
    }
}
