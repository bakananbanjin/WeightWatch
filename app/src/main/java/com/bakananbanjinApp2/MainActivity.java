package com.bakananbanjinApp2;

import static android.util.Log.DEBUG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.Menu;
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

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //tempvariable for linechart from git
    LineChart lineChart;


    //1.Ersetze Name mit dem vom user eingegebenen Namen aus DB oder anderer Quelle
    //2.Toolbar Textcolor dynamisch anpassen oder eigenen Theme schreiben
    //3.Icon fuer toolbar anpassen
    //4.Filewriter fertig programmieren
    //5.Make own Graph Class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);


        TextView textViewToolbar = findViewById(R.id.toolbar_textview);
        textViewToolbar.setText(getString(R.string.tollbar_welcome) + " Name");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment frag = fragmentManager.findFragmentById(R.id.fragment_container);

        //insert Fragment for information overview
        if(frag == null){
            frag = new Overview();
            fragmentManager.beginTransaction().add(R.id.fragment_container, frag).commit();
        }
        //insert Fragment for graphs and table
        tempSetLineChart();

        //DataItem test later to be deleted

        DataSet dataSet = new DataSet(this.getApplicationContext());
        dataSet.query();

        //FAB
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertDialog insertDialog = new InsertDialog();
                insertDialog.show(getSupportFragmentManager(),"");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menue_main, menu);
        return true;
    }

    //Temp function to test Linechart from git
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
