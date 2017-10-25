package com.example.sabila.mobilantimaling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;


import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sabila on 10/25/2017.
 */

public class NotificationEventReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
    private static final String ACTION_DELETE_NOTIFICATION_NOTIFICATION = "ACTION_DELETE_NOTIFICATION_NOTIFICATION";
    private static final int NOTIFICATION_INTERVAL_IN_HOURS = 2;

    public static void setupAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = getStartPendingIntent(context);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, getTriggerAt(new Date()), NOTIFICATION_INTERVAL_IN_HOURS * AlarmManager.INTERVAL_HOUR, alarmIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("HUAA", "RECEIVEEEE");
        String action = intent.getAction();
        Intent serviceIntent = null;
        if (ACTION_START_NOTIFICATION_SERVICE.equals(action)) {
            serviceIntent = NotificationIntentService.createIntentStartNotificationService(context);

        } else if (ACTION_DELETE_NOTIFICATION_NOTIFICATION.equals(action)) {
            serviceIntent = NotificationIntentService.createIntentStartNotificationService(context);
        }

        if (serviceIntent != null) {
            startWakefulService(context, serviceIntent);
        }
    }

    private static long getTriggerAt (Date now) {
        Calendar calendar =  Calendar.getInstance();
        calendar.setTime(now);
        return calendar.getTimeInMillis();
    }

    private static PendingIntent getStartPendingIntent (Context context) {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_START_NOTIFICATION_SERVICE);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static PendingIntent getDeleteIntent(Context context) {
        Intent intent = new Intent(context, NotificationEventReceiver.class);
        intent.setAction(ACTION_DELETE_NOTIFICATION_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
