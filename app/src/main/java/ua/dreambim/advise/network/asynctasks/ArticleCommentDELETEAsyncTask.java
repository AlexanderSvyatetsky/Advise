package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.entities.TheComment;
import ua.dreambim.advise.fragments.CommentFragment;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.ResponseCode;

/**
 * Created by MykhailoIvanov on 12/22/2016.
 */
public class ArticleCommentDELETEAsyncTask extends AsyncTask<TheComment, Void, Integer> {

    private String getPath(TheComment comment)
    {
        return "/articles/" + comment.article_id + "/comments/" + comment.comment_id;
    }

    private CommentFragment fragment;

    public ArticleCommentDELETEAsyncTask(CommentFragment fragment){
        this.fragment = fragment;
    }

    private String article_id;

    @Override
    protected Integer doInBackground(TheComment... comments) {

        ConnectivityManager connectivityManager = (ConnectivityManager) fragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URL url = new URL(Host.getHost() + getPath(comments[0]));
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            urlConnection.setRequestMethod("DELETE");
            urlConnection.setRequestProperty(Host.KEY_TOKEN, Host.getToken(fragment.getActivity()));

            int status = urlConnection.getResponseCode();
            article_id = comments[0].article_id;

            urlConnection.disconnect();

            return status;

        }catch(Exception e){}

        return null;
    }

    @Override
    protected void onPostExecute(Integer status) {
        if (status == null){}
        else if (ResponseCode.isSuccess(status))
            (new ArticleCommentsGETAsyncTask(fragment)).execute(article_id, "0");
    }
}
