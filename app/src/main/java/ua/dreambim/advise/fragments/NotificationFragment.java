package ua.dreambim.advise.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.AdviseActivity;
import ua.dreambim.advise.activities.SignActivity;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.entities.TheComment;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.network.asynctasks.NotificationsDELETEAsyncTask;
import ua.dreambim.advise.network.asynctasks.NotificationsGETAsyncTask;

public class NotificationFragment extends Fragment {


    private AdviseActivity activity;

    public RelativeLayout mNotificationLayout;
    public LinearLayout mNewArticleCommentNotificationLayout;
    public LinearLayout mNewCommentBelowNotificationLayout;

    public ProgressBar mNotificationProgressBar;

    public void setParameters(AdviseActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();

        if (TheUser.getAuthorizedUser(getActivity()) == null){
            Intent intent = new Intent(getActivity(), SignActivity.class);
            getActivity().startActivity(intent);
        }
        else{
            mNotificationLayout = (RelativeLayout) getView().findViewById(R.id.notifications_layout);
            mNotificationProgressBar = (ProgressBar) getView().findViewById(R.id.notification_progress_bar);

            mNewArticleCommentNotificationLayout = (LinearLayout) getView().findViewById(R.id.notifications_new_article_comments);

            mNewCommentBelowNotificationLayout = (LinearLayout) getView().findViewById(R.id.notifications_new_comments_below);

            (getActivity().findViewById(R.id.delete_button)).setOnClickListener(new DeleteClickListener());
            (new NotificationsGETAsyncTask(this)).execute();
        }



    }

    public void addNotificationNewArticleCommentsView(HashMap<String, Integer> commentsNum, HashMap<String, TheArticle> articles) {

        mNewArticleCommentNotificationLayout = (LinearLayout) getView().findViewById(R.id.notifications_new_article_comments);

        mNewArticleCommentNotificationLayout.removeAllViews();

        if (articles.keySet().size() == 0) {
            (getView().findViewById(R.id.notifications_new_article_comments)).setVisibility(View.GONE);
            (getView().findViewById(R.id.new_article_comments)).setVisibility(View.GONE);
        }
        else {
            (getView().findViewById(R.id.notifications_new_article_comments)).setVisibility(View.VISIBLE);
            (getView().findViewById(R.id.new_article_comments)).setVisibility(View.VISIBLE);
        }

        for (String article_id : articles.keySet())
        {
            TheArticle article = articles.get(article_id);

            ViewGroup layout = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_notification_item, null);

            ((TextView) layout.findViewById(R.id.article_name)).setText(article.title);
            ((TextView) layout.findViewById(R.id.down_text)).setText(Integer.toString(commentsNum.get(article.article_id)) + " " + getActivity().getResources().getString(R.string.fragment_notification_articles_comments) );

            layout.setOnClickListener(new OnNotificationsClickListener(article.article_id));
            mNewArticleCommentNotificationLayout.addView(layout);
        }
    }

    public void addNotificationNewCommentBelowView(TheComment[] comments) {

        mNewCommentBelowNotificationLayout = (LinearLayout) getView().findViewById(R.id.notifications_new_comments_below);

        mNewCommentBelowNotificationLayout.removeAllViews();

        if (comments == null || (comments.length == 0)){
            (getView().findViewById(R.id.new_comments_under_comments)).setVisibility(View.GONE);
            (getView().findViewById(R.id.notifications_new_comments_below)).setVisibility(View.GONE);
        }
        else{
            (getView().findViewById(R.id.new_comments_under_comments)).setVisibility(View.VISIBLE);
            (getView().findViewById(R.id.notifications_new_comments_below)).setVisibility(View.VISIBLE);

            for (int i = 0; i < comments.length; i++){
                ViewGroup layout = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_notification_item, null);

                ((TextView) layout.findViewById(R.id.down_text)).setText(comments[i].body);
                ((TextView) layout.findViewById(R.id.article_name)).setText(comments[i].authorNickname);

                layout.setOnClickListener(new OnNotificationsClickListener(comments[i].article_id));
                mNewCommentBelowNotificationLayout.addView(layout);
            }
        }


    }


    private class DeleteClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            (new NotificationsDELETEAsyncTask(NotificationFragment.this)).execute();
        }
    }

    private class OnNotificationsClickListener implements View.OnClickListener{

        private String article_id;

        public OnNotificationsClickListener(String article_id){
            this.article_id = article_id;
        }

        @Override
        public void onClick(View view) {
//            Intent intent = new Intent(NotificationFragment.this.getActivity(), CommentsActivity.class);
//            intent.putExtra(TheArticle.KEY_article_id, article_id);
//            NotificationFragment.this.getActivity().startActivity(intent);

            activity.setArticleFragment(article_id);
        }
    }

}
