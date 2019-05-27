/*
 * Copyright (c) 2011-2019, Zingaya, Inc. All rights reserved.
 */

package com.rnim.rn.audio;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import static com.rnim.rn.audio.Constants.NOTIFICATION_CONFIG;

public class VIForegroundService extends Service {

    private final int NOTIF_ID = 957;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Log.e("ERROR_SERVICE_ERROR", action)
        if (action != null) {
            if (action.equals(Constants.ACTION_FOREGROUND_SERVICE_START)) {
                // if (intent.getExtras() != null && intent.getExtras().containsKey(NOTIFICATION_CONFIG)) {
                //     Bundle notificationConfig = intent.getExtras().getBundle(NOTIFICATION_CONFIG);
                    // if (notificationConfig != null && notificationConfig.containsKey("id")) {
                        Notification notification = NotificationHelper.getInstance(getApplicationContext())
                            .buildNotification(getApplicationContext());

                        startForeground(NOTIF_ID, notification);
                //     }
                // }
            } else if (action.equals(Constants.ACTION_FOREGROUND_SERVICE_STOP)) {
                stopSelf();
            }
        }
        return START_NOT_STICKY;

    }

}