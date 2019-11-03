package sa.aqwas.wainnakolsample.utils;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import sa.aqwas.wainnakolsample.data.db.entities.User;

public class MySettings {
    private static final String TAG = MySettings.class.getSimpleName();

    private static final String PREF_ACTIVE_LANGUAGE = "pref_active_language";
    private static final String PREF_APP_FIRST_START = "pref_app_first_start";
    private static final String PREF_ACTIVE_USER_TYPE = "pref_active_user_type";
    private static final String PREF_ACTIVE_USER = "pref_active_user";
    private static final String PREF_NOTIFICATIONS_ENABLED = "pref_notifications_enabled";


    private static SharedPreferences sharedPref;
    private static String activeLanguage;
    private static boolean appFirstStart;
    private static int activeUserType;
    private static User activeUser;
    private static boolean notificationsEnabled;

    private static Gson gson;

    private MySettings(){

    }

    public static void setActiveLanguage(String language) {
        MySettings.activeLanguage = language;

        SharedPreferences.Editor editor = getSettings().edit();
        if(language != null) {
            editor.putString(PREF_ACTIVE_LANGUAGE, language);
        }else{
            editor.putString(PREF_ACTIVE_LANGUAGE, "en");
        }
        editor.apply();
    }
    public static String getActiveLanguage() {
        if (activeLanguage != null) {
            return activeLanguage;
        } else {
            SharedPreferences prefs = getSettings();
            activeLanguage = prefs.getString(PREF_ACTIVE_LANGUAGE, "en");
            return activeLanguage;
        }
    }

    public static void setAppFirstStart(boolean firstStart) {
        MySettings.appFirstStart = firstStart;

        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(PREF_APP_FIRST_START, firstStart);
        editor.apply();
    }
    public static boolean getAppFirstStart() {
        SharedPreferences prefs = getSettings();
        appFirstStart = prefs.getBoolean(PREF_APP_FIRST_START, true);
        return appFirstStart;
    }

    public static void setNotificationsEnabled(boolean notificationsEnabled) {
        MySettings.notificationsEnabled = notificationsEnabled;

        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(PREF_NOTIFICATIONS_ENABLED, notificationsEnabled);
        editor.apply();
    }
    public static boolean isNotificationsEnabled() {
        SharedPreferences prefs = getSettings();
        notificationsEnabled = prefs.getBoolean(PREF_NOTIFICATIONS_ENABLED, true);
        return notificationsEnabled;
    }

    public static void setActiveUserType(int userType) {
        MySettings.activeUserType = userType;

        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(PREF_ACTIVE_USER_TYPE, userType);
        editor.apply();
    }
    public static int getActiveUserType() {
        SharedPreferences prefs = getSettings();
        activeUserType = prefs.getInt(PREF_ACTIVE_USER_TYPE, User.USER_TYPE_OWNER);
        return activeUserType;
    }

    public static void setActiveUser(User user) {
        MySettings.activeUser = user;

        SharedPreferences.Editor editor = getSettings().edit();
        if(user != null) {
            if(gson == null){
                gson = Utils.getGson();
            }
            editor.putString(PREF_ACTIVE_USER, gson.toJson(user));
        }else{
            editor.putString(PREF_ACTIVE_USER, "");
        }
        editor.apply();
    }
    public static User getActiveUser() {
        if (activeUser != null) {
            return activeUser;
        } else {
            SharedPreferences prefs = getSettings();
            String jsonString = prefs.getString(PREF_ACTIVE_USER, "");
            if(jsonString.length() >= 1){
                if(gson == null){
                    gson = Utils.getGson();
                }
                activeUser = gson.fromJson(jsonString, new TypeToken<User>() {}.getType());
            }
            return activeUser;
        }
    }

    /*public static void logout(Activity activity){
        MySettings.setActiveUser(null);

        //logout from Facebook
        LoginManager.getInstance().logOut();

        //logout from Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        mGoogleSignInClient.signOut();
    }*/

    public static SharedPreferences getSettings() {
        if(sharedPref == null){
            sharedPref = MyApp.getShardPrefs();
        }

        return sharedPref;
    }
}
