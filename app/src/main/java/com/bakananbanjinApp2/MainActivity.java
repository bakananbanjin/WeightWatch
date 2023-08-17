package com.bakananbanjinApp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;

import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity{
    private static final String CALORIE_USED_TODAY = "calDay";
    private static final String CALORIE_BURN_DAY = "calBurn";
    public static float TEXTSIZE;
    public static int DIAGRAMMDAYS = 7;
    public static final String USER = "user";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String AGE = "age";
    public static final String ISMAN = "isMan";
    public static final String TARGETWEIGHT = "targetWeight";
    public static final String ACTIVITYLEVEL = "activity";
    public static final String NOTIFICATION_ENABLED = "notification_enabled";
    public static final String NOTIFICATION_MORNING = "notification_morning";
    public static final String NOTIFICATION_EVENING = "notification_evening";
    public static final String NOTIFICATION_MORNING_HOUR = "notification_morning_hour";
    public static final String NOTIFICATION_MORNING_MIN = "notification_morning_min";
    public static final String NOTIFICATION_EVENING_HOUR = "notification_evening_hour";
    public static final String NOTIFICATION_EVENING_MIN = "notification_evening_min";
    public static SharedPreferences mPrefs;
    public static SharedPreferences.Editor mEditor;
    public static User user;
    private Toolbar toolbar;
    private TextView textViewToolbar;
    private Graph graph;
    public static FragmentManager fragmentManager;
    private TableLayout dayPlanner;
    HorizontalScrollView horizontalScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //test change for version 2.0
        //initialise  Engine
        Engine engine = new Engine(this.getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        //sharedPreferences for user data
        mPrefs = getSharedPreferences("WeightWatch", MODE_PRIVATE);
        mEditor = mPrefs.edit();

        //initialise Toolbar
        textViewToolbar = findViewById(R.id.toolbar_textview);
        textViewToolbar.setText(getString(R.string.welcome));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //initialise Fragment for information overview
        fragmentManager = getSupportFragmentManager();
        Fragment frag = fragmentManager.findFragmentById(R.id.fragment_container);
        if (frag == null) {
            frag = new Overview();
            fragmentManager.beginTransaction().add(R.id.fragment_container, frag).commit();
        }

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertDialog insertDialog = new InsertDialog();
                insertDialog.show(getSupportFragmentManager(), "");
                //update preference for notification
                mEditor.putInt(CALORIE_USED_TODAY, 0);
                mEditor.apply();
            }
        });

        //if no user is present ask to create one
        if (!mPrefs.contains("user")) {
            //CreateUser createUserDialog = new CreateUser();
            //createUserDialog.show(getSupportFragmentManager(), "");
            Intent intent = new Intent(getApplicationContext(), ActivityCreateUser.class);
            startActivity(intent);
        }
        //should be else???
        try {
            user = new User(mPrefs.getString(USER, "user"),
                    mPrefs.getBoolean(ISMAN, true),
                    mPrefs.getInt(AGE, 30),
                    mPrefs.getInt(HEIGHT, 170),
                    mPrefs.getFloat(WEIGHT, 50f),
                    mPrefs.getInt(TARGETWEIGHT, 0),
                    mPrefs.getFloat(ACTIVITYLEVEL, 1.2f));
            textViewToolbar.setText(getString(R.string.welcome) + " " + user.getUserName());
        } catch (Exception e) {
            //Log.e("NOUSER", "no user found");
            mEditor.clear().apply();
            user = new User("user", true, 0, 0, 0, 0, 0);
        }
        //check if preference hold values for notifications if not create them
        if(!mPrefs.contains(NOTIFICATION_ENABLED)){
            mEditor.putBoolean(NOTIFICATION_ENABLED, false);
            mEditor.putBoolean(NOTIFICATION_MORNING, false);
            mEditor.putBoolean(NOTIFICATION_EVENING, false);
            mEditor.putInt(NOTIFICATION_MORNING_HOUR, 8);
            mEditor.putInt(NOTIFICATION_MORNING_MIN, 0);
            mEditor.putInt(NOTIFICATION_EVENING_HOUR, 22);
            mEditor.putInt(NOTIFICATION_EVENING_MIN, 0);
            mEditor.commit();
        }

        Engine.initGraphFromDB();
        graphQuery();
        horizontalScrollView = findViewById(R.id.main_hscroll_view);
        dayPlanner = DayPlanner.generateTable(this);
        horizontalScrollView.addView(dayPlanner);

        //save preference for Notification
        mEditor.putInt(CALORIE_BURN_DAY, Engine.calcCalNeed(user));
        mEditor.apply();
    }
    private void graphQuery() {
        graph = findViewById(R.id.graph);
        //how many days (items) will be shown (xAxis Labels) 7 = one week
        int graphDays = DIAGRAMMDAYS;
        //List for last graphDays dates
        List<YearMonthDay> lastDaysList = new ArrayList<>();
        lastDaysList = Engine.getLastdays(graphDays);
        //list for all weights of the last graphDays
        List<Float> weightList = new ArrayList<>();
        //List for all call used of the last graphDays
        List<Float> calList = new ArrayList<>();
        //List for xAxisLabel
        List<String> xAxisLabelList = new ArrayList<>();
        for (YearMonthDay i : lastDaysList) {
            xAxisLabelList.add(i.toString());
            calList.add(Engine.mDB.calculateSumByDate(i.year, i.month, i.day, DataSetDB.DB_TABLE_NAME, DataSetDB.DB_ROW_CAL));
            weightList.add(Engine.mDB.calculateAvgByDate(i.year, i.month, i.day, DataSetDB.DB_TABLE_NAME_WEIGHT, DataSetDB.TABLEWEIGHT_ROW_WEIGHT));
        }
        graph.setData(xAxisLabelList, weightList, calList, Engine.calcCalNeed(user));
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //switch range of graph and put visibility of column Label
                switch(DIAGRAMMDAYS){
                    case 7:
                        DIAGRAMMDAYS = 14;
                        graph.setLabelVisible(false);
                        break;
                    case 14:
                        DIAGRAMMDAYS = 28;
                        graph.setLabelVisible(false);
                        break;
                    default:
                        DIAGRAMMDAYS = 7;
                        graph.setLabelVisible(true);
                }
                graphQuery();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menue_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int selectedId = item.getItemId();
        if (selectedId == R.id.menu_Profil) {
            Intent intent = new Intent(this, ProfilActivity.class);
            startActivity(intent);
            return true;
        } else if (selectedId == R.id.menu_Info) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
            return true;
        } else if (selectedId == R.id.menu_Edit) {
            Intent intent = new Intent(this, EditActivity.class);
            startActivity(intent);
            return true;
        } else if (selectedId == R.id.menu_Help) {
            Intent intent = new Intent(this, ActivityHelp.class);
            startActivity(intent);

            return true;
        }
        else if (selectedId == R.id.menu_Option) {
            Intent intent = new Intent(this, ActivityOptions.class);
            startActivity(intent);
            return true;

        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        mEditor.putInt(CALORIE_USED_TODAY, Engine.calcUsedToday());
        mEditor.putInt(CALORIE_BURN_DAY, Engine.calcCalNeed(user));
        mEditor.apply();
        super.onStop();
    }
    @Override
    protected void onPause() {
        mEditor.putInt(CALORIE_USED_TODAY, Engine.calcUsedToday());
        mEditor.putInt(CALORIE_BURN_DAY, Engine.calcCalNeed(user));
        mEditor.apply();
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Overview fragment = (Overview) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.updateOverview();
        }
        graphQuery();
        dayPlannerUpdate();
    }
    private void dayPlannerUpdate() {
        dayPlanner = DayPlanner.generateTable(this);
        horizontalScrollView.removeAllViews();
        horizontalScrollView.addView(dayPlanner);
    }
    public void restartApp() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
