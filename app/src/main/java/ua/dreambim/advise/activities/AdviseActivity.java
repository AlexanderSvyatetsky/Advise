package ua.dreambim.advise.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import ua.dreambim.advise.R;
import ua.dreambim.advise.dialog_fragments.PickLanguageDialogFragment;
import ua.dreambim.advise.entities.TheArticle;
import ua.dreambim.advise.entities.TheUser;
import ua.dreambim.advise.fragments.AboutFragment;
import ua.dreambim.advise.fragments.ArticleFragment;
import ua.dreambim.advise.fragments.FeedFragment;
import ua.dreambim.advise.fragments.NotificationFragment;
import ua.dreambim.advise.fragments.SearchFragment;
import ua.dreambim.advise.network.Host;
import ua.dreambim.advise.network.asynctasks.NotificationsCheckGETAsyncTask;
import ua.dreambim.advise.network.asynctasks.SignInPOSTAsyncTask;
import ua.dreambim.advise.network.asynctasks.TopTagsGETAsyncTask;

public class AdviseActivity extends AppCompatActivity{

    // LANGUAGE
    public static final String KEY_LANGUAGE = "language";
    public static final int LANGUAGE_ENGLISH = 0;
    public static final int LANGUAGE_RUSSIAN = 1;

    public static int getLanguage(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TheUser.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getInt(KEY_LANGUAGE, 0);
    }

    public static String getLanguageCode(int language){
        switch (language){
            case LANGUAGE_ENGLISH:
                return "en";
            case LANGUAGE_RUSSIAN:
                return "ru";
            default:
                return "en";
        }
    }

    public static void setLanguage(Activity activity, int language){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TheUser.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_LANGUAGE, language);

