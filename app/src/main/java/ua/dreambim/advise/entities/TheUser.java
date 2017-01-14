package ua.dreambim.advise.entities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import ua.dreambim.advise.network.Host;

/**
 * Created by MykhailoIvanov on 11/27/2016.
 */
public class TheUser{

    public static final String SHARED_PREFERENCES_NAME = "advise_prefs";

    public static final String KEY_email = "email";
    public static final String KEY_nickname = "nickname";
    public static final String KEY_password = "password";
    public static final String KEY_avatarURL = "avatarURL";
    public static final String KEY_articlesLikes = "articlesLikes";
    public static final String KEY_articlesTotal = "articlesTotal";
    public static final String KEY_articlesLiked = "articlesLiked";

    public String email;
    public String nickname;
    public String password; // used only in sign in / sign up menus

    public String avatarURL;
    public Bitmap avatarBitmap; // always check for null, and put default image in this case

    public int articlesTotal; // current user's articles number
    public int articlesLikes; /* total number of likes, given by other users to
                                current user's articles*/
    public int articlesLiked;



    // --------------------------

    private static TheUser authorizedUser;

    public static void saveAuthorizedUser(Activity activity, TheUser authorized){

        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_email, authorized.email);
        editor.putString(KEY_nickname, authorized.nickname);
        editor.putString(KEY_password, authorized.password);
        editor.putString(KEY_avatarURL, authorized.avatarURL);
        editor.putInt(KEY_articlesTotal, authorized.articlesTotal);
        editor.putInt(KEY_articlesLikes, authorized.articlesLikes);
        editor.putInt(KEY_articlesLiked, authorized.articlesLiked);

        editor.commit();

        authorizedUser = authorized;
    }

    public static TheUser getAuthorizedUser(Activity activity){

        if (authorizedUser != null)
            return authorizedUser;

        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        TheUser authorizedUser = new TheUser();
        authorizedUser.email = sharedPreferences.getString(KEY_email, null);
        authorizedUser.nickname = sharedPreferences.getString(KEY_nickname, null);
        if (authorizedUser.nickname == null){
            return null;
        }
        authorizedUser.password = sharedPreferences.getString(KEY_password, null);
        authorizedUser.avatarURL = sharedPreferences.getString(KEY_avatarURL, null);
        authorizedUser.articlesTotal = sharedPreferences.getInt(KEY_articlesTotal, 0);
        authorizedUser.articlesLikes = sharedPreferences.getInt(KEY_articlesLikes, 0);
        authorizedUser.articlesLiked = sharedPreferences.getInt(KEY_articlesLiked, 0);

        return authorizedUser;
    }

    public static void removeAuthorizedUser(Activity activity){

        SharedPreferences sharedPreferences = activity.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(TheUser.KEY_nickname);
        editor.remove(TheUser.KEY_password);
        editor.commit();

        authorizedUser = null;

        Host.removeToken(activity);
    }
}