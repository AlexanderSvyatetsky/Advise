package ua.dreambim.advise.activities;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ua.dreambim.advise.R;
import ua.dreambim.advise.custom_views.FlowLayout;
import ua.dreambim.advise.custom_views.TagsAutoCompleteTextView;
import ua.dreambim.advise.dialog_fragments.BackFromCreateArticleDialogFragment;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.network.asynctasks.ArticlePOSTAsyncTask;

public class CreateArticleActivity extends AppCompatActivity {

    private static final String TAG = "CreateArticleActivity.java";
    private ScrollView scrollView;
    private EditText textEnterEditText;
    private EditText articleNameEditText;
    private TagsAutoCompleteTextView tagsEditText;
    TagsAutoCompleteAdapter adapter;
    private ArrayList<TextView> hiddenViews = new ArrayList<>();
    private String[] mandatoryTags;
    private String[] customTags;
    private FlowLayout topTagsLayout;
    private FlowLayout mandatoryTagsLayout;

    private static final List<String> tags = new ArrayList<>(Arrays.asList(
            "customtag1", "customtag2", "customtag3"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_article);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mandatoryTagsLayout = (FlowLayout) findViewById(R.id.activity_create_article_mandatory_tags_layout);
        uploadMandatoryTags(mandatoryTagsLayout);

        topTagsLayout = (FlowLayout) findViewById(R.id.activity_create_article_top_tags_layout);
        uploadCustomTags(topTagsLayout);

        articleNameEditText = (EditText) findViewById(R.id.activity_create_article_article_name_input_view_id);
        textEnterEditText = (EditText) findViewById(R.id.activity_create_article_article_text_input_view_id);
        tagsEditText = (TagsAutoCompleteTextView)
                findViewById(R.id.activity_create_article_article_tags_input_id);

        adapter = new TagsAutoCompleteAdapter(this,
                android.R.layout.simple_dropdown_item_1line, tags);

        tagsEditText.setMandatroyTags(mandatoryTags);
        tagsEditText.setAdapter(adapter);
        tagsEditText.setOnTagDeletedListener(onTagEventListener);


        scrollView = (ScrollView) findViewById(R.id.rootScrollViewId);

    }

    private TheArticle getArticle() {
        TheArticle article = new TheArticle();

        article.title = articleNameEditText.getText().toString();
        article.body = textEnterEditText.getText().toString();
        article.authorNickname = TheUser.getAuthorizedUser(this).nickname;

        article.tags = tagsEditText.getTags();

        return article;
    }

    private void uploadCustomTags(FlowLayout layout) {
        if (TheArticle.tagsForAutocompleteText == null) {
            return;
        }

        for (int i = 0; i < TheArticle.tagsForAutocompleteText.length; i++) {
            LinearLayout tagLinearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.tag_custom, topTagsLayout, false);
            TextView tagTextView = (TextView) tagLinearLayout.findViewById(R.id.tag);
            tagTextView.setText(TheArticle.tagsForAutocompleteText[i]);
            tags.add(TheArticle.tagsForAutocompleteText[i]);
            tagTextView.setOnClickListener(tagsOnClickListener);
            layout.addView(tagLinearLayout);
        }
    }

    private void uploadMandatoryTags(FlowLayout layout) {

        mandatoryTags = getResources().getStringArray(R.array.strings_mandatory_tags_array);
        for (int i = 0; i < mandatoryTags.length; i++) {
            LinearLayout tagLinearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.tag_mandatory, mandatoryTagsLayout, false);
            TextView tagTextView = (TextView) tagLinearLayout.findViewById(R.id.tag);
            tagTextView.setText(mandatoryTags[i]);
            tags.add(mandatoryTags[i]);
            tagTextView.setOnClickListener(tagsOnClickListener);
            layout.addView(tagLinearLayout);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_article_action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save)
            (new ArticlePOSTAsyncTask(this)).execute(getArticle());
        else{
            startDialogFragment();
        }


        return true;
    }

    private void startDialogFragment()
    {
        BackFromCreateArticleDialogFragment dialogFragment = new BackFromCreateArticleDialogFragment();
        dialogFragment.setActivity(this);
        dialogFragment.show(this.getFragmentManager(), "confirm");
    }

    public void onBackPressed(){
        startDialogFragment();
    }

    View.OnClickListener tagsOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tagString = ((TextView) v).getText().toString();
            tagsEditText.append(tagString + ",");

        }
    };
    TagsAutoCompleteTextView.OnTagEventListener onTagEventListener = new TagsAutoCompleteTextView.OnTagEventListener() {
        @Override
        public void deleted(String tagText) {
            for (TextView textView : hiddenViews) {
                if (tagText.equals(textView.getText().toString())) {
                    textView.setVisibility(View.VISIBLE);

                    break;
                }
            }
            adapter.add(tagText);
        }

        @Override
        public void added(String text) {
            TextView tv;
            boolean continueSearch = true;
            for (int i = 0; i < mandatoryTagsLayout.getChildCount(); i++) {
                tv = (TextView) ((LinearLayout) mandatoryTagsLayout.getChildAt(i)).getChildAt(0);
                if (text.equals(tv.getText().toString())) {
                    tv.setVisibility(View.GONE);
                    hiddenViews.add(tv);
                    continueSearch = false;
                    break;
                }
            }
            if (continueSearch) {
                for (int i = 0; i < topTagsLayout.getChildCount(); i++) {
                    tv = (TextView) ((LinearLayout) topTagsLayout.getChildAt(i)).getChildAt(0);
                    if (text.equals(tv.getText().toString())) {
                        tv.setVisibility(View.GONE);
                        hiddenViews.add(tv);
                        break;
                    }
                }
            }
            adapter.remove(text);
        }
    };

    public void showSnackbar(String text) {
        if (text == null)
            return;
        Snackbar.make(findViewById(R.id.activity_create_article_layout_id), text, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

    class TagsAutoCompleteAdapter extends ArrayAdapter<String> {

        private final int MANDATORY_TAG_COLOR = getResources().getColor(R.color.colorMandatoryTagBackground);
        private final int CUSTOM_TAG_COLOR = getResources().getColor(R.color.colorCustomTagBackground);

        public TagsAutoCompleteAdapter(Context context, int resource) {
            super(context, resource);
        }

        public TagsAutoCompleteAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        public TagsAutoCompleteAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
        }

        public TagsAutoCompleteAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public TagsAutoCompleteAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        public TagsAutoCompleteAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) super.getView(position, convertView, parent);
            String tagString = textView.getText().toString();
            boolean mandatory = false;
            for (int i = 0; i < mandatoryTags.length; i++) {
                if (tagString.equals(mandatoryTags[i])) {
                    textView.setTextColor(MANDATORY_TAG_COLOR);
                    mandatory = true;
                    break;
                }
            }
            if (!mandatory) {
                textView.setTextColor(CUSTOM_TAG_COLOR);
            }
            return textView;

        }
    }

    ;

}
