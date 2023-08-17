package com.bakananbanjinApp2;

import static com.bakananbanjinApp2.MainActivity.mEditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ActivityOptions extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TimePicker.OnTimeChangedListener {
    private static final String TAG = "OPTIONS";
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static int notificationMorningHour = 8;
    private static int notificationMorningMinute = 0;
    private static int notificationEveningHour = 22;
    private static int notificationEveningMinute = 0;
    private Switch switchEnableNotifications;
    private Switch switchMorningNotifications;
    private Switch switchEveningNotifications;
    private static boolean enableNotifications = false;
    private static boolean morningNotifications = false;
    private static boolean eveningNotifications = false;
    private TimePicker tpMorning;
    private TimePicker tpEvening;
    private Button btDelete;
    private ImageButton ibtMorningNotification;
    private ImageButton ibtEveningNotification;
    private View divider;
    private TextView tvNotificationBlocked;
    private TextView tvNotificationInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //set toolbar as action bar and show back arrow and title
        Toolbar toolbar = findViewById(R.id.toolbar_options);
        toolbar.setTitle(R.string.info_toolbar_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //get the notification settings from the preference
        notificationMorningMinute = MainActivity.mPrefs.getInt(MainActivity.NOTIFICATION_MORNING_MIN, notificationMorningMinute);
        notificationEveningHour = MainActivity.mPrefs.getInt(MainActivity.NOTIFICATION_EVENING_HOUR, notificationEveningHour);
        notificationEveningMinute = MainActivity.mPrefs.getInt(MainActivity.NOTIFICATION_EVENING_MIN, notificationEveningMinute);
        notificationMorningHour = MainActivity.mPrefs.getInt(MainActivity.NOTIFICATION_MORNING_HOUR, notificationMorningHour);
        enableNotifications = MainActivity.mPrefs.getBoolean(MainActivity.NOTIFICATION_ENABLED, enableNotifications);
        morningNotifications = MainActivity.mPrefs.getBoolean(MainActivity.NOTIFICATION_MORNING, morningNotifications);
        eveningNotifications = MainActivity.mPrefs.getBoolean(MainActivity.NOTIFICATION_EVENING, eveningNotifications);


        if(!Engine.areNotificationsEnabled(getApplicationContext())){
            //notifications are blocked
            enableNotifications = false;
            morningNotifications = false;
            eveningNotifications = false;
        }


        tvNotificationInformation = findViewById(R.id.tv_options_notification_information);
        divider = findViewById(R.id.divider9);
        tvNotificationBlocked = findViewById(R.id.tv_options_notification_blocked);
        switchEnableNotifications = findViewById(R.id.switch_options_enable_notifications);
        switchMorningNotifications = findViewById(R.id.switch_options_morning_notifications);
        switchEveningNotifications = findViewById(R.id.switch_options_evening_notifications);
        ibtMorningNotification = findViewById(R.id.ibt_optiions_notification_morning);
        ibtEveningNotification = findViewById(R.id.ibt_optiions_notification_evening);

        tpMorning = findViewById(R.id.tp_options_morning);
        tpEvening = findViewById(R.id.tp_options_evening);
        tpMorning.setIs24HourView(true);
        tpEvening.setIs24HourView(true);
        tpMorning.setHour(notificationMorningHour);
        tpMorning.setMinute(notificationMorningMinute);
        tpEvening.setHour(notificationEveningHour);
        tpEvening.setMinute(notificationEveningMinute);

        switchEnableNotifications.setChecked(enableNotifications);
        switchEveningNotifications.setEnabled(enableNotifications);
        switchMorningNotifications.setEnabled(enableNotifications);
        switchMorningNotifications.setChecked(morningNotifications);
        switchEveningNotifications.setChecked(enableNotifications);
        if(enableNotifications){
            tvNotificationInformation.setVisibility(View.VISIBLE);
        }
        //check if notifications a active and show button to expand time picker
        if(morningNotifications){
            ibtMorningNotification.setVisibility(View.VISIBLE);
            ibtMorningNotification.setImageResource(android.R.drawable.arrow_down_float);
        }
        if(eveningNotifications){
            ibtEveningNotification.setVisibility(View.VISIBLE);
            ibtEveningNotification.setImageResource(android.R.drawable.arrow_down_float);
        }

        switchEnableNotifications.setOnCheckedChangeListener(this);
        switchMorningNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                morningNotifications = b;
                if(!b) {
                    tpMorning.setVisibility(View.GONE);
                    divider.setVisibility(View.GONE);
                    ibtMorningNotification.setVisibility(View.GONE);

                } else {
                    divider.setVisibility(View.VISIBLE);
                    ibtMorningNotification.setVisibility(View.VISIBLE);
                    ibtMorningNotification.setImageResource(android.R.drawable.arrow_down_float);
                }

            }
        });
        switchEveningNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                eveningNotifications = b;
                if(!b) {
                    tpEvening.setVisibility(View.GONE);
                    ibtEveningNotification.setVisibility(View.GONE);
                } else {
                    ibtEveningNotification.setVisibility(View.VISIBLE);
                    ibtEveningNotification.setImageResource(android.R.drawable.arrow_down_float);

                }
            }
        });
        ibtMorningNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tpMorning.getVisibility() == View.VISIBLE){
                    tpMorning.setVisibility(View.GONE);
                    ibtMorningNotification.setImageResource(android.R.drawable.arrow_down_float);
                } else {
                    tpMorning.setVisibility(View.VISIBLE);
                    ibtMorningNotification.setImageResource(android.R.drawable.arrow_up_float);
                }
            }
        });
        ibtEveningNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tpEvening.getVisibility() == View.VISIBLE){
                    tpEvening.setVisibility(View.GONE);
                    ibtEveningNotification.setImageResource(android.R.drawable.arrow_down_float);
                } else {
                    tpEvening.setVisibility(View.VISIBLE);
                    ibtEveningNotification.setImageResource(android.R.drawable.arrow_up_float);
                }
            }
        });
        tpMorning.setOnTimeChangedListener(this);
        tpEvening.setOnTimeChangedListener(this);


        btDelete = findViewById(R.id.bt_options_delete_data);
        btDelete.setOnClickListener(this);


        //Test data load and save data
        /*
        Button save = findViewById(R.id.savedata);
        Button load = findViewById(R.id.loaddata);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: ");
                if(Engine.backupAll(MainActivity.user)){
                    Toast.makeText(getApplicationContext(), "DATA SAVED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "DATA NOT SAVED", Toast.LENGTH_SHORT).show();
                }
            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Engine.insertIntoDBFromFiles();
            }
        });
        */
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Log.i("TEST", "granted");
            } else {
                enableNotifications = false;
                switchEnableNotifications.setChecked(enableNotifications);
                Toast.makeText(this, getText(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onBackPressed() {
        super.onBackPressed();
    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.bt_options_delete_data){
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityOptions.this);
            builder.setTitle(getString(R.string.delet_dialogbox_title));
            builder.setMessage(getString(R.string.delet_dialogbox_warning));
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Engine.deleteData();
                    Engine.getPref();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            enableNotifications = b;
            if(!b){
                tpMorning.setVisibility(View.GONE);
                tpEvening.setVisibility(View.GONE);
                ibtMorningNotification.setVisibility(View.GONE);
                ibtEveningNotification.setVisibility(View.GONE);
                tvNotificationInformation.setVisibility(View.GONE);
            } else {
                //ask for permission if not present
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
                }
                //App-Notification are blocked
                if(!Engine.areNotificationsEnabled(this)){
                    //Toast message is maybe disabled as well if Notifications are blocked
                    //maybe make a textview message as well
                    Toast.makeText(getBaseContext(), R.string.options_notification_blocked, Toast.LENGTH_SHORT).show();
                    tvNotificationBlocked.setVisibility(View.VISIBLE);
                    //switch cant turn on if Notifications are blocked
                    enableNotifications = false;
                    switchEnableNotifications.setChecked(enableNotifications);
                } else {
                    tvNotificationInformation.setVisibility(View.VISIBLE);
                    tvNotificationBlocked.setVisibility(View.GONE);
                    if (morningNotifications) {
                        ibtMorningNotification.setVisibility(View.VISIBLE);
                    }
                    if (eveningNotifications) {
                        ibtEveningNotification.setVisibility(View.VISIBLE);
                    }
                }
            }
        switchEveningNotifications.setEnabled(enableNotifications);
        switchMorningNotifications.setEnabled(enableNotifications);
        morningNotifications = false;
        eveningNotifications = false;
        switchMorningNotifications.setChecked(false);
        switchEveningNotifications.setChecked(false);

    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int i, int i1) {
        if(timePicker.getId() == R.id.tp_options_morning){
            notificationMorningMinute = i1;
            notificationMorningHour = i;
        }else if(timePicker.getId() == R.id.tp_options_evening){
            notificationEveningHour = i;
            notificationEveningMinute = i1;
        }
    }
    //save changes to notification settings in preferences call at onPause and onStop
    //set schedule notification accordingly
    public void saveNotificationSettings(){
        mEditor.putBoolean(MainActivity.NOTIFICATION_ENABLED, enableNotifications);
        mEditor.putBoolean(MainActivity.NOTIFICATION_MORNING, morningNotifications);
        mEditor.putBoolean(MainActivity.NOTIFICATION_EVENING, eveningNotifications);
        mEditor.putInt(MainActivity.NOTIFICATION_MORNING_HOUR, notificationMorningHour);
        mEditor.putInt(MainActivity.NOTIFICATION_MORNING_MIN, notificationMorningMinute);
        mEditor.putInt(MainActivity.NOTIFICATION_EVENING_HOUR, notificationEveningHour);
        mEditor.putInt(MainActivity.NOTIFICATION_EVENING_MIN, notificationEveningMinute);
        mEditor.commit();
        if(!enableNotifications){
            //user has shut down the Notifications disable all notifications
            Engine.disableAllNotifications(getApplicationContext());
        } else if(!morningNotifications) {
            Engine.disableMorningNotifications(getApplicationContext());
        } else if(!eveningNotifications){
            Engine.disableEveningNotifications(getApplicationContext());
        }
        Engine.setNotification(getApplicationContext());
        //String temp = notificationMorningHour + " " + notificationMorningMinute + " " + notificationEveningHour + " " + notificationEveningMinute;
        //Log.i(TAG, "saveNotificationSettings: " + temp);
    }
    @Override
    protected void onPause() {
        saveNotificationSettings();
        super.onPause();

    }
    @Override
    protected void onStop() {
        saveNotificationSettings();
        super.onStop();
    }

    @Override
    protected void onResume() {
        //get the notification settings from the preference
        notificationMorningMinute = MainActivity.mPrefs.getInt(MainActivity.NOTIFICATION_MORNING_MIN, notificationMorningMinute);
        notificationEveningHour = MainActivity.mPrefs.getInt(MainActivity.NOTIFICATION_EVENING_HOUR, notificationEveningHour);
        notificationEveningMinute = MainActivity.mPrefs.getInt(MainActivity.NOTIFICATION_EVENING_MIN, notificationEveningMinute);
        notificationMorningHour = MainActivity.mPrefs.getInt(MainActivity.NOTIFICATION_MORNING_HOUR, notificationMorningHour);
        enableNotifications = MainActivity.mPrefs.getBoolean(MainActivity.NOTIFICATION_ENABLED, enableNotifications);
        eveningNotifications = MainActivity.mPrefs.getBoolean(MainActivity.NOTIFICATION_EVENING, eveningNotifications);
        tpMorning.setHour(notificationMorningHour);
        tpMorning.setMinute(notificationMorningMinute);
        tpEvening.setHour(notificationEveningHour);
        tpEvening.setMinute(notificationEveningMinute);
        super.onResume();
    }
}