package com.bakananbanjinApp2;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DayPlanner {
    private static TableLayout dayPlanner;

    public static TableLayout generateTable(Context context) {
        dayPlanner = new TableLayout(context);
        //24 hours + header
        int numRows = 25;
        //7days + time
        int numCols = 8;
        //id for all cells beginning at 1 unique in tablelayout
        int cellId = 0;
        for (int i = 0; i < numRows; i++) {
            // Create a new TableRow for each row
            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT
            ));

            for (int j = 0; j < numCols; j++) {
                // Create a new TextView for each cell
                TextView cell = new TextView(context);
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

        changeCellText(201, context.getString(R.string.planner_time_date), dayPlanner);
        changeCellFormat(201, dayPlanner);

        //get the last 7 days
        List<YearMonthDay> last7days = Engine.getLastdays(6);
        // 2-8 id is the first row insert Date
        for (int i = 1; i < 8; i++) {
            changeCellText(i, last7days.get(i - 1).toString(), dayPlanner);
            changeCellFormat(i, dayPlanner);
        }

        //label 10 ...240 hour column
        int j = 0;
        for(int i = 10; i <=240;  i = i+10){
            int startTime = j++;
            int endTime = startTime + 1;
            changeCellText(i,  startTime + " to " + endTime, dayPlanner) ;
            changeCellFormat(i, dayPlanner);
        }

        //get all Cal Items from the last 7days
        List<DataItem> dataItemListLast7days = new ArrayList<>();
        dataItemListLast7days = Engine.getDataItemNewerThanSorted(6);

        if(dataItemListLast7days == null || dataItemListLast7days.isEmpty()) {
            return null;
        }
        int dayCalBurn = Engine.calcCalNeed(MainActivity.user);
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
            int cellCalCount = getCellCalCount(id, dayPlanner);
            if(cellCalCount > 0){
                cellCalCount = cellCalCount+ i.getmCal();
            } else {
                cellCalCount = i.getmCal();
            }
            changeCellColor(id, Engine.interpolateColor(Color.GREEN, Color.RED,0, dayCalBurn, cellCalCount), dayPlanner, context);
            changeCellText(id,cellCalCount + "", dayPlanner);
            //save id in treeset to calculate the hours with no food maybe take real food intake
            setCalIntakeCellId.add(id);
            Log.i("TEST", id + " next " + getNextCellId(id));
        }

        while(setCalIntakeCellId.iterator().hasNext()){
            Log.i("TREESET", setCalIntakeCellId.iterator().next() +"");
            setCalIntakeCellId.remove(setCalIntakeCellId.iterator().next());
        }
        //go through all cells and the id list and change color based on 16 + 8

        return dayPlanner;
    }
    private static int getCellCalCount(int id, View view) {
        try {
            TextView cell = view.findViewById(id);
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
    private static void changeCellText(int id, String text, View view) {
        //check if id is in range of the table 25 * 8 and at least 1

        try {
            TextView cell = view.findViewById(id);
            cell.setText(text);
        } catch (Exception e) {
            Log.e("CHANGECELLTEXT", "cell not found");
        }
    }
    private static void changeCellFormat(int id, View view){

        try {
            TextView cell = view.findViewById(id);
            cell.setBackgroundResource(R.drawable.black_boarder_gray_background);
            cell.setTypeface(null, Typeface.BOLD);
        } catch (Exception e) {
            Log.e("CHANGECELLFORMAT", "cell not found");
        }
    }
    private static void changeCellColor(int id, int color, View view, Context context){
        try {
            Resources res = view.getResources();
            Drawable drawable = res.getDrawable(R.drawable.black_boarder_red_background, context.getTheme());
            GradientDrawable shape = (GradientDrawable) drawable;
            shape.setColor(color);
            TextView cell = view.findViewById(id);
            cell.setBackground(shape);
        } catch (Resources.NotFoundException e) {
            Log.e("CHANGECELLCOLOR", "cell not found");
        }
    }
    private static int getNextCellId(int startId){
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
