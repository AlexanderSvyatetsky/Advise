package ua.dreambim.advise.network;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.net.URL;

import ua.dreambim.advise.entities.TheUser;

/**
 * Created by MykhailoIvanov on 12/10/2016.
 */
public class Host {
    public static final String KEY_TOKEN = "token";
    public static final String KEY_EXPIRES_IN = "expiresIn";
    public static final String KEY_APP_CODE = "app_code";
    public static final String KEY_ERROR_COMMENT = "error";
    public static final String KEY_LIMIT = "limit";
    public static final String KEY_OFFSET = "offset";

    public static final String app_code = "asdasd123asdaseh1j3h";

    public static String getToken(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TheUser.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public static void putToken(Activity activity, String token){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TheUser.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);

        editor.commit();
    }

    public static void removeToken(Activity activity)
    {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(TheUser.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN);
        editor.commit();
    }


    private static final String PROTOCOL = "https";
    private static final String HOST_NAME = "advise-server.herokuapp.com/api";

    public static String getHost()
    {
        return PROTOCOL + "://" + HOST_NAME;
    }
}
