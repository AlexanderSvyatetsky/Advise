package ua.dreambim.advise.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ua.dreambim.advise.R;
import ua.dreambim.advise.activities.AdviseActivity;
import ua.dreambim.advise.activities.CommentsActivity;
import ua.dreambim.advise.activities.UserProfileActivity;
import ua.dreambim.advise.custom_views.FlowLayout;
import ua.dreambim.advise.custom_views.RoundedImageView;
import ua.dreambim.advise.dialog_fragments.DeleteArticleDialogFragment;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.network.asynctasks.ArticleDELETEAsyncTask;
import ua.dreambim.advise.network.asynctasks.ArticleGETAsyncTask;
import ua.dreambim.advise.network.asynctasks.ArticleLikeDELETEAsyncTask;
import ua.dreambim.advise.network.asynctasks.ArticleLikePOSTAsyncTask;
import ua.dreambim.advise.onclicklisteners.OnTagClickListener;

public class ArticleFragment extends Fragment {

    private TextView mLikeCountTextView;
    private ImageView mLikesImageView;

    private TheArticle article;
    private TheUser user;

    private AdviseActivity activity;

    public void setParameters(AdviseActivity activity)
    {
        this.activity = activity;
    }

    public void setEntities(TheArticle article ,TheUser user){
        this.article = article;
        this.user = user;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_article, container, false);

    }


    private String article_id;
    public void setArticleId(String article_id){
        this.article_id = article_id;
    }

    @Override
    public void onStart() {
        super.onStart();

        (new ArticleGETAsyncTask(this)).execute(article_id);
    }

    /**
     * Initialize all views, set values
     */

    public void getFullArticleView() {

        (getView().findViewById(R.id.top_info)).setOnClickListener(new OnTopInfoClicked());

        RoundedImageView avatarImageView = (RoundedImageView) getView().findViewById(R.id.avatar_imageview);

        if(user.avatarBitmap == null){
            avatarImageView.setImageResource(R.drawable.default_avatar);
        } else {
            avatarImageView.setImageBitmap(user.avatarBitmap);
        }

        ((TextView) getView().findViewById(R.id.user_name)).setText("@" + article.authorNickname);

        ((TextView) getView().findViewById(R.id.date_time)).setText(article.date + "  " + article.time);

        ((TextView) getView().findViewById(R.id.title)).setText(article.title);

        ((TextView) getView().findViewById(R.id.body)).setText(article.body);

        ((TextView) getView().findViewById(R.id.down_text)).setText(String.valueOf(article.commentsNumber));

        mLikesImageView = (ImageView)  getView().findViewById(R.id.likes_imageview);
        mLikesImageView.setOnClickListener(new OnLikeImageClicked());

        (getView().findViewById(R.id.comments_imageview)).setOnClickListener(new OnCommentImageClicked());

        mLikeCountTextView = (TextView) getView().findViewById(R.id.likes_number);

        mLikeCountTextView.setText(String.valueOf(article.likesNumber));

        if (article.liked)
            mLikesImageView.setImageResource(R.drawable.ic_thumb_up_green_48dp);
        else
            mLikesImageView.setImageResource(R.drawable.ic_thumb_up_black_48dp);

        addTags();

        if (TheUser.getAuthorizedUser(this.getActivity()) != null && article.authorNickname.equals(TheUser.getAuthorizedUser(getActivity()).nickname)) {
            (getView().findViewById(R.id.delete_button)).setVisibility(View.VISIBLE);
            (getView().findViewById(R.id.delete_button)).setOnClickListener(new OnDeleteClickListener());
        }
        else
            (getView().findViewById(R.id.delete_button)).setVisibility(View.GONE);

    }

    /**
     * add mandatory and custom tags (TextViews) to parent LinearLayout
     */

    private void addTags() {

        FlowLayout flowLayout = (FlowLayout) getView().findViewById(R.id.tags_layout);
        flowLayout.removeAllViews();

        List<String> mandatoryTags = Arrays.asList(activity.getResources().
                getStringArray(R.array.strings_mandatory_tags_array)) ;

        for (String tag : article.tags) {

            if(mandatoryTags.contains(tag)){
                LinearLayout tagLinearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tag_mandatory, null);
                TextView tagTextView = (TextView) tagLinearLayout.findViewById(R.id.tag);
                tagTextView.setText(tag);
                tagLinearLayout.setOnClickListener(new OnTagClickListener(activity,tag));
                flowLayout.addView(tagLinearLayout, 0);

            } else {

                LinearLayout tagLinearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tag_custom, null);
                TextView tagTextView = (TextView) tagLinearLayout.findViewById(R.id.tag);
                tagTextView.setText(tag);
                tagLinearLayout.setOnClickListener(new OnTagClickListener(activity,tag));
                flowLayout.addView(tagLinearLayout);
            }

        }

    }

    public void showSnackbar(String text){
        if (text == null)
            return;
        Snackbar.make(getView(), text, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

    private class OnTopInfoClicked implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            UserProfileActivity.setUserNickname(article.authorNickname);

            Intent intent = new Intent(ArticleFragment.this.getActivity(), UserProfileActivity.class);
            ArticleFragment.this.getActivity().startActivity(intent);

            ArticleFragment.this.getActivity().overridePendingTransition(R.anim.up_in, R.anim.up_out);
        }
    }

    private class OnCommentImageClicked implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(activity, CommentsActivity.class);
            intent.putExtra(TheArticle.KEY_article_id, article_id);
            activity.startActivity(intent);
        }
    }

    private class OnLikeImageClicked implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(TheUser.getAuthorizedUser(activity) != null){
                if (article.liked)
                    (new ArticleLikeDELETEAsyncTask(ArticleFragment.this)).execute(article_id);
                else
                    (new ArticleLikePOSTAsyncTask(ArticleFragment.this)).execute(article_id);
            }
        }

    }

    private class OnDeleteClickListener implements View.OnClickListener{
        public void onClick(View view){
            DeleteArticleDialogFragment deleteArticleDialogFragment = new DeleteArticleDialogFragment();

            deleteArticleDialogFragment.setActivity(ArticleFragment.this.getActivity());

            deleteArticleDialogFragment.callbackInterface = new DeleteArticleDialogFragment.CallbackInterface() {
                @Override
                public void callback() {

                    (new ArticleDELETEAsyncTask(activity)).execute(new String[]{article_id});
                }
            };

            deleteArticleDialogFragment.show(ArticleFragment.this.getActivity().getFragmentManager(), "delete confirm");
        }
    }

}
