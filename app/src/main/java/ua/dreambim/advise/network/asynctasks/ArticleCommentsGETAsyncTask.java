package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.R;
import ua.dreambim.advise.custom_views.BottomDetectScrollView;
import ua.dreambim.advise.entities.TheComment;
import ua.dreambim.advise.fragments.CommentFragment;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;
import ua.dreambim.advise.network.ResponseCode;
import ua.dreambim.advise.network.URLParams;

/**
 * Created by alexswyat on 21.12.2016.
 */

public class ArticleCommentsGETAsyncTask extends AsyncTask<String, Void, TheComment[]> {

    private final String PATH = "/articles/%s/comments";
    private final int COMMENTS_LIMIT = 10;

    CommentFragment fragment;

    public ArticleCommentsGETAsyncTask(CommentFragment fragment) {
        this.fragment = fragment;
    }

    private int status;
    private String errorMessage;
    private int offset = 0;

    @Override
    protected TheComment[] doInBackground(String... strings) {

        String article_id = strings[0];
        TheComment[] comments;

        ConnectivityManager connectivityManager = (ConnectivityManager) fragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try {
            URLParams urlParams = new URLParams();
            urlParams.add("offset", strings[1]);
            urlParams.add("limit", COMMENTS_LIMIT);

            offset = Integer.parseInt(strings[1]);

            URL url = new URL(Host.getHost() + String.format(PATH, article_id) + urlParams.getURLParamsString());

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            status = connection.getResponseCode();
            if (ResponseCode.isSuccess(status)) {
                JSONArray jsonArrayComments = JSONParser.getJSONArray(connection.getInputStream());

                comments = new TheComment[jsonArrayComments.length()];
                for (int i = 0; i < jsonArrayComments.length(); i++)
                {
                    comments[i] = getComment(jsonArrayComments.getJSONObject(i));
                    if (comments[i] == null)
                        return null;
                }

                return comments;
            }
            else
                errorMessage = JSONParser.getJSONObject(connection.getInputStream()).getString("error");

            connection.disconnect();



        } catch (Exception e) {}

        return null;
    }

    @Override
    protected void onPostExecute(TheComment[] theComments) {

        if ((fragment == null) || (fragment.getView() == null))
            return;

        if (offset != 0){
            BottomDetectScrollView bottomDetectScrollView = (BottomDetectScrollView) fragment.getView().findViewById(R.id.comments_scrollView);
            bottomDetectScrollView.removeProgressBar();
        }

        if (ResponseCode.isSuccess(status)) {
            if (offset == 0)
                fragment.updateView(theComments);
            else {
                fragment.addViews(theComments);
            }
        }
        else{}
    }

    private TheComment getComment(JSONObject jsonObject) {

        TheComment comment = new TheComment();

        try {
            comment.comment_id = jsonObject.getString(TheComment.KEY_comment_id);
            comment.body = jsonObject.getString(TheComment.KEY_comment_body);
            comment.authorNickname = jsonObject.getString(TheComment.KEY_comment_authorNickname);
        } catch (Exception e) {
            return null;
        }

        return comment;
    }
}
