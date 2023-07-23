package com.bakananbanjinApp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
    public static float TEXTSIZE;
    public static int DIAGRAMMDAYS = 7;
    public static final String USER = "user";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String AGE = "age";
    public static final String ISMAN = "isMan";
    public static final String TARGETWEIGHT = "targetWeight";
    public static final String ACTIVITYLEVEL = "activity";
    public static SharedPreferences mPrefs;
    public static SharedPreferences.Editor mEditor;
    public static User user;
    private Toolbar toolbar;
    private TextView textViewToolbar;
    private Graph graph;
    public static FragmentManager fragmentManager;
    private TableLayout dayPlanner;
    HorizontalScrollView horizontalScrollView;

    //1.
    //2. Toolbar Textcolor dynamisch anpassen oder eigenen Theme schreiben
    //3. Icon fuer toolbar anpassen
    //4.
    //5. Advise in Engine
    //6. button to add weight and additonal cal
    //7. Update Cal need per day on current weight

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initialise  Engine
        Engine engine = new Engine(this.getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        //shardePreferences for user data
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
            }
        });

        //if no user is present ask to create one
        if (!mPrefs.contains("user")) {
            CreateUser createUserDialog = new CreateUser();
            createUserDialog.show(getSupportFragmentManager(), "");
        }
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
            mEditor.clear().commit();
            user = new User("user", true, 0, 0, 0, 0, 0);
        }

        Engine.initGraphFromDB();
        graphQuery();
        horizontalScrollView = findViewById(R.id.main_hscroll_view);
        dayPlanner = DayPlanner.generateTable(this);
        horizontalScrollView.addView(dayPlanner);
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
        } /*else if (selectedId == R.id.menu_Backup) {
            if (Engine.backupAll(user)) {
                Toast.makeText(this, getText(R.string.backup_information), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getText(R.string.backup_information_fail), Toast.LENGTH_LONG).show();
            }

            return true;
        } */
        else if (selectedId == R.id.menu_Delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

            return true;
        }
        return false;
    }
    @Override
    protected void onResume() {
        super.onResume();
       /*  20.07 21.12 test commented may needs to be reinstaded
       Overview fragment = (Overview) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.updateOverview();
        }
        graphQuery();*/
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
    /*
    +
    +  TEST CODE ONLY BELOW
    +
     */
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
