/*
 * Copyright (c) 2011-2019, Zingaya, Inc. All rights reserved.
 */

package com.rnim.rn.audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.graphics.Color;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

import static com.rnim.rn.audio.Constants.ERROR_ANDROID_VERSION;
import static com.rnim.rn.audio.Constants.ERROR_INVALID_CONFIG;

class NotificationHelper {
    private static NotificationHelper instance = null;
    private NotificationManager mNotificationManager;
    private String channelId = "RecordingForegroundService";

    public static synchronized NotificationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationHelper(context);
        }
        return instance;
    }

    private NotificationHelper(Context context) {
        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Notification Channel";
            int channelImportance = 2;
            String channelDescription = "Notification Channel for Foreground Service when Recording";
            boolean enableVibration = false;
            
            NotificationChannel channel = new NotificationChannel(channelId, channelName, channelImportance);
            channel.setDescription(channelDescription);
            channel.enableVibration(enableVibration);
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    Notification buildNotification(Context context) {
        Class mainActivityClass = getMainActivityClass(context);
        if (mainActivityClass == null) {
            return null;
        }
        Intent notificationIntent = new Intent(context, mainActivityClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder notificationBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(context, channelId);
        } else {
            notificationBuilder = new NotificationCompat.Builder(context);
        }

        int priority = NotificationCompat.PRIORITY_HIGH;
        String title = "Notiv is Recording";
        String text = "Starting to record...";

        notificationBuilder.setContentTitle(title)
            .setContentText(text)
            .setPriority(priority)
            .setContentIntent(pendingIntent);

        notificationBuilder.setSmallIcon(getResourceIdForResourceName(context, "ic_notification"));

        notificationBuilder.setColor(Color.parseColor("#60d189"));

        return notificationBuilder.build();
    }

    // public void updateNotification(Context context, Bundle notificationConfig) {
    //     Notification notification = buildNotification(context, notificationConfig);
    //     mNotificationManager.notify((int)notificationConfig.getDouble("id"), notification);
    // }

    private Class getMainActivityClass(Context context) {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null || launchIntent.getComponent() == null) {
            Log.e("NotificationHelper", "Failed to get launch intent or component");
            return null;
        }
        try {
            return Class.forName(launchIntent.getComponent().getClassName());
        } catch (ClassNotFoundException e) {
            Log.e("NotificationHelper", "Failed to get main activity class");
            return null;
        }
    }

    private int getResourceIdForResourceName(Context context, String resourceName) {
        int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        if (resourceId == 0) {
            resourceId = context.getResources().getIdentifier(resourceName, "mipmap", context.getPackageName());
        }
        return resourceId;
    }
}