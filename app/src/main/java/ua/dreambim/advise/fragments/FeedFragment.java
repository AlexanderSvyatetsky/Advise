package ua.dreambim.advise.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.AdviseActivity;
import ua.dreambim.advise.activities.CreateArticleActivity;
import ua.dreambim.advise.custom_views.BottomDetectScrollView;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.network.asynctasks.ArticlesByTagsGETAsyncTask;
import ua.dreambim.advise.network.asynctasks.ArticlesTopGETAsyncTask;
import ua.dreambim.advise.onclicklisteners.OnTagClickListener;

/**
 * Created by MykhailoIvanov on 11/19/2016.
 */
public class FeedFragment extends Fragment {

    public static final int TYPE_BEST = 0;
    public static final int TYPE_NEW = 1;
    public static final int TYPE_MOST_DISCUSSED = 2;
    public static final int TYPE_BY_TAGS = 3;


    public static final int VALID_PREVIEW_ARTICLE_BODY_LENGTH = 50;
    public static final int MAX_TAGS_TO_SHOW = 10;
    public static final int MAX_ARTICLES_TO_SHOW = 5;

    private AdviseActivity activity;

    private List<String> mandatoryTags;

    // put some stuff from AdviseActivity here
    public void setParameters(AdviseActivity activity)
    {
        this.activity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle bundle){
        mandatoryTags = (List) Arrays.asList(getActivity().getResources().getStringArray(R.array.strings_mandatory_tags_array));

        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    public void onStart(){
        super.onStart();

        FloatingActionButton floatingActionButton = (FloatingActionButton) getView().findViewById(R.id.fragment_feed_floatingbutton);
        if (TheUser.getAuthorizedUser(this.getActivity()) == null)
            floatingActionButton.hide();
        else{
            floatingActionButton.show();
            floatingActionButton.setOnClickListener(new OnFlyButtonClickListener());
        }

        // some preparations for linearlayout updating
        BottomDetectScrollView bottomDetectScrollView = (BottomDetectScrollView) getView().findViewById(R.id.fragment_feed_scrollview);
        bottomDetectScrollView.bottomDetectedCallback = new CallbackImplemetation();

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.fragment_feed_swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeUpListener());
        swipeRefreshLayout.setRefreshing(true);
        //

        if (feed_type != TYPE_BY_TAGS)
            showArticles(feed_type, AdviseActivity.getLanguage(getActivity()), 0, MAX_ARTICLES_TO_SHOW);
        else
            showArticles(tagsArray, AdviseActivity.getLanguage(getActivity()), 0, MAX_ARTICLES_TO_SHOW);
    }

    // this method will be called from the AsyncTask after downloading the needed info
    public void updateView(TheArticle[] articles, boolean removeCurrentChildViews)
    {
        LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.fragment_feed_content_layout);
        if (removeCurrentChildViews)
            linearLayout.removeAllViews();

        for (int i = 0; i < articles.length; i++)
            linearLayout.addView(getFormedArticleView(linearLayout, articles[i]));
    }


    private View getFormedArticleView(ViewGroup parent, TheArticle article)
    {
        ViewGroup articleViewGroup = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.fragment_feed_article, parent, false);

        ((TextView) articleViewGroup.findViewById(R.id.title)).setText(article.title);
        ((TextView) articleViewGroup.findViewById(R.id.body)).setText(getValidLengthArticleBodyPreview(article.body));

        ((TextView) articleViewGroup.findViewById(R.id.down_text)).setText(Integer.toString(article.commentsNumber));
        ((TextView) articleViewGroup.findViewById(R.id.likes_number)).setText(Integer.toString(article.likesNumber));

        LinearLayout tagsLinearLayout = (LinearLayout) articleViewGroup.findViewById(R.id.tags_linear_layout);
        for (int i = 0; i < Math.min(MAX_TAGS_TO_SHOW, article.tags.length); i++)
        {
            LinearLayout tagLinearLayout = null;
            if (mandatoryTags.contains(article.tags[i]))
                tagLinearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tag_mandatory, null);
            else
                tagLinearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tag_custom, null);

            TextView tagTextView = (TextView) tagLinearLayout.findViewById(R.id.tag);
            tagTextView.setText(article.tags[i]);
            tagLinearLayout.setOnClickListener(new OnTagClickListener(activity, article.tags[i]));

            tagsLinearLayout.addView(tagLinearLayout);
        }

        articleViewGroup.setOnClickListener(new OnArticlePreviewClicked(article.article_id));
        return (View) articleViewGroup;
    }


    //
    private int feed_type;
    public void setFeed_type(int feed_type){
        this.feed_type = feed_type;
    }

    private String[] tagsArray; // for case if feed_type == BY_TAGS
    public void setTagsArray(String[] tags)
    {
        tagsArray = tags;
    }
    //

    public void showArticles(int feed_type, int language, int offset, int limit)
    {
        setFeed_type(feed_type);
        setTagsArray(null);
        (new ArticlesTopGETAsyncTask(this, offset)).execute(Integer.toString(feed_type), Integer.toString(language), Integer.toString(offset), Integer.toString(limit));
    }

    public void showArticles(String[] tags, int language, int offset, int limit)
    {
        setFeed_type(TYPE_BY_TAGS);
        setTagsArray(tags);

        String[] out = new String[tags.length + 3];
        out[0] = Integer.toString(language);
        out[1] = Integer.toString(offset);
        out[2] = Integer.toString(limit);
        for (int i = 0; i < tags.length; i++)
            out[3 + i] = tags[i];

        (new ArticlesByTagsGETAsyncTask(this)).execute(out);
    }

    public void showSnackbar(String text){
        if (text == null)
            return;
        Snackbar.make(getView(), text, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

    public static String getValidLengthArticleBodyPreview(String body){

        body = body.replace('\n', ' ');

        if (body.length() <= VALID_PREVIEW_ARTICLE_BODY_LENGTH)
            return body;
        else
            return new String(body.substring(0, VALID_PREVIEW_ARTICLE_BODY_LENGTH) + "...");
    }

    private class OnArticlePreviewClicked implements View.OnClickListener{

        private String article_id;

        public OnArticlePreviewClicked(String article_id){
            this.article_id = article_id;
        }

        @Override
        public void onClick(View view) {

            activity.setArticleFragment(article_id);
        }
    }

    private class OnFlyButtonClickListener implements View.OnClickListener{

        public void onClick(View v)
        {
            Intent intent = new Intent(activity, CreateArticleActivity.class);
            activity.startActivity(intent);
        }
    }

    private class CallbackImplemetation implements BottomDetectScrollView.BottomDetectedCallback{

        @Override
        public void callback() {
            int offset = ((LinearLayout) getView().findViewById(R.id.fragment_feed_content_layout)).getChildCount();
            (new ArticlesTopGETAsyncTask(FeedFragment.this, offset)).execute(Integer.toString(FeedFragment.this.feed_type),
                    Integer.toString(AdviseActivity.getLanguage(getActivity())),
                    Integer.toString(offset),
                    Integer.toString(MAX_ARTICLES_TO_SHOW));
        }
    }

    private class SwipeUpListener implements SwipeRefreshLayout.OnRefreshListener{

        @Override
        public void onRefresh() {
            if (feed_type != TYPE_BY_TAGS)
                showArticles(feed_type, AdviseActivity.getLanguage(getActivity()), 0, MAX_ARTICLES_TO_SHOW);
            else
                showArticles(tagsArray, AdviseActivity.getLanguage(getActivity()), 0, MAX_ARTICLES_TO_SHOW);
        }
    }
}
