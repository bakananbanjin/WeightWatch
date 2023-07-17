package com.bakananbanjinApp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String USER = "user";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String AGE = "age";
    public static final String ISMAN = "isMan";
    public static final String TARGETWEIGHT = "targetWeight";
    public static final String ACTIVITYLEVEL = "activity";
    private static final int TABELCELLCOUNT = 25 * 8 - 1;
    public static SharedPreferences mPrefs;
    public static SharedPreferences.Editor mEditor;
    public static User user;
    private Toolbar toolbar;
    private TextView textViewToolbar;
    private Graph graph;
    public static FragmentManager fragmentManager;
    private TableLayout dayPlanner;
    private boolean tableGenerated = false;
    private List<Integer> cellIdList = new ArrayList<>();


    //1.
    //2. Toolbar Textcolor dynamisch anpassen oder eigenen Theme schreiben
    //3. Icon fuer toolbar anpassen
    //4. Make own Graph Class
    //5. Advise in Engine
    //6. button to add weight and additonal cal
    //7. Update Cal need per day on current weight


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        //shardePreferences for user data
        mPrefs = getSharedPreferences("WeightWatch", MODE_PRIVATE);
        mEditor = mPrefs.edit();

        //initialise  Engine
        Engine engine = new Engine(this.getApplicationContext());

        //initialise Toolbar
        textViewToolbar = findViewById(R.id.toolbar_textview);
        textViewToolbar.setText(getString(R.string.tollbar_welcome));
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
            textViewToolbar.setText(getString(R.string.tollbar_welcome) + " " + user.getUserName());
        } catch (Exception e) {
            Log.e("NOUSER", "no user found");
            mEditor.clear().commit();
            user = new User("user", true, 0, 0, 0, 0, 0);
        }
        /*
         * TEST CODE
         *
         */

        Engine.initGraphFromDB();
        graphQuery();
        generateTable();
    }

    private void graphQuery() {
        graph = findViewById(R.id.graph);
        //how many days (items) will be shown (xAxis Labels) 7 = one week
        int graphDays = 7;
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
            Log.i("MENU", "Profil selected");
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
        } else if (selectedId == R.id.menu_Backup) {
            if (Engine.backupAll(user)) {
                Toast.makeText(this, getText(R.string.backup_information), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getText(R.string.backup_information_fail), Toast.LENGTH_LONG).show();
            }

            Log.i("MENU", "Backup selected");
            return true;
        } else if (selectedId == R.id.menu_Delete) {
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
        Overview fragment = (Overview) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.updateOverview();
        }
        graphQuery();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        graphQuery();
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

    private void generateTable() {
        if (tableGenerated) {
            return;
        }
        tableGenerated = true;

        dayPlanner = findViewById(R.id.dayplaner);
        //dayPlanner.removeAllViews();
        //24 hours + header
        int numRows = 25;
        //7days + time
        int numCols = 8;
        //id for all cells beginning at 1 unique in tablelayout
        int cellId = 1;

        for (int i = 0; i < numRows; i++) {
            // Create a new TableRow for each row
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            ));

            for (int j = 0; j < numCols; j++) {
                // Create a new TextView for each cell
                TextView cell = new TextView(this);
                cell.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                ));

                // Set the text and other properties for the cell

                cell.setTextSize(12f);
                cell.setId(cellId++);
                cell.setPadding(5, 3, 5, 3);
                cell.setBackgroundResource(R.drawable.black_boarder_green_background);


                // Add the cell to the TableRow
                tableRow.addView(cell);
            }
            // Add the TableRow to the TableLayout
            dayPlanner.addView(tableRow);
            //init table
        }
        changeCellText(1, getString(R.string.planner_time_date));
        changeCellFormat(1);
        //get the last 7 days
        List<YearMonthDay> last7days = Engine.getLastdays(6);


        // 2-8 id is the first row
        for (int i = 2; i <= 8; i++) {
            changeCellText(i, last7days.get(i - 2).toString());
            changeCellFormat(i);
        }

        //label the time windows row has 8 coloms first time starts at cell 9
        int j=0;
        for(int i = 9; i < TABELCELLCOUNT; i = i+8){
            int startTime = j++;
            int endTime = startTime + 1;
            changeCellText(i,  startTime + " to " + endTime) ;
            changeCellFormat(i);
        }

    }

    private void changeCellText(int id, String text) {
        //check if id is in range of the table 25 * 8 and at least 1
        if (id > 200 || id < 1) {
            return;
        }
        TextView cell = findViewById(id);
        cell.setText(text);
    }

    private void changeCellFormat(int id)
    {
        if (id > 200 || id < 1) {
            return;
        }
        TextView cell = findViewById(id);
        cell.setBackgroundResource(R.drawable.black_boarder_gray_background);
        cell.setTypeface(null, Typeface.BOLD);
    }
}
