package ua.dreambim.advise.network.asynctasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ua.dreambim.advise.activities.CommentsActivity;
import ua.dreambim.advise.entities.TheComment;
import ua.dreambim.advise.fragments.CommentFragment;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.JSONParser;
import ua.dreambim.advise.network.ResponseCode;

/**
 * Created by MykhailoIvanov on 12/21/2016.
 */
public class ArticleCommentsPOSTAsyncTask extends AsyncTask<TheComment, Void, Integer> {

    private String getPath(String article_id){
        return "/articles/" + article_id + "/comments";
    }

    private CommentFragment fragment;

    public ArticleCommentsPOSTAsyncTask(CommentFragment fragment)
    {
        this.fragment = fragment;
    }

    private String errorMessage;
    private String article_id;

    @Override
    protected Integer doInBackground(TheComment... comments) {

        ConnectivityManager connectivityManager = (ConnectivityManager) fragment.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        if ((connectivityManager.getActiveNetworkInfo() == null) || (!connectivityManager.getActiveNetworkInfo().isConnected()))
            return null;

        try{
            URL url = new URL(Host.getHost() + getPath(comments[0].article_id));
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            Log.d("logs", Host.getHost() + getPath(comments[0].article_id));
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty(Host.KEY_TOKEN, Host.getToken(fragment.getActivity()));

            JSONParser json = new JSONParser();
            json.addField(TheComment.KEY_comment_body, comments[0].body);
            article_id = comments[0].article_id;

            Log.d("logs", json.getStringForResponseBody());

            byte[] bytes = json.getStringForResponseBody().getBytes("UTF-8");
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(bytes);
            outputStream.close();

            int status = urlConnection.getResponseCode();

            Log.d("logs", Integer.toString(status));

            if (!ResponseCode.isSuccess(status)){
                errorMessage = (JSONParser.getJSONObject(urlConnection.getInputStream())).getString("error");
            }

            urlConnection.disconnect();

            return status;

        }catch(Exception e){return null;}
    }

    @Override
    protected void onPostExecute(Integer status) {

        if (errorMessage != null)
            Log.d("logs", errorMessage);

        if (status == null)
        {}
        else if (ResponseCode.isSuccess(status))
            (new ArticleCommentsGETAsyncTask(fragment)).execute(article_id,"0");

    }
}
