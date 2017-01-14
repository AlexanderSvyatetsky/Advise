package ua.dreambim.advise.network.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.R;
import ua.dreambim.advise.custom_views.BottomDetectScrollView;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.fragments.FeedFragment;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;
import ua.dreambim.advise.network.URLParams;
import ua.dreambim.advise.network.asynctasks.ArticlesDownloadAsyncTask;

/**
 * Created by MykhailoIvanov on 12/10/2016.
 */
public class ArticlesTopGETAsyncTask extends ArticlesDownloadAsyncTask {
    /*
    Input String[]:
    strings[0] - feed_type. Is it top NEW, BEST, MOST_DISCUSSED
    strings[1] - language
    strings[2] - offset
    strings[3] - limit
     */

    private final String PATH = "/articles";

    private FeedFragment feedFragment;

    public ArticlesTopGETAsyncTask(FeedFragment feedFragment, int offset){
        this.feedFragment = feedFragment;

        this.offset = offset;
    }


    private int offset = 0;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (feedFragment == null || feedFragment.getView() == null || offset == 0)
            return;

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)feedFragment.getView().findViewById(R.id.fragment_feed_swiperefreshlayout);
        if (swipeRefreshLayout == null)
            return;
        if (!swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(true);

    }

    @Override
    protected TheArticle[] doInBackground(String[] strings) {

        TheArticle[] articles = null;

        if ((feedFragment == null) || (feedFragment.getActivity() == null))
            return null;

        ConnectivityManager connectivityManager = (ConnectivityManager) feedFragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URLParams urlParams = new URLParams();
            urlParams.add("feed_type", strings[0]);
            urlParams.add("language", strings[1]);

            offset = Integer.parseInt(strings[2]);
            urlParams.add("offset", strings[2]);
            urlParams.add("limit", strings[3]);

            URL url = new URL(Host.getHost() + PATH + urlParams.getURLParamsString());
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestProperty(Host.KEY_TOKEN, Host.getToken(feedFragment.getActivity()));
            urlConnection.setRequestProperty(Host.KEY_APP_CODE, Host.app_code);
            urlConnection.setRequestMethod("GET");

            JSONArray jsonArrayArticles = JSONParser.getJSONArray(urlConnection.getInputStream());

            urlConnection.disconnect();

            articles = new TheArticle[jsonArrayArticles.length()];
            for (int i = 0; i < articles.length; i++)
                articles[i] = getArticle(jsonArrayArticles.getJSONObject(i));

        }catch(Exception e){articles = null;}

        return articles;
    }

    protected void onPostExecute(TheArticle[] articles)
    {
        if (feedFragment == null || feedFragment.getView() == null)
            return;

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) feedFragment.getView().findViewById(R.id.fragment_feed_swiperefreshlayout);

        if (offset != 0)
            ((BottomDetectScrollView) feedFragment.getView().findViewById(R.id.fragment_feed_scrollview)).removeProgressBar();

        if (articles != null) {
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);

            feedFragment.updateView(articles, (offset == 0));
        }
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
            JSONArray tagsArray = jsonObject.getJSONArray(TheArticle.KEY_tags);
            article.tags = new String[tagsArray.length()];
            for (int i = 0; i < article.tags.length; i++)
                article.tags[i] = tagsArray.getString(i);

        }catch(Exception e){article = null;}

        return article;
    }
}
