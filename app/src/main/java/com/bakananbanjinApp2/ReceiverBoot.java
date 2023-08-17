package com.bakananbanjinApp2;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Calendar;

public class ReceiverBoot extends BroadcastReceiver {
    public static final String NOTIFICATION_ENABLED = "notification_enabled";
    public static final String NOTIFICATION_MORNING = "notification_morning";
    public static final String NOTIFICATION_EVENING = "notification_evening";
    public static final String NOTIFICATION_MORNING_HOUR = "notification_morning_hour";
    public static final String NOTIFICATION_MORNING_MIN = "notification_morning_min";
    public static final String NOTIFICATION_EVENING_HOUR = "notification_evening_hour";
    public static final String NOTIFICATION_EVENING_MIN = "notification_evening_min";
    public static final int MORNING_NOTIFICATION_ID = 1;
    public static final int EVENING_NOTIFICATION_ID = 2;
    public static final int MORNING_NOTIFICATION_REQUEST_CODE = 1;
    public static final int EVENING_NOTIFICATION_REQUEST_CODE = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            //here restart Notification after device restart
            restartNotifications(context);
        }
    }

    private void restartNotifications(Context context) {
        //get an instance of preferences and restart the notifications
        SharedPreferences mPrefs = context.getSharedPreferences("WeightWatch", android.content.Context.MODE_PRIVATE);

        int day = 0;

        /*Calendar calendar = Calendar.getInstance();

                Code later
        */

        //check if notifications are enabled
        if(mPrefs.getBoolean(NOTIFICATION_ENABLED, false)){
            NotificationManagerWW managerWW = new NotificationManagerWW();
            //restart morning Notification
            if(mPrefs.getBoolean(NOTIFICATION_MORNING, false)){
                //get time and check if time has passed and schedule for tomorrow (implemented later)

                int hour = mPrefs.getInt(NOTIFICATION_MORNING_HOUR, 8);
                int min = mPrefs.getInt(NOTIFICATION_MORNING_MIN, 0);
                //schedule morning notification
                managerWW.scheduleNotification(context,
                        hour,
                        min,
                        day,
                        MORNING_NOTIFICATION_REQUEST_CODE,
                        MORNING_NOTIFICATION_ID);

            }
            if(mPrefs.getBoolean(NOTIFICATION_EVENING, false)){
                //scheudle evening notification
                int hour = mPrefs.getInt(NOTIFICATION_EVENING_HOUR, 22);
                int min = mPrefs.getInt(NOTIFICATION_EVENING_MIN, 0);
                //schedule morning notification
                managerWW.scheduleNotification(context,
                        hour,
                        min,
                        day,
                        EVENING_NOTIFICATION_REQUEST_CODE,
                        EVENING_NOTIFICATION_ID);
            }
        }
    }
}
