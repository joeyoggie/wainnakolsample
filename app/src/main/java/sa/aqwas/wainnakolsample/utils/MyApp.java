package com.vhorus.saloni.barberapp.utils;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.vhorus.saloni.barberapp.R;

import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class MyApp extends Application {
    private static MyApp mInstance;

    private Locale locale = null;

    @Override
    public void onCreate() {
        super.onCreate();

        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/arial.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );*/

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("Cairo-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        mInstance = this;


        String lang = MySettings.getActiveLanguage();
        LocaleUtils.setLocale(new Locale(lang));
        LocaleUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.CHANNEL_ID);
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND); // This line did the magic for me.

            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*if (locale != null)
        {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }*/
        LocaleUtils.updateConfig(this, newConfig);
    }

    public static synchronized MyApp getInstance() {
        return mInstance;
    }

    public static SharedPreferences getShardPrefs(){
        SharedPreferences prefs = mInstance.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
        return prefs;
    }
}