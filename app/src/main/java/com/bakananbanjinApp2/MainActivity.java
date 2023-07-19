package com.bakananbanjinApp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class MainActivity extends AppCompatActivity {

    public static int DIAGRAMMDAYS = 7;
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
        HorizontalScrollView horizontalScrollView = findViewById(R.id.main_hscroll_view2);
        TableLayout dayPlanner = DayPlanner.generateTable(this);
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
        generateTable();
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
      /*  if (tableGenerated) {
            return;
        }
        tableGenerated = true;*/
        dayPlanner = findViewById(R.id.dayplaner);
        dayPlanner.removeAllViews();
        //24 hours + header
        int numRows = 25;
        //7days + time
        int numCols = 8;
        //id for all cells beginning at 1 unique in tablelayout
        int cellId = 0;
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
                cellId = (i * 10 + j);
                if(cellId > 0){
                    cell.setId(cellId);
                } else {
                    int temp = 201;
                    cell.setId(temp);
                }
                cell.setTextSize(12f);

                cell.setPadding(5, 3, 5, 3);
                cell.setBackgroundResource(R.drawable.black_boarder_green_background);

                // Add the cell to the TableRow
                tableRow.addView(cell);
            }
            // Add the TableRow to the TableLayout
            dayPlanner.addView(tableRow);
            //init table
        }

        changeCellText(201, getString(R.string.planner_time_date));
        changeCellFormat(201);

        //get the last 7 days
        List<YearMonthDay> last7days = Engine.getLastdays(6);
        // 2-8 id is the first row insert Date
        for (int i = 1; i < 8; i++) {
            changeCellText(i, last7days.get(i - 1).toString());
            changeCellFormat(i);
        }

        //label 10 ...240 hour column
        int j = 0;
        for(int i = 10; i <=240;  i = i+10){
            int startTime = j++;
            int endTime = startTime + 1;
            changeCellText(i,  startTime + " to " + endTime) ;
            changeCellFormat(i);
        }

        //get all Cal Items from the last 7days
        List<DataItem> dataItemListLast7days = new ArrayList<>();
        dataItemListLast7days = Engine.getDataItemNewerThanSorted(6);

        if(dataItemListLast7days == null || dataItemListLast7days.isEmpty()) {
         return;
        }
        int dayCalBurn = Engine.calcCalNeed(user);
        //get current day
        Calendar calendar = Calendar.getInstance();
        int currentComparisonDay = calendar.get(Calendar.DAY_OF_MONTH);

        //make a treeset to store unicq ids with cal intake
        Set<Integer> setCalIntakeCellId = new TreeSet<>();

        int dayColumn = 7;
        //set all cal intake in table
        //safe time slots and change color
        for(DataItem i : dataItemListLast7days){
            //set the date to the current selected item
            while(currentComparisonDay != i.getDay()) {
                //susbstract one day from calander
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
                currentComparisonDay = calendar.get(Calendar.DAY_OF_MONTH);
                dayColumn--;
            }
            int id =(i.getHour() * 10) + dayColumn;
            int cellCalCount = getCellCalCount(id);
            if(cellCalCount > 0){
                cellCalCount = cellCalCount+ i.getmCal();
            } else {
                cellCalCount = i.getmCal();
            }
            changeCellColor(id, Engine.interpolateColor(Color.GREEN, Color.RED,0, dayCalBurn, cellCalCount));
            changeCellText(id,cellCalCount + "");
            //save id in treeset to calculate the hours with no food maybe take real food intake
            setCalIntakeCellId.add(id);
            Log.i("TEST", id + " next " + getNextCellId(id));
        }

        while(setCalIntakeCellId.iterator().hasNext()){
            Log.i("TREESET", setCalIntakeCellId.iterator().next() +"");
            setCalIntakeCellId.remove(setCalIntakeCellId.iterator().next());
        }
        //go through all cells and the id list and change color based on 16 + 8
    }
    private int getCellCalCount(int id) {
        try {
            TextView cell = findViewById(id);
            String temp = (String) cell.getText();
            if(temp.compareTo("") == 0){
                return 0;
            }
            return Integer.parseInt(temp);
        } catch (Exception e) {
            Log.e("GETCELLCALCOUNT", "cell not found or no value");
            return 0;
        }
    }
    private void changeCellText(int id, String text) {
        //check if id is in range of the table 25 * 8 and at least 1

        try {
            TextView cell = findViewById(id);
            cell.setText(text);
        } catch (Exception e) {
            Log.e("CHANGECELLTEXT", "cell not found");
        }
    }
    private void changeCellFormat(int id){

        try {
            TextView cell = findViewById(id);
            cell.setBackgroundResource(R.drawable.black_boarder_gray_background);
            cell.setTypeface(null, Typeface.BOLD);
        } catch (Exception e) {
            Log.e("CHANGECELLFORMAT", "cell not found");
        }
    }
    private void changeCellColor(int id, int color){
        try {
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.black_boarder_red_background, getTheme());
            GradientDrawable shape = (GradientDrawable) drawable;
            shape.setColor(color);
            TextView cell = findViewById(id);
            cell.setBackground(shape);
        } catch (Resources.NotFoundException e) {
            Log.e("CHANGECELLCOLOR", "cell not found");
        }
    }
    private int getNextCellId(int startId){
        int rowCount = 25;
        int columnCount = 8;

        int row = startId / 10;
        int column = startId % 10;

        //column is 6 or bigger we have to go one row up and set column back to start
        if(column >= 6){
            row++;
            column = 1;
        } else {
            //we can add one to starId and return it
            return ++startId;
        }
        return (row*10 + column);
    }
    private int getLastCellId(int startId){
        return 0;
    }
}
