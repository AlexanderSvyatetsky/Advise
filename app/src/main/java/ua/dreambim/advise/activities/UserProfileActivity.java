package ua.dreambim.advise.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import ua.dreambim.advise.R;
import ua.dreambim.advise.dialog_fragments.DeleteArticleDialogFragment;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.fragments.FeedFragment;
import ua.dreambim.advise.network.asynctasks.ArticleDELETEAsyncTask;
import ua.dreambim.advise.network.asynctasks.UserArticlesLikedGETAsyncTask;
import ua.dreambim.advise.network.asynctasks.UserAvatarUploadPOSTAsyncTask;
import ua.dreambim.advise.network.asynctasks.UserGETAsyncTask;
import ua.dreambim.advise.network.asynctasks.UsersArticlesGETAsyncTask;

/**
 * Created by MykhailoIvanov on 12/8/2016.
 */
public class UserProfileActivity extends AppCompatActivity {

    private static String user_nickname;

    public static String get_user_nickname()
    {
        return user_nickname;
    }

    public static void setUserNickname(String nickname){
        user_nickname = nickname;
    }


    private final int PICK_IMAGE_CODE = 1;
    private List<String> mandatoryTags;

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

        setContentView(R.layout.activity_userprofile);

        ImageView topBackgroundImageView = (ImageView) findViewById(R.id.top_background_imageview);
        int height =
                (int) (getResources().getDimension(R.dimen.activity_userprofile_avatar_margin_top) + getResources().getDimension(R.dimen.activity_userprofile_avatar_size) / 2);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        topBackgroundImageView.setLayoutParams(params);

