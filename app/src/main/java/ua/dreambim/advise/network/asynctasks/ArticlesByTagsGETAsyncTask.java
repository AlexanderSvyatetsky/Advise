package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.R;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.fragments.FeedFragment;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;
import ua.dreambim.advise.network.URLParams;

/**
 * Created by MykhailoIvanov on 12/18/2016.
 */
public class ArticlesByTagsGETAsyncTask extends ArticlesDownloadAsyncTask  {
    /*
    Input String[]:
    strings[0] - language
    strings[1] - offset
    strings[2] - limit
    strings[3] - strings[unlimited] - tags
     */

    private final String PATH = "/articles";

    private FeedFragment feedFragment;

    public ArticlesByTagsGETAsyncTask(FeedFragment feedFragment){
        this.feedFragment = feedFragment;
    }


    private int offset = 0;
    private int status = 0;
    private String errorMessage = "check connection";

    @Override
    protected TheArticle[] doInBackground(String[] strings) {

        TheArticle[] articles = null;

        if (feedFragment == null || feedFragment.getActivity() == null)
            return null;

        ConnectivityManager connectivityManager = (ConnectivityManager) feedFragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null || (connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URLParams urlParams = new URLParams();
            urlParams.add("feed_type", FeedFragment.TYPE_BY_TAGS);
            urlParams.add("language", strings[0]);

            offset = Integer.parseInt(strings[1]);
            urlParams.add("offset", strings[1]);
            urlParams.add("limit", strings[2]);

            for (int i = 3; i < strings.length; i++)
                urlParams.add("tags", strings[i]);

            URL url = new URL(Host.getHost() + PATH + urlParams.getURLParamsString());
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestProperty(Host.KEY_TOKEN, Host.getToken(feedFragment.getActivity()));
            urlConnection.setRequestProperty(Host.KEY_APP_CODE, Host.app_code);
            urlConnection.setRequestMethod("GET");

            status = urlConnection.getResponseCode();

            if (status != 200){
                JSONObject jsonObject = JSONParser.getJSONObject(urlConnection.getInputStream());
                errorMessage = jsonObject.getString("error");
                return null;
            }

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
        if (feedFragment.getView() == null)
            return;

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) feedFragment.getView().findViewById(R.id.fragment_feed_swiperefreshlayout);

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

        if (articles != null)
            feedFragment.updateView(articles, (offset == 0));
        else
            feedFragment.showSnackbar(errorMessage);
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
