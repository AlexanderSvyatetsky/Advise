package ua.dreambim.advise.network.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.activities.UserProfileActivity;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;

/**
 * Created by MykhailoIvanov on 12/18/2016.
 */
public class UsersArticlesGETAsyncTask extends AsyncTask<String, Void, TheArticle[]> {

    /*
    strings[0] - nickname
     */

    private String getPath(String nickname){
        return "/users/" + nickname + "/articles";
    }

    private UserProfileActivity activity;

    public UsersArticlesGETAsyncTask(UserProfileActivity activity){
        this.activity = activity;
    }


    private String errorMessage;

    @Override
    protected TheArticle[] doInBackground(String... strings) {

        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URL url = new URL(Host.getHost() + getPath(strings[0]));
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");

            JSONArray jsonArray = JSONParser.getJSONArray(urlConnection.getInputStream());

            TheArticle[] articles = new TheArticle[jsonArray.length()];
            for (int i = 0; i < articles.length; i++)
                articles[i] = getArticle(jsonArray.getJSONObject(i));

            urlConnection.disconnect();

            return articles;

        }catch(Exception e){}

        return null;
    }

    @Override
    protected void onPostExecute(TheArticle[] articles) {
        if (articles != null)
            activity.setArticlesView(articles);
    }

    private TheArticle getArticle(JSONObject jsonObject){
        TheArticle article= new TheArticle();

        try{
            article.article_id = jsonObject.getString(TheArticle.KEY_article_id);
            article.language = jsonObject.getInt(TheArticle.KEY_language);
            article.title = jsonObject.getString(TheArticle.KEY_title);
            article.body = jsonObject.getString(TheArticle.KEY_body);
            article.likesNumber = jsonObject.getInt(TheArticle.KEY_likesNumber);
            article.commentsNumber = jsonObject.getInt(TheArticle.KEY_commentsNumber);

        }catch(Exception e){article = null;}

        return article;
    }
}
