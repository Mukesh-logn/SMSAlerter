package com.mindtree.amexalerter.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

/**
 * Created by M1030452 on 4/22/2018.
 */

public class PlaySoundService extends Service {
    MediaPlayer mp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mp = MediaPlayer.create(this, notification);
        mp.setLooping(true);*/


        Context context = getApplicationContext();

        // Check whether has the write settings permission or not.
        boolean settingsCanWrite = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            settingsCanWrite = Settings.System.canWrite(context);
        }

        if (!settingsCanWrite) {
            // If do not have write settings permission then open the Can modify system settings panel.
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
        } else {
            // If has permission then show an alert dialog with message.
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mp = MediaPlayer.create(this, notification);
            mp.setLooping(true);
            mp.start();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.stop();
    }
}
