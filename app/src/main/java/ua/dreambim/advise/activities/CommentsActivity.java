package ua.dreambim.advise.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import ua.dreambim.advise.R;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.fragments.ArticleFragment;
import ua.dreambim.advise.fragments.CommentFragment;


public class CommentsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
    }


    @Override
    protected void onStart() {
        super.onStart();

        CommentFragment commentFragment = new CommentFragment();

        String articleId = getIntent().getStringExtra(TheArticle.KEY_article_id);

        commentFragment.setParameters(this, articleId);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.fragment_comment_frame,commentFragment);
        ft.commit();

    }

    public void onBackPressed(){
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item){

        finish();

        return true;
    }
}
