package ua.dreambim.advise.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ua.dreambim.advise.R;
import ua.dreambim.advise.custom_views.BottomDetectScrollView;
import ua.dreambim.advise.entities.TheComment;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.network.asynctasks.ArticleCommentDELETEAsyncTask;
import ua.dreambim.advise.network.asynctasks.ArticleCommentsGETAsyncTask;
import ua.dreambim.advise.network.asynctasks.ArticleCommentsPOSTAsyncTask;


public class CommentFragment extends Fragment {

    private final static String LOG_TAG = CommentFragment.class.getSimpleName();

    private final static String AT = "@";

    private String mArticleId;

    private Activity activity;

    private LinearLayout rootLayout;
    private BottomDetectScrollView bottomDetectScrollView;

    public void setParameters(Activity activity, String articleId) {
        this.activity = activity;
        this.mArticleId = articleId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed_comments, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        rootLayout = (LinearLayout) getView().findViewById(R.id.fragment_feed_comment_layout);

        (getView().findViewById(R.id.send_imageview)).setOnClickListener(new OnSendImageClicked());

        if (TheUser.getAuthorizedUser(this.getActivity()) == null) {
            (getView().findViewById(R.id.new_comment_field)).setVisibility(View.GONE);
        }

        bottomDetectScrollView = (BottomDetectScrollView) getView().findViewById(R.id.comments_scrollView);
        bottomDetectScrollView.bottomDetectedCallback = new BottomDetected();
        bottomDetectScrollView.bottomDetectedCallback.callback();

        (new ArticleCommentsGETAsyncTask(this)).execute(mArticleId, "0");

    }

    private View getCommentView(TheComment comment) {
        RelativeLayout commentLayout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.fragment_comment, rootLayout, false);

        ((TextView) commentLayout.findViewById(R.id.body)).setText(comment.body);
        ((TextView) commentLayout.findViewById(R.id.user_name)).setText(AT + comment.authorNickname);

        ImageView deleteImageView = (ImageView) commentLayout.findViewById(R.id.delete_imageview);

        if (TheUser.getAuthorizedUser(activity) != null && comment.authorNickname.equals(TheUser.getAuthorizedUser(activity).nickname)) {
            deleteImageView.setOnClickListener(new OnDeleteImageClicked(comment.comment_id));
        } else deleteImageView.setVisibility(View.GONE);

        return commentLayout;

    }

    public void updateView(TheComment[] comments){

        rootLayout = (LinearLayout) getView().findViewById(R.id.fragment_feed_comment_layout);
        rootLayout.removeAllViews();

        if (comments == null || comments.length == 0) {
            (getView().findViewById(R.id.no_comments)).setVisibility(View.VISIBLE);
            (getView().findViewById(R.id.comments_scrollView)).setVisibility(View.INVISIBLE);
        }
        else {
            (getView().findViewById(R.id.no_comments)).setVisibility(View.GONE);
            (getView().findViewById(R.id.comments_scrollView)).setVisibility(View.VISIBLE);
        }

        for (TheComment comment : comments){
            rootLayout.addView(getCommentView(comment));
        }
    }

    public void addViews(TheComment[] comments)
    {
        rootLayout = (LinearLayout) getView().findViewById(R.id.fragment_feed_comment_layout);

        for (int i = 0; i < comments.length; i++){
            rootLayout.addView(getCommentView(comments[i]));
        }
    }

    private class OnSendImageClicked implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            EditText commentMessageEditText = (EditText) getView().findViewById(R.id.comment_message);
            String commentMessage = commentMessageEditText.getText().toString();

            if(!commentMessage.isEmpty()){

                TheComment comment = new TheComment();

                comment.body = commentMessage;
                comment.article_id = mArticleId;

                commentMessageEditText.setText("");
                (new ArticleCommentsPOSTAsyncTask(CommentFragment.this)).execute(comment);

                //hide keyboard
                View view = activity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }

    private class OnDeleteImageClicked implements View.OnClickListener {

        private String comment_id;

        public OnDeleteImageClicked(String comment_id){
            this.comment_id = comment_id;
        }

        @Override
        public void onClick(View v) {

            TheComment comment = new TheComment();
            comment.article_id = CommentFragment.this.mArticleId;
            comment.comment_id = comment_id;

            (new ArticleCommentDELETEAsyncTask(CommentFragment.this)).execute(comment);

        }
    }

    private class BottomDetected implements BottomDetectScrollView.BottomDetectedCallback {

        @Override
        public void callback() {
            new ArticleCommentsGETAsyncTask(CommentFragment.this).execute(mArticleId, Integer.toString(rootLayout.getChildCount()));
        }
    }

}
