package com.bakananbanjinApp2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.icu.text.DecimalFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Graph extends View {
    private static final float YAXISLABELSETPS = 10f;
    private static final int NUMBEROFLABELS = 10;
    private static float XAXISOFSET = 40f;
    private static float YAXISOFSET = 30F;
    private static float YAXISSPACEBOT = 1f;
    private static float YAXISSPACETOP = 0.5f;

    private float yAxisLabelDifference = 1f;
    private List<Float> xValues;
    private List<Float> yValues;
    private List<Integer> xAxisValues;
    private List<Float> yAxisValues;
    private List<Integer> columnValues;
    private int columnBase;
    private int lineColor;
    private float lineWidth;
    private Paint axisPaint;
    private Paint labelPaint;
    private Paint guidelinePaint;
    private Paint columnPaint;
    private Paint borderPaint;


    public Graph(Context context) {
        super(context);
        init();
    }
    public Graph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        // Initialize member variables and perform any setup tasks
        xValues = new ArrayList<>();
        yValues = new ArrayList<>();

        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        columnValues = new ArrayList<>();

        lineColor = Color.BLUE;
        lineWidth = 4f;

        // Initialize Paint objects for axes and labels
        axisPaint = new Paint();
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(2f);

        labelPaint = new Paint();
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(16f);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        guidelinePaint = new Paint();
        guidelinePaint.setColor(Color.GRAY);
        guidelinePaint.setAlpha(50);

        columnPaint = new Paint();
        columnPaint.setColor(Color.GREEN);
        columnPaint.setAlpha(100);

        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);


    }
    public void setData(List <Integer> xAxisValues, List<Float> xValues, List<Float> yValues, List<Integer> columnValues, int columnBaseValue) {
        this.xAxisValues = xAxisValues;
        this.yAxisValues = calcyAxis(yValues);

        this.columnValues = columnValues;
        this.columnBase = columnBaseValue;

        this.xValues = xValues;
        this.yValues = calcyValues(yValues);

        invalidate(); // Trigger redraw of the view
    }
    private List<Float> calcyValues(List<Float> yValues) {
        //go through list and adjust values for new yaxis
        float yMin = yAxisValues.get(0);
        List <Float> transformedYValues = new ArrayList<>();
        for(int i = 0; i < yValues.size(); i++){
            transformedYValues.add(yValues.get(i) - yMin);
        }
        return transformedYValues;
    }
    private List<Float> calcyAxis(List<Float> weightList){
        //how many steps should have yAxis
        float steps = YAXISLABELSETPS;
        //yaxis value label list:
        List<Float> yAxis = new ArrayList<>();
        //get max and min Value for yAxsis
        if (weightList == null || weightList.isEmpty()) {
            // Handle empty list case
            return null;
        }
        float maxValue = weightList.get(0);
        float minValue = weightList.get(0);

        for (float value : weightList) {
            if (value > maxValue) {
                maxValue = value;
            }
            if (value < minValue) {
                minValue = value;
            }
        }

        //yAxis Startpoint minVale - 2 kg (can be adjusted)
        float startValue = minValue - YAXISSPACEBOT;
        //yAxis endvalue maxVale + 2 kg
        float endValue = maxValue + YAXISSPACETOP;
        //yAxis range
        float differenceStartEnd = endValue - startValue;
        //yAxis step
        yAxisLabelDifference = differenceStartEnd / steps;
        //generate yAxis label List
        for(int i = 0; i < steps; i++){
            float addValue = startValue + (i * yAxisLabelDifference);
            yAxis.add(addValue);
        }

        return yAxis;
    }
    public void setLineColor(int color) {
        lineColor = color;
        invalidate();
    }
    public void setLineWidth(float width) {
        lineWidth = width;
        invalidate();
    }
    private String floatToString(float value){
        DecimalFormat decimalFormat = new DecimalFormat("#.1");
        return decimalFormat.format(value);
    }
    // Method to interpolate color based on value within a range
    private int interpolateColor(int startColor, int endColor, float minValue, float maxValue, float value) {
        float ratio = (value - minValue) / (maxValue - minValue);
        int red = (int) (Color.red(startColor) * (1 - ratio) + Color.red(endColor) * ratio);
        int green = (int) (Color.green(startColor) * (1 - ratio) + Color.green(endColor) * ratio);
        int blue = (int) (Color.blue(startColor) * (1 - ratio) + Color.blue(endColor) * ratio);
        return Color.rgb(red, green, blue);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //if no Grid data is avaible return
        if(yAxisValues.size() == 0 || xAxisValues.size() == 0){
            return;
        }

        drawGrid(canvas);
        drawLineGraph(canvas);
        drawColumns(canvas);
    }
    private void drawGrid(Canvas canvas){
        //set yAxis max and min Values
        float maxYValue = yAxisValues.get(yAxisValues.size() - 1);
        float minYValue = yAxisValues.get(0);
        // Set the desired number of labels for now fixed soon to be changed
        int numOfLabels = NUMBEROFLABELS;
        //how far from label to label
        float yStepLabel = (getHeight() - (YAXISOFSET * 2)) / (numOfLabels);
        float xStepLabel = (getWidth() - (XAXISOFSET * 2)) / (xAxisValues.size() - 1);

        // Set the number of guidelines should be the same number as labels
        int numOfHorizontalGuidelines = NUMBEROFLABELS;
        int numOfVerticalGuidelines = NUMBEROFLABELS;

        // Draw x and y axes
        float startX = XAXISOFSET;
        float startY = getHeight() - YAXISOFSET;
        float endX = getWidth() - XAXISOFSET;
        float endY = getHeight() - YAXISOFSET;
        canvas.drawLine(startX, startY, endX, endY, axisPaint); // x-axis
        canvas.drawLine(startX, startY, startX, YAXISOFSET, axisPaint); // y-axis

        // Draw arrowheads at the ends of the axes
        float arrowSize = 20f; // Set the size of the arrowhead

        // Draw arrowhead for x-axis
        Path xArrowPath = new Path();
        xArrowPath.moveTo(endX - arrowSize, startY - arrowSize / 2f);
        xArrowPath.lineTo(endX, startY);
        xArrowPath.lineTo(endX - arrowSize, startY + arrowSize / 2f);
        canvas.drawPath(xArrowPath, axisPaint);

        // Draw arrowhead for y-axis
        Path yArrowPath = new Path();
        yArrowPath.moveTo(startX - arrowSize / 2f, YAXISOFSET + arrowSize);
        yArrowPath.lineTo(startX, YAXISOFSET);
        yArrowPath.lineTo(startX + arrowSize / 2f, YAXISOFSET + arrowSize);
        canvas.drawPath(yArrowPath, axisPaint);

        // Draw horizontal guidelines
        for (int i = 0; i < numOfHorizontalGuidelines; i++) {
            float yPosition = (getHeight() - YAXISOFSET )  - (i * yStepLabel);
            canvas.drawLine(XAXISOFSET, yPosition, getWidth() - XAXISOFSET,
                    yPosition, guidelinePaint);
        }
        // Draw vertical guidelines
        for (int i = 0; i < numOfVerticalGuidelines; i++) {
            float xPosition = XAXISOFSET + i * xStepLabel;
            canvas.drawLine(xPosition, YAXISOFSET, xPosition,
                    getHeight()-YAXISOFSET, guidelinePaint);
        }

        for (int i = 0; i < xAxisValues.size(); i++) {
            float xPos = XAXISOFSET + (i * xStepLabel);
            float yPos = getHeight() ; // Adjust label position below x-axis
            Integer temp =  xAxisValues.get(i);
            String label = String.valueOf(temp);
            canvas.drawText(label, xPos, yPos, labelPaint);
        }

        // Draw labels on y-axis
        float yValueStep = (maxYValue - minYValue) / (numOfLabels - 1);
        for (int i = 1; i < numOfLabels; i++) {
            float xPos = 20f; // Adjust the x-position of the label
            float yPos = (getHeight() - YAXISOFSET) - (i * yStepLabel); // Adjust the y-position of the label
            float labelValue = minYValue + (i * yValueStep);
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String labelText = decimalFormat.format(labelValue); // Format the label value as desired
            canvas.drawText(labelText, xPos, yPos, labelPaint);
        }
    }
    private void drawLineGraph(Canvas canvas){
        // Draw line graph
        Paint linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineWidth);

        //xAxis values
        float xStep = (getWidth() - (XAXISOFSET * 2)) / (xAxisValues.size() - 1);
        //yAxis Value must be adjusted the step size. what is the value between 2 labels
        float yStep = ((getHeight() - (YAXISOFSET * 2)) / NUMBEROFLABELS) / yAxisLabelDifference;

        for (int i = 1; i < yValues.size(); i++) {
            float prevX = XAXISOFSET + (i - 1) * xStep;
            float prevY = (getHeight() - YAXISOFSET) - (yValues.get(i - 1) * yStep);
            float curX = XAXISOFSET + i * xStep;
            float curY = (getHeight() - YAXISOFSET) - (yValues.get(i) * yStep);

            canvas.drawLine(prevX, prevY, curX, curY, linePaint);

            // Draw the value text
            canvas.drawText(floatToString(yValues.get(i) + yAxisValues.get(0)), curX, curY - 10, labelPaint);
        }
    }
    private void drawColumns(Canvas canvas){
        // Set the number of columns based on input data
        int numOfColumns = columnValues.size();

        // Calculate the width of each column
        float columnWidth = (float) (getWidth() - (2 * XAXISOFSET)) / ((float)numOfColumns -  1);

        // Calculate the height ratio they should not go above the half of the graph y/4y1
        float heightRatioColumn = (getHeight() - (YAXISOFSET * 2))/ (columnBase * 4);

        // Draw the columns
        for (int i = 0; i < numOfColumns; i++) {
            // Calculate the color based on the value
            // from green to read
            int startColor = Color.GREEN;
            int endColor = Color.RED;
            // Minimum and Maximum value for the color range
            float minValue = columnBase / 2f;
            float maxValue = 1.5f * columnBase;

            int color = interpolateColor(startColor, endColor, minValue, maxValue, columnValues.get(i));

            // Set the color for the column
            columnPaint.setColor(color);

            float left = i * columnWidth + (XAXISOFSET - (columnWidth/2)) + (columnWidth/6);
            float top = (getHeight() - YAXISOFSET) - (columnValues.get(i) * heightRatioColumn);
            float right = (i + 1) * columnWidth + (XAXISOFSET - (columnWidth/2)) - (columnWidth/6);
            float bottom = (getHeight() - YAXISOFSET);
            canvas.drawRect(left, top, right, bottom, columnPaint);

            // Draw the black border around the column
            float borderWidth = 2f; // Set the width of the border
            float borderLeft = left + borderWidth / 2f;
            float borderTop = top + borderWidth / 2f;
            float borderRight = right - borderWidth / 2f;
            float borderBottom = bottom - borderWidth / 2f;
            borderPaint.setStrokeWidth(borderWidth);
            borderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(borderLeft, borderTop, borderRight, borderBottom, borderPaint);

            // Draw the label on top of the column
            String label = String.format(Locale.getDefault(), "%.0f %s", (float)columnValues.get(i), "cal");
            float labelX = (left + right) / 2f;
            float labelY = top - 10f;
            canvas.drawText(label, labelX, labelY, labelPaint);
        }
    }

}
