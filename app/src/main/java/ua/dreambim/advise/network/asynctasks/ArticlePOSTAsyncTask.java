package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.activities.AdviseActivity;
import ua.dreambim.advise.activities.CreateArticleActivity;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;
import ua.dreambim.advise.network.ResponseCode;

/**
 * Created by MykhailoIvanov on 12/15/2016.
 */
public class ArticlePOSTAsyncTask extends AsyncTask<TheArticle, Void, Integer> {
    /*
        requires TheArticle[] with length == 1
     */

    private final String PATH = "/articles";

    private CreateArticleActivity activity;

    public ArticlePOSTAsyncTask (CreateArticleActivity activity){
        this.activity = activity;
    }

    private String errorMessage;

    @Override
    protected Integer doInBackground(TheArticle... articles) {
        if (Host.getToken(activity) == null)
            return null;

        TheArticle article = articles[0];

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URL url = new URL(Host.getHost() + PATH);

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty(Host.KEY_TOKEN, Host.getToken(activity));

            JSONParser json = new JSONParser();
            json.addField(AdviseActivity.KEY_LANGUAGE, AdviseActivity.getLanguage(activity));
            json.addField(TheArticle.KEY_title, article.title);
            json.addField(TheArticle.KEY_body, article.body);
            json.addStringArray(TheArticle.KEY_tags, article.tags);

            byte[] bytes = json.getStringForResponseBody().getBytes("UTF-8");
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(bytes);
            outputStream.close();

            int status = urlConnection.getResponseCode();

            if (!ResponseCode.isSuccess(status)){
                errorMessage = (JSONParser.getJSONObject(urlConnection.getInputStream())).getString("error");
            }

            urlConnection.disconnect();

            return status;

        }catch(Exception e){return null;}
    }

    protected void onPostExecute(Integer responseCode) {
        if (responseCode != null){
            if (ResponseCode.isSuccess(responseCode))
                activity.finish();
            else
                activity.showSnackbar(errorMessage);
        }
        else
            activity.showSnackbar("check connection");
    }
}
