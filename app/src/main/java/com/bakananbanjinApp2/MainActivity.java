package com.bakananbanjinApp2;

import static android.util.Log.DEBUG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.components.AxisBase;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //tempvariable for linechart from git
    LineChart lineChart;
    public static SharedPreferences mPrefs;
    public static SharedPreferences.Editor mEditor;
    private User user;




    //1.Ersetze Name mit dem vom user eingegebenen Namen aus DB oder anderer Quelle
    //2.Toolbar Textcolor dynamisch anpassen oder eigenen Theme schreiben
    //3.Icon fuer toolbar anpassen
    //4. Make own Graph Class

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
        TextView textViewToolbar = findViewById(R.id.toolbar_textview);
        textViewToolbar.setText(getString(R.string.tollbar_welcome) + " Name");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //initialise Fragment for information overview
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment frag = fragmentManager.findFragmentById(R.id.fragment_container);
        if(frag == null){
            frag = new Overview();
            fragmentManager.beginTransaction().add(R.id.fragment_container, frag).commit();
        }
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               InsertDialog insertDialog = new InsertDialog();
               insertDialog.show(getSupportFragmentManager(),"");
            }
        });

        //if no user is present ask to create one
        if(!mPrefs.contains("user")){
            CreateUser createUserDialog = new CreateUser();
            createUserDialog.show(getSupportFragmentManager(), "");

        }
        try {
           user = new User(mPrefs.getString("user", "user"),
                   mPrefs.getInt("weight", 50),
                   mPrefs.getInt("height", 170),
                   mPrefs.getInt("age", 30),
                   mPrefs.getInt("targetWeight", 0));
        } catch (Exception e){
            Log.e("NOUSER", "no user found");
        }


        /*
        +
        + TEST CODE ONLY BELOW
        +
         */

        //insert Fragment for graphs and table will be deleted later
        tempSetLineChart();

        //Testclass needs to be deleted

        DataSet dataSet = new DataSet(this.getApplicationContext());
        dataSet.query();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menue_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int selectedId = item.getItemId();
        if(selectedId == R.id.menu_Profil){
            Log.i("MENU", "Profil selected");
            /*
            +
            +TEST Layout
            +
             */
            CreateUser createUser = new CreateUser();
            createUser.show(getSupportFragmentManager(),"");
            return true;
        } else if (selectedId == R.id.menu_Info) {
            Log.i("MENU", "Info selected");
            return true;
        } else if (selectedId == R.id.menu_Edit) {
            Log.i("MENU", "Edit selected");
            return true;
        } else if (selectedId == R.id.menu_Backup) {
            Log.i("MENU", "Backup selected");
            return true;
        } else if (selectedId == R.id.menu_Delete) {
            Engine.deleteData();
            Engine.getPref();
            Log.i("MENU", "Delete selected");
            return true;
        }
        return false;
    }



    /*
    +
    +  TEST CODE ONLY BELOW
    +
     */

    //Temp function to test Linechart from git should be deleted or moved
    private void addDataEntry(float value) {
        LineData lineData = lineChart.getData();

        if (lineData != null) {
            LineDataSet lineDataSet = (LineDataSet) lineData.getDataSetByIndex(0);
            if (lineDataSet == null) {
                lineDataSet = new LineDataSet(null, "Weight");
                lineData.addDataSet(lineDataSet);
            }

            Entry entry = new Entry(lineDataSet.getEntryCount(), value);
            lineDataSet.setLineWidth(5f);
            lineData.addEntry(entry, 0);
            lineData.notifyDataChanged();
        }
    }
    private class CustomXAxisValueFormatter extends ValueFormatter {
        private final List<String> labels;

        public CustomXAxisValueFormatter(List<String> labels) {
            this.labels = labels;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int index = (int) value;
            if (index >= 0 && index < labels.size()) {
                return labels.get(index);
            }
            return "";
        }
    }
    //needs to be deleted on finishing progarmm
    private void tempSetLineChart()
    {
        lineChart = findViewById(R.id.line_chart);
        LineData lineData = new LineData();

        lineChart.setData(lineData);

        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(true);



        YAxis  yAxis = lineChart.getAxisLeft();
        yAxis.setTextSize(12f);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setAxisMaximum(90f);
        yAxis.setAxisMinimum(80f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        List<String> customLabels = new ArrayList<>();
        customLabels.add("Mo 01/12");
        customLabels.add("Tue 02/12");
        customLabels.add("Wed 03/12");
        customLabels.add("Thu 04/12");
        customLabels.add("Fri 05/12");
        customLabels.add("Sat 06/12");
        customLabels.add("Sun 07/12");


        xAxis.setValueFormatter(new CustomXAxisValueFormatter(customLabels));


        Legend legend = lineChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        addDataEntry(85f);
        addDataEntry(83f);
        addDataEntry(82f);
        addDataEntry(84f);
        addDataEntry(85f);
        addDataEntry(83f);
        addDataEntry(82f);
        addDataEntry(84f);

        lineChart.invalidate();
    }
}
