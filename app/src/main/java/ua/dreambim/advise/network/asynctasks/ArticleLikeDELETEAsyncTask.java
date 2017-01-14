package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.fragments.ArticleFragment;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;
import ua.dreambim.advise.network.ResponseCode;

/**
 * Created by MykhailoIvanov on 12/18/2016.
 */
public class ArticleLikeDELETEAsyncTask extends AsyncTask<String, Void, Integer> {
    /*
    strings[0] - article_id
     */

    private String getPath(String articleId){
        return "/articles/" + articleId + "/like";
    }

    private ArticleFragment fragment;
    private String article_id;
    private String errorMessage;

    public ArticleLikeDELETEAsyncTask(ArticleFragment fragment)
    {
        this.fragment = fragment;
    }

    @Override
    protected Integer doInBackground(String... strings) {

        if (Host.getToken(fragment.getActivity()) == null)
            return 403;

        this.article_id = strings[0];


        ConnectivityManager connectivityManager = (ConnectivityManager) fragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URL url = new URL(Host.getHost() + getPath(strings[0]));
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty(Host.KEY_TOKEN, Host.getToken(fragment.getActivity()));


            int status = urlConnection.getResponseCode();
            if (!ResponseCode.isSuccess(status))
                errorMessage = JSONParser.getJSONObject(urlConnection.getInputStream()).getString("error");

            urlConnection.disconnect();

            return status;

        }catch(Exception e){}

        return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == null)
            fragment.showSnackbar("check connection");
        else if (ResponseCode.isSuccess(result)) {
            (new ArticleGETAsyncTask(fragment, true)).execute(article_id);
        }
        else
            fragment.showSnackbar(errorMessage);
    }
}
