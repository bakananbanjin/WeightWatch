package com.bakananbanjinApp2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

public class ReceiverNotification extends BroadcastReceiver {
    private static final String CALORIE_USED_TODAY = "calDay";
    private static final String CALORIE_BURN_DAY = "calBurn";
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerWW managerWW = new NotificationManagerWW();
        // Retrieve the extras from the Intent
        Bundle extras = intent.getExtras();
        int hour = 0;
        int min = 0;
        int notificationID = 0;
        if(extras != null){
            try {
                hour = extras.getInt("hour");
                min = extras.getInt("minute");
                notificationID = extras.getInt("NOTIFICATION_ID");
            } catch (Exception e){}
        }

        //differentiate based on the notification id
        if (notificationID == NotificationManagerWW.MORNING_NOTIFICATION_ID) {
                // Show the morning notification
            managerWW.showNotification(context,
                    NotificationManagerWW.MORNING_NOTIFICATION_ID,
                    context.getString(R.string.notification_morning_title),
                    context.getString(R.string.notification_morning_text));
            managerWW.scheduleNotification(context, hour, min, 1, NotificationManagerWW.MORNING_NOTIFICATION_REQUEST_CODE, notificationID);
        } else if (notificationID == NotificationManagerWW.EVENING_NOTIFICATION_ID) {
            // Show the evening notification
            SharedPreferences prefs = context.getSharedPreferences("WeightWatch", android.content.Context.MODE_PRIVATE);
            int cal = prefs.getInt(CALORIE_USED_TODAY, 0);
            int calDay = prefs.getInt(CALORIE_BURN_DAY, 0);
            int def = cal - calDay;
            String message = context.getString(R.string.notification_evening_text,
                        cal,
                        def);
            managerWW.showNotification(context,
                    NotificationManagerWW.EVENING_NOTIFICATION_ID,
                    context.getString(R.string.notification_evening_title),
                    message);
            managerWW.scheduleNotification(context, hour, min, 1, NotificationManagerWW.EVENING_NOTIFICATION_REQUEST_CODE, notificationID);
        }
        //Log.i("TAG", "onReceive: " + Calendar.getInstance().getTime().toString());
    }
}