        editor.commit();
    }
    //

    // fragments
    private FeedFragment bestFeedFragment;
    private FeedFragment newFeedFragment;
    private FeedFragment mostDiscussedFeedFragment;
    private FeedFragment searchFeedFragment;
    private SearchFragment searchFragment;
    private ArticleFragment articleFragment;
    private NotificationFragment notificationFragment;
    private AboutFragment aboutFragment;
    //

    private NavigationListener navigationListener;
    private CustomDrawerListener drawerListener;
    private SearchViewListener searchViewListener;
    private SearchViewClickListener searchViewClickListener;
    private OnNavigationHeaderClickListener onNavigationHeaderClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advise);

        // toolbar preparations
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchViewListener = new SearchViewListener();
        searchViewClickListener = new SearchViewClickListener();
        //

        //
        openArticle = false;
        //

        // language setting
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        android.content.res.Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(getLanguageCode(getLanguage(this)));
        resources.updateConfiguration(configuration, displayMetrics);
        //


        // navigation view
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        drawerListener = new CustomDrawerListener();
        drawer.addDrawerListener(drawerListener);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationListener = new NavigationListener();
        navigationView.setNavigationItemSelectedListener(navigationListener);

        onNavigationHeaderClickListener = new OnNavigationHeaderClickListener();
        //

        // fragments
        bestFeedFragment = new FeedFragment();
        bestFeedFragment.setParameters(this);
        bestFeedFragment.setFeed_type(FeedFragment.TYPE_BEST);

        newFeedFragment = new FeedFragment();
        newFeedFragment.setParameters(this);
        newFeedFragment.setFeed_type(FeedFragment.TYPE_NEW);

        mostDiscussedFeedFragment = new FeedFragment();
        mostDiscussedFeedFragment.setParameters(this);
        mostDiscussedFeedFragment.setFeed_type(FeedFragment.TYPE_MOST_DISCUSSED);

        searchFeedFragment = new FeedFragment();
        searchFeedFragment.setParameters(this);
        searchFeedFragment.setFeed_type(FeedFragment.TYPE_BY_TAGS);

        searchFragment = new SearchFragment();
        searchFragment.setParameters(this);

        articleFragment = new ArticleFragment();
        articleFragment.setParameters(this);

        notificationFragment = new NotificationFragment();
        notificationFragment.setParameters(this);

        aboutFragment = new AboutFragment();
        //

        // activity starts from the new feed
        setNewFeedFragment();

        // update token
        if (savedInstanceState == null)
            (new SignInPOSTAsyncTask(this)).execute(TheUser.getAuthorizedUser(this));
        //
    }

    //

    public static boolean openArticle;
    public static String openArticle_article_id;

    protected void onStart()
    {
        super.onStart();

        TheUser.getAuthorizedUser(this);
        Host.getToken(this);

        if (openArticle){
            setArticleFragment(openArticle_article_id);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(Gravity.LEFT);
        }

        openArticle = false;
    }
    //


    protected void onRestart(){
        super.onRestart();

        // updating navigationbar
        updateNavigationBar();
    }

    protected void onResume(){
        super.onResume();

        //downloading the tags for autocomplete
        (new TopTagsGETAsyncTask(this)).execute(getLanguage(this), TheArticle.TAGS_FOR_AUTOCOMPLETE_LIMIT);
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        //

        RelativeLayout navigationHeaderLayout = (RelativeLayout) findViewById(R.id.activity_advise_header_layout);
        navigationHeaderLayout.setOnClickListener(onNavigationHeaderClickListener);
        // updating navigationbar
        updateNavigationBar();

        //

        getMenuInflater().inflate(R.menu.toolbar_activity_advise, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.activity_advise_search).getActionView();
        searchView.setOnQueryTextListener(searchViewListener);
        searchView.setOnSearchClickListener(searchViewClickListener);
        searchView.setQueryHint(this.getResources().getString(R.string.activity_advise_toolbar_search));

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem){
        return super.onOptionsItemSelected(menuItem);
    }

    public void setNewFeedFragment()
    {
        if (newFeedFragment.isInLayout())
            newFeedFragment.showArticles(FeedFragment.TYPE_NEW, getLanguage(this), 0, FeedFragment.MAX_ARTICLES_TO_SHOW);
        else{
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_content_frame, newFeedFragment);
            fragmentTransaction.commit();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

    }

    public void setBestFeedFragment()
    {
        if (bestFeedFragment.isInLayout())
            bestFeedFragment.showArticles(FeedFragment.TYPE_BEST, getLanguage(this), 0, FeedFragment.MAX_ARTICLES_TO_SHOW);
        else{
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_content_frame, bestFeedFragment);
            fragmentTransaction.commit();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);

    }

    public void setMostDiscussedFeedFragment()
    {
        if (mostDiscussedFeedFragment.isInLayout())
            mostDiscussedFeedFragment.showArticles(FeedFragment.TYPE_MOST_DISCUSSED, getLanguage(this), 0, FeedFragment.MAX_ARTICLES_TO_SHOW);
        else{
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_content_frame, mostDiscussedFeedFragment);
            fragmentTransaction.commit();
        }


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);

    }

    public void setSearchFeedFragment(String[] tags)
    {
        searchFeedFragment.setTagsArray(tags);

        if (searchFeedFragment.isResumed())
            searchFeedFragment.showArticles(tags, getLanguage(this), 0, FeedFragment.MAX_ARTICLES_TO_SHOW);
        else{
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_content_frame, searchFeedFragment);
            fragmentTransaction.commit();
        }
    }

    public void setSearchFragment()
    {
        if (searchFragment.isInLayout()){
            (new TopTagsGETAsyncTask(this, searchFragment)).execute(getLanguage(this), 100);
        }
        else{
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_content_frame, searchFragment);
            fragmentTransaction.commit();
        }
    }

    public void setArticleFragment(String article_id)
    {
        articleFragment.setArticleId(article_id);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content_frame, articleFragment);
        fragmentTransaction.commit();
    }

    public void setNotificationFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content_frame, notificationFragment);
        fragmentTransaction.commit();
    }

    public void setAboutFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content_frame, aboutFragment);
        fragmentTransaction.commit();
    }


    //
    public void showSnackbar(String text){
        if (text == null)
            return;
        Snackbar.make(findViewById(R.id.fragment_content_frame), text, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }
    //

    private void updateNavigationBar(){
        ImageView avatarImageView = (ImageView) findViewById(R.id.activity_advise_useravatar_imageview);
        TextView usernameTextView = (TextView) findViewById(R.id.activity_advise_username_textview);

        ImageView actionImageView = (ImageView) findViewById(R.id.activity_advise_action_imageview);

        if (TheUser.getAuthorizedUser(AdviseActivity.this) == null)
        {
            avatarImageView.setImageResource(R.drawable.default_avatar);
            usernameTextView.setText("");

            actionImageView.setImageResource(R.drawable.ic_person_add_white_48dp);
        }
        else{
            if (TheUser.getAuthorizedUser(AdviseActivity.this).avatarBitmap != null)
                avatarImageView.setImageBitmap(TheUser.getAuthorizedUser(AdviseActivity.this).avatarBitmap);
            else
                avatarImageView.setImageResource(R.drawable.default_avatar);
            usernameTextView.setText("@" + TheUser.getAuthorizedUser(AdviseActivity.this).nickname);

            actionImageView.setImageResource(R.drawable.ic_expand_more_white_48dp);
        }

        if (TheUser.getAuthorizedUser(AdviseActivity.this) != null)
            (new NotificationsCheckGETAsyncTask(AdviseActivity.this)).execute();

        ((NavigationView) this.findViewById(R.id.nav_view)).getMenu().clear();
        ((NavigationView) this.findViewById(R.id.nav_view)).inflateMenu(R.menu.navigation_menu);

    }


    private class NavigationListener  implements NavigationView.OnNavigationItemSelectedListener{

        private DrawerLayout drawer;

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.navigation_item_new)
                setNewFeedFragment();
            else if (id == R.id.navigation_item_best)
                setBestFeedFragment();
            else if (id == R.id.navigation_item_mostdiscussed)
                setMostDiscussedFeedFragment();
            else if (id == R.id.item_about){
                setAboutFragment();
            }
            else if (id == R.id.navigation_item_notifications){
                setNotificationFragment();
            }


            if (id == R.id.item_language){
                PickLanguageDialogFragment pickLanguageDialogFragment = new PickLanguageDialogFragment();
                pickLanguageDialogFragment.setActivity(AdviseActivity.this);
                pickLanguageDialogFragment.show(AdviseActivity.this.getFragmentManager(), "language picker");
            }
            else {
                drawer = (DrawerLayout) AdviseActivity.this.findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }

            return true;
        }
    }

    private class CustomDrawerListener implements DrawerLayout.DrawerListener
    {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {}

        @Override
        public void onDrawerOpened(View drawerView) {

            updateNavigationBar();
        }

        @Override
        public void onDrawerClosed(View drawerView) {}

        @Override
        public void onDrawerStateChanged(int newState) {
            SearchView searchView = (SearchView) findViewById(R.id.activity_advise_search);
            if (!searchView.isIconified())
                searchView.setIconified(true);
        }
    }

    private class SearchViewListener implements SearchView.OnQueryTextListener{

        @Override
        public boolean onQueryTextSubmit(String query) {

            SearchView searchView = (SearchView) findViewById(R.id.activity_advise_search);
            searchView.clearFocus();

            setSearchFeedFragment(query.split(" "));

            return true;
        }

        @Override
        public boolean onQueryTextChange(String query) {

            if ((query == null) || (query.length() == 0))
                setSearchFragment();
            else
                setSearchFeedFragment(query.split(" "));

            return true;
        }
    }

    public static boolean programmaticallySearchEnteredFlag = false;

    private class SearchViewClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            if (!programmaticallySearchEnteredFlag)
                setSearchFragment();

            programmaticallySearchEnteredFlag = false;
        }
    }

    private class OnNavigationHeaderClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (TheUser.getAuthorizedUser(AdviseActivity.this) == null)
            {
                Intent intent = new Intent(AdviseActivity.this, SignActivity.class);
                startActivity(intent);
            }
            else{
                UserProfileActivity.setUserNickname(TheUser.getAuthorizedUser(AdviseActivity.this).nickname);
                Intent intent = new Intent(AdviseActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }

            overridePendingTransition(R.anim.up_in, R.anim.up_out);
        }
    }
}
