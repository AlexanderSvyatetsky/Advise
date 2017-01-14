package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.R;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.entities.TheComment;
import ua.dreambim.advise.fragments.NotificationFragment;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;
import ua.dreambim.advise.network.ResponseCode;

/**
 * Created by alexswyat on 18.12.2016.
 */

public class NotificationsGETAsyncTask extends AsyncTask<String, Void, Integer> {

    public static final String NOTIFICATION_NEW_ARTICLE_COMMENT = "new_article_comments_array";
    public static final String NOTIFICATION_NEW_COMMENT_BELOW = "new_comments_below_array";

    private final String PATH = "/notifications";

    private final static String KEY_article = "article";
    private final static String KEY_comment = "comment";
    private final static String KEY_comment_body = "body";
    private final static String KEY_comment_authorNickname = "authorNickname";

    private NotificationFragment notificationFragment;

    private TheComment[] comments;
    private HashMap<String, Integer> articlesCommentsNotificationsNumber;
    private HashMap<String, TheArticle> articlesHashMap;

    public NotificationsGETAsyncTask(NotificationFragment notificationFragment) {
        this.notificationFragment = notificationFragment;
    }

    @Override
    protected void onPreExecute() {
        notificationFragment.mNotificationLayout.setVisibility(View.GONE);
        notificationFragment.mNotificationProgressBar.setVisibility(View.VISIBLE);

        articlesCommentsNotificationsNumber = new HashMap<String, Integer>();
        articlesHashMap = new HashMap<String, TheArticle>();
    }

    @Override
    protected Integer doInBackground(String... params) {

        ConnectivityManager connectivityManager = (ConnectivityManager) notificationFragment.getActivity().
                getSystemService(Context.CONNECTIVITY_SERVICE);

        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try {

            URL url = new URL(Host.getHost() + PATH);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty(Host.KEY_TOKEN, Host.getToken(notificationFragment.getActivity()));

            int status = urlConnection.getResponseCode();

            JSONObject jsonNotifications = JSONParser.getJSONObject(urlConnection.getInputStream());

            urlConnection.disconnect();


            JSONArray jsonArrayNotificationsArticleNewComment = jsonNotifications.
                    getJSONArray(NOTIFICATION_NEW_ARTICLE_COMMENT);
            JSONArray jsonArrayNotificationsNewCommentBelow = jsonNotifications.
                    getJSONArray(NOTIFICATION_NEW_COMMENT_BELOW);

            for (int i = 0; i < jsonArrayNotificationsArticleNewComment.length(); i++){

                TheArticle article = getArticle(jsonArrayNotificationsArticleNewComment.getJSONObject(i).getJSONObject("article"));

                if (articlesHashMap.containsKey(article.article_id)){
                    articlesCommentsNotificationsNumber.put(article.article_id, 1 + articlesCommentsNotificationsNumber.get(article.article_id));
                }
                else {
                    articlesCommentsNotificationsNumber.put(article.article_id, 1);
                    articlesHashMap.put(article.article_id, article);
                }
            }

            comments = new TheComment[jsonArrayNotificationsNewCommentBelow.length()];
            for (int i = 0; i < jsonArrayNotificationsNewCommentBelow.length(); i++){
                comments[i] = getComment(jsonArrayNotificationsNewCommentBelow.getJSONObject(i).getJSONObject("comment"));
                TheArticle article = getArticle(jsonArrayNotificationsNewCommentBelow.getJSONObject(i).getJSONObject("article"));
                comments[i].article_id = article.article_id;
                comments[i].authorNickname = article.title;
            }


            return status;

        } catch (Exception e) {return null;}
    }


    protected void onPostExecute(Integer status) {

        if (notificationFragment == null || notificationFragment.getView() == null)
            return;

        if ((status != null) && (ResponseCode.isSuccess(status))){
            notificationFragment.mNotificationLayout.setVisibility(View.VISIBLE);
            notificationFragment.mNotificationProgressBar.setVisibility(View.GONE);

            notificationFragment.addNotificationNewArticleCommentsView(articlesCommentsNotificationsNumber, articlesHashMap);
            notificationFragment.addNotificationNewCommentBelowView(comments);

            if (((comments == null) || (comments.length == 0)) && (articlesHashMap == null || (articlesHashMap.size() == 0))) {
                (notificationFragment.getView().findViewById(R.id.no_notifications)).setVisibility(View.VISIBLE);
                (notificationFragment.getView()).findViewById(R.id.delete_button).setVisibility(View.GONE);
            }
            else {
                (notificationFragment.getView().findViewById(R.id.no_notifications)).setVisibility(View.GONE);
                (notificationFragment.getView()).findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
            }
        }
    }

    private TheArticle getArticle(JSONObject jsonObject){
        TheArticle article = new TheArticle();

        try{
            article.article_id = jsonObject.getString(TheArticle.KEY_article_id);
            article.title = jsonObject.getString(TheArticle.KEY_title);

        }catch(Exception e){return null;}

        return article;
    }

    private TheComment getComment(JSONObject jsonObject){
        TheComment comment = new TheComment();

        try{
            comment.body = jsonObject.getString(TheComment.KEY_comment_body);
            comment.authorNickname = jsonObject.getString(TheComment.KEY_comment_authorNickname);

        }catch(Exception e){return null;}

        return comment;
    }
}
