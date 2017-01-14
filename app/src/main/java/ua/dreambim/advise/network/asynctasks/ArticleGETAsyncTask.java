package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.AdviseActivity;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.fragments.ArticleFragment;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;

/**
 * Created by MykhailoIvanov on 12/15/2016.
 */
public class ArticleGETAsyncTask extends AsyncTask<String, Void, TheArticle> {

    /*
    String[]: strings[0] - article_id
     */

    private final String PATH = "/articles/";

    private ArticleFragment fragment;
    private boolean doNotUseProgressBar;

    public ArticleGETAsyncTask(ArticleFragment fragment){
        this.fragment = fragment;
        doNotUseProgressBar = false;
    }

    public ArticleGETAsyncTask(ArticleFragment fragment, boolean doNotUseProgressBar){
        this.fragment = fragment;
        this.doNotUseProgressBar = doNotUseProgressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (fragment == null || fragment.getView() == null || doNotUseProgressBar)
            return;

        fragment.getView().findViewById(R.id.content).setVisibility(View.GONE);
        fragment.getView().findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
    }

    protected TheArticle doInBackground(String... strings) {

        String article_id = strings[0];

        ConnectivityManager connectivityManager = (ConnectivityManager) fragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URL url = new URL(Host.getHost() + PATH + article_id);

            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            if (Host.getToken(fragment.getActivity()) != null)
                urlConnection.setRequestProperty(Host.KEY_TOKEN, Host.getToken(fragment.getActivity()));

            JSONObject jsonObject = JSONParser.getJSONObject(urlConnection.getInputStream());

            urlConnection.disconnect();

            return getArticle(jsonObject);

        }catch (Exception e){return null;}
    }

    @Override
    protected void onPostExecute(TheArticle article) {

        if (fragment == null || fragment.getView() == null)
            return;

        if (article != null){
            fragment.getView().findViewById(R.id.content).setVisibility(View.VISIBLE);
            fragment.getView().findViewById(R.id.progress_bar).setVisibility(View.GONE);

            fragment.setEntities(article, new TheUser());
            fragment.getFullArticleView();
        }

    }

    private TheArticle getArticle(JSONObject jsonObject){
        TheArticle article = new TheArticle();

        try{
            article.article_id = jsonObject.getString(TheArticle.KEY_article_id);
            article.language = jsonObject.getInt(TheArticle.KEY_language);
            article.title = jsonObject.getString(TheArticle.KEY_title);
            article.body = jsonObject.getString(TheArticle.KEY_body);
            article.authorNickname = jsonObject.getString(TheArticle.KEY_authorNickname);
            article.likesNumber = jsonObject.getInt(TheArticle.KEY_likesNumber);
            article.commentsNumber = jsonObject.getInt(TheArticle.KEY_commentsNumber);
            JSONArray tagsArray = jsonObject.getJSONArray(TheArticle.KEY_tags);
            article.tags = new String[tagsArray.length()];
            for (int i = 0; i < article.tags.length; i++)
                article.tags[i] = tagsArray.getString(i);
            article.date = jsonObject.getString(TheArticle.KEY_date);
            article.time = jsonObject.getString(TheArticle.KEY_time);

            if (TheUser.getAuthorizedUser(fragment.getActivity()) != null)
                article.liked = jsonObject.getBoolean(TheArticle.KEY_liked);

        }catch(Exception e){}

        return article;
    }
}
