package com.bakananbanjinApp2;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class NotificationManagerWW {
    private static final String CHANNEL_ID = "WeightWatchID";
    private static final CharSequence CHANNEL_NAME = "Weight Watch Channel";
    public static final int MORNING_NOTIFICATION_ID = 1;
    public static final int EVENING_NOTIFICATION_ID = 2;
    public static final int MORNING_NOTIFICATION_REQUEST_CODE = 1;
    public static final int EVENING_NOTIFICATION_REQUEST_CODE = 2;

    public NotificationManagerWW() {
    }
    public static void scheduleNotification(Context context, int hour, int min, int day, int request_code, int notificationID){
        //set the time for the Notification
        Calendar calendar = Calendar.getInstance();
        int day1 = day;

        if(calendar.get(Calendar.HOUR_OF_DAY) >= hour){
            //current hour is bigger or equal to schedule hour
            if(calendar.get(Calendar.MINUTE) > min){
                //current min is bigger then schedule min
                // we have to show the notification tomorrow
                day1 = 1;
            }
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        //add one day for the next Alarm
        calendar.add(Calendar.DAY_OF_MONTH, day1);

        //create an intent to trigger the receiver
        Intent intent = new Intent(context, ReceiverNotification.class);
        intent.putExtra("NOTIFICATION_ID", notificationID);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", min);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(
                    context,
                    request_code,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getBroadcast(
              context,
              request_code,
              intent,
              PendingIntent.FLAG_UPDATE_CURRENT
            );
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if(alarmManager != null) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }
    public void cancelNotification(Context context, int request_code){
        Intent intent = new Intent(context, ReceiverNotification.class);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(
                    context,
                    request_code,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getBroadcast(
                    context,
                    request_code,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
            );
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        //cancel any existing notification that might be currently shown
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(request_code);
    }
    public void showNotification(Context context, int notification_id, String notification_title, String notification_text) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //create pendingIntend to start MainActivity.class
        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);


        // Create a Notification Channel and Notification for devices running Android Oreo (API 26) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);

            PendingIntent pendingIntent =  PendingIntent.getActivity(context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT  | PendingIntent.FLAG_IMMUTABLE);

            //Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.toolbar_icon)
                    .setContentTitle(notification_title)
                    .setContentText(notification_text)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notification_text))
                    .setAutoCancel(true);
            notificationManager.notify(notification_id, builder.build());

        } else {
            //Create Notification for devices running Android 24 or 25
            PendingIntent pendingIntent =  PendingIntent.getActivity(context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.toolbar_icon)
                    .setContentTitle(notification_title)
                    .setContentText(notification_text)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notification_text))
                    .setAutoCancel(true);
            notificationManager.notify(notification_id, builder.build());
        }
    }
}