        mandatoryTags = (List) Arrays.asList(getResources().getStringArray(R.array.strings_mandatory_tags_array));

    }

    public void onStart()
    {
        super.onStart();

        (new UserGETAsyncTask(this)).execute(user_nickname);
        (new UsersArticlesGETAsyncTask(this)).execute(user_nickname);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        TheUser authUser = TheUser.getAuthorizedUser(this);
        if ((authUser != null) && (authUser.nickname.equals(user_nickname)))
            getMenuInflater().inflate(R.menu.toolbar_activity_userprofile_authuser, menu);
        else
            getMenuInflater().inflate(R.menu.toolbar_activity_userprofile_empty, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){

        if (menuItem.getItemId() == R.id.activity_userprofile_logout_action){
            TheUser.removeAuthorizedUser(this);

            Intent intent = new Intent(getApplicationContext(), SignActivity.class);
            finish();
            startActivity(intent);

            return true;
        }

        finish();
        overridePendingTransition(R.anim.down_in, R.anim.down_out);
        return true;
    }

    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.down_in, R.anim.down_out);
    }

    public void setHead(TheUser user)
    {
        if (user == null)
            return;

        ImageView avatarImageView = (ImageView) findViewById(R.id.avatar_imageview);
        if (user.avatarBitmap != null)
            avatarImageView.setImageBitmap(user.avatarBitmap);
        else
            avatarImageView.setImageResource(R.drawable.default_avatar);

        TheUser authUser = TheUser.getAuthorizedUser(this);
        if ((authUser != null) && (authUser.nickname.equals(user_nickname)))
            avatarImageView.setOnClickListener(new OnAvatarClickListener());
        else
            avatarImageView.setOnClickListener(null);

        TextView nicknameTextView = (TextView) findViewById(R.id.nickname_textview);
        nicknameTextView.setText("@" + user.nickname);

        TextView articlesLikesNumberTextView = (TextView) findViewById(R.id.articleslikes_textview);
        articlesLikesNumberTextView.setText(Integer.toString(user.articlesLikes));

        ((TextView) findViewById(R.id.articlesliked_textview)).setText(Integer.toString(user.articlesLiked));

        TextView articlesTotalTextView = (TextView) findViewById(R.id.articlestotal_textview);
        articlesTotalTextView.setText(Integer.toString(user.articlesTotal));

        findViewById(R.id.likednumber_layout).setOnClickListener(new OnLikedLayoutClickListener());
        findViewById(R.id.articlesnumber_layout).setOnClickListener(new OnArticlesLayoutClickListener());
    }

    public void setArticlesView(TheArticle[] articles){

        LinearLayout articlesLayout = (LinearLayout) findViewById(R.id.articles_layout);
        articlesLayout.removeAllViews();
        for (int i = 0; i < articles.length; i++)
            articlesLayout.addView(getFormedArticleView(articlesLayout, articles[i]));

    }

    private View getFormedArticleView(ViewGroup parent, TheArticle article)
    {
        ViewGroup articleViewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.fragment_feed_article, parent, false);

        ((TextView) articleViewGroup.findViewById(R.id.title)).setText(article.title);
        ((TextView) articleViewGroup.findViewById(R.id.body)).setText(FeedFragment.getValidLengthArticleBodyPreview(article.body));

        ((TextView) articleViewGroup.findViewById(R.id.down_text)).setText(Integer.toString(article.commentsNumber));
        ((TextView) articleViewGroup.findViewById(R.id.likes_number)).setText(Integer.toString(article.likesNumber));

        TheUser authorizedUser = TheUser.getAuthorizedUser(this);

        if ((authorizedUser != null) && (user_nickname.equals(authorizedUser.nickname))){
            ImageView deleteButton = new ImageView(getApplicationContext());
            deleteButton.setImageResource(R.drawable.ic_delete_black_48dp);

            int size = (int) getResources().getDimension(R.dimen.activity_userprofile_ok_button_size);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            int right = (int) getResources().getDimension(R.dimen.fragment_feed_article_margin_left_and_right);
            int top = (int) getResources().getDimension(R.dimen.fragment_feed_article_margin_top_and_bottom);
            params.setMargins(0,top, right, 0);
            deleteButton.setLayoutParams(params);

            deleteButton.setOnClickListener(new OnDeleteArticleClickListener(article.article_id));
            articleViewGroup.addView(deleteButton);
        }

        articleViewGroup.setOnClickListener(new OnArticlePreviewClicked(article.article_id));
        return (View) articleViewGroup;
    }

    private class OnAvatarClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select avatar"), PICK_IMAGE_CODE);
        }
    }

    protected void onActivityResult(int requestCode, int result, Intent data){
        if ((requestCode == PICK_IMAGE_CODE) && (result == Activity.RESULT_OK) && (data != null) && (data.getData() != null)) {
            try {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());

                byte[] imageBytes = getByteArray(inputStream);
                (new UserAvatarUploadPOSTAsyncTask(this, imageBytes)).execute();

            } catch (Exception e) {
                return;
            }
        }
    }

    private byte[] getByteArray(InputStream inputStream) throws Exception{
        ByteArrayOutputStream byteByffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int length = 0;
        while ((length = inputStream.read(buffer)) != -1){
            byteByffer.write(buffer, 0, length);
        }

        return byteByffer.toByteArray();
    }

    private class OnArticlePreviewClicked implements View.OnClickListener{

        private String article_id;

        public OnArticlePreviewClicked(String article_id){
            this.article_id = article_id;
        }

        @Override
        public void onClick(View view) {

            AdviseActivity.openArticle = true;
            AdviseActivity.openArticle_article_id = article_id;

            UserProfileActivity.this.finish();
        }
    }

    private class OnDeleteArticleClickListener implements View.OnClickListener{

        private String article_id;

        public OnDeleteArticleClickListener(String article_id){
            this.article_id = article_id;
        }

        public void onClick(View view) {
            DeleteArticleDialogFragment deleteArticleDialogFragment = new DeleteArticleDialogFragment();

            deleteArticleDialogFragment.setActivity(UserProfileActivity.this);

            deleteArticleDialogFragment.callbackInterface = new DeleteArticleDialogFragment.CallbackInterface() {
                @Override
                public void callback() {

                    (new ArticleDELETEAsyncTask(UserProfileActivity.this)).execute(new String[]{article_id});
                }
            };

            deleteArticleDialogFragment.show(UserProfileActivity.this.getFragmentManager(), "delete confirm");
        }

    }

    private class OnArticlesLayoutClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            (new UsersArticlesGETAsyncTask(UserProfileActivity.this)).execute(user_nickname);
        }
    }

    private class OnLikedLayoutClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            (new UserArticlesLikedGETAsyncTask(UserProfileActivity.this)).execute(user_nickname);
        }
    }

}
