package com.bakananbanjinApp2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.icu.text.DecimalFormat;
import android.util.AttributeSet;
;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Graph extends View {

    //textsize will be later calculated as % of canvas size
    private float TEXTSIZETITLE = 36f;
    private float TEXTSIZELARGE = 24f;
    private float TEXTSIZENORMAL = 16f;
    private float TEXTSIZESMALL = 12f;

    private static final float YAXISLABELSETPS = 10f;
    private static final int NUMBEROFLABELS = 10;
    private static final int FIRSTCOLUMN = 1;
    private static final int LEGENDWIDTH = 175;
    private static final int LEGENDEHEIGHT = 75;
    private static final int MINYVALUE = 30;
    private static final int XAXISLABELSTEPS = 7;
    private static float XAXISOFSET = 60f;
    private static float YAXISOFSET = 30f;
    private static float YAXISSPACEBOT = 2.00f;
    private static float YAXISSPACETOP = 2.00f;
    private float yAxisLabelDifference = 1f;
    private float yAxisMinValue;
    private List<Float> yValues;
    private List<String> xAxisValues;
    private List<Float> yAxisValues;
    private List<Float> columnValues;
    private List<Double> xTrendLine;
    private List<Double> yTrendLine;
    private int columnBase;
    private int lineColor;
    private float lineWidth;
    private Paint axisPaint;
    private Paint labelPaint;
    private Paint guidelinePaint;
    private Paint columnPaint;
    private Paint borderPaint;
    private Paint dottedLinePaint;
    private Paint linePaint;
    private Paint legendPaint;
    private Paint titlePaint;
    private Paint legendLabelPaint;
    private boolean paintColumnLabel = true;

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
        yValues = new ArrayList<>();

        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        columnValues = new ArrayList<>();

        xTrendLine = new ArrayList<>();
        yTrendLine = new ArrayList<>();

        lineColor = Color.BLUE;
        lineWidth = 4f;

        // Initialize Paint objects for axes and labels
        axisPaint = new Paint();
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(2f);

        labelPaint = new Paint();
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        legendLabelPaint = new Paint();
        legendLabelPaint.setColor(Color.BLACK);
        legendLabelPaint.setTextAlign(Paint.Align.LEFT);

        titlePaint = new Paint();
        titlePaint.setColor(Color.BLACK);

        guidelinePaint = new Paint();
        guidelinePaint.setColor(Color.GRAY);
        guidelinePaint.setAlpha(50);

        columnPaint = new Paint();
        columnPaint.setColor(Color.GREEN);
        columnPaint.setAlpha(100);

        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);

        dottedLinePaint = new Paint();
        dottedLinePaint.setColor(Color.RED);
        dottedLinePaint.setStyle(Paint.Style.STROKE);
        dottedLinePaint.setStrokeWidth(4);
        dottedLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));

        legendPaint = new Paint();
        legendPaint.setColor(Color.WHITE);

        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineWidth);

    }
    public void setData(List <String> xAxisValues, List<Float> yValue, List<Float> columnValues, int columnBaseValue) {
        this.xAxisValues = xAxisValues;
        this.yAxisValues = calcyAxis(yValue);
        this.columnValues = columnValues;
        this.columnBase = columnBaseValue;
        this.yValues = yValue;

        invalidate(); // Trigger redraw of the view
    }
    private List<Float> calcyAxis(List<Float> weightList){
        //how many steps should have yAxis
        float steps = YAXISLABELSETPS;
        //yaxis value label list:
        List<Float> yAxis = new ArrayList<>();
        //get max and min Value for yAxsis
        if (weightList == null || weightList.isEmpty()) {
            // Handle empty list case

            for(int i = 0; i < steps; i++){
                yAxis.add((float)i);
            }
            return yAxis;
        }
        float maxValue = getMaxFromList(weightList);
        float minValue = getMinFromList(weightList);

        //yAxis Startpoint minVale min weight min yaxis
        if(minValue < MINYVALUE){
            yAxisMinValue = maxValue * 0.9f;
        } else {
            yAxisMinValue = minValue - YAXISSPACEBOT;
        }
        //yAxis endvalue maxVale
        float endValue = maxValue + YAXISSPACETOP;
        //yAxis range
        float differenceStartEnd = endValue - yAxisMinValue;
        //yAxis step
        yAxisLabelDifference = differenceStartEnd / steps;
        //generate yAxis label List
        for(int i = 0; i < steps; i++){
            float addValue = yAxisMinValue + (i * yAxisLabelDifference);
            yAxis.add(addValue);
        }
        return yAxis;
    }
    private String floatToString(float value){
        DecimalFormat decimalFormat = new DecimalFormat("#.1");
        return decimalFormat.format(value);
    }
    private float getMinFromList(List<Float> yValues) {
        //not efficient but for now its ok
        float min = getMaxFromList(yValues);
        for (float value : yValues) {
            if (value < min && value > 1f) {
                min = value;
            }
        }
        return min;
    }
    private float getMaxFromList(List<Float> yValues) {
        float max = 1f;
        for (float value : yValues) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        TEXTSIZELARGE = getTextSizeAsPercentageOfWidth(canvas, 3.0f);
        TEXTSIZETITLE = getTextSizeAsPercentageOfWidth(canvas, 5f);
        TEXTSIZENORMAL = getTextSizeAsPercentageOfWidth(canvas, 2.0f);
        TEXTSIZESMALL = getTextSizeAsPercentageOfWidth(canvas, 1.0f);
        titlePaint.setTextSize(TEXTSIZETITLE);
        labelPaint.setTextSize(TEXTSIZELARGE);
        legendLabelPaint.setTextSize(TEXTSIZENORMAL);

        //if no Grid data is avaible return
        if(yAxisValues.size() == 0 || xAxisValues.size() == 0){
            return;
        }
        drawGrid(canvas);
        drawColumns(canvas);
        drawLineGraph(canvas);
        drawLegend(canvas);
        drawTitle(canvas);

    }
    private void drawGrid(Canvas canvas){

        // Set the desired number of labels for now fixed soon to be changed Y-Axis
        int numOfLabels = NUMBEROFLABELS;
        //how far from label to label
        float yStepLabel = (getHeight() - (YAXISOFSET * 2)) / (numOfLabels);
        //switch on value for how many days are displayed for now static from MainActivity
        int xAxisLabelSteps = XAXISLABELSTEPS;

        float xStepLabel = (getWidth() - (XAXISOFSET * 2)) / (xAxisLabelSteps);
        float xStepGuidline = (getWidth() - (XAXISOFSET * 2)) / (xAxisValues.size() - 1);

        // Set the number of guidelines should be the same number as labels
        int numOfHorizontalGuidelines = NUMBEROFLABELS;
        int numOfVerticalGuidelines = xAxisValues.size();

        // Draw x and y axes
        float startX = XAXISOFSET;
        float startY = getHeight() - YAXISOFSET;
        float endX = getWidth() - (XAXISOFSET/2);
        float endY = getHeight() - YAXISOFSET;
        canvas.drawLine(startX, startY, endX, endY, axisPaint); // x-axis
        canvas.drawLine(startX, startY, startX, YAXISOFSET, axisPaint); // y-axis

        // Draw arrowheads at the ends of the axes
        float arrowSize = 20f; // Set the size of the arrowhead

        // Draw arrowhead for x-axis
        Path xArrowPath = new Path();
        xArrowPath.moveTo(endX, startY - arrowSize / 2f);
        xArrowPath.lineTo(endX + arrowSize, startY);
        xArrowPath.lineTo(endX, startY + arrowSize / 2f);
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
            float xPosition = XAXISOFSET + i * xStepGuidline;
            canvas.drawLine(xPosition, YAXISOFSET, xPosition,
                    getHeight()-YAXISOFSET, guidelinePaint);
        }

        // Draw labels x-axis
        int labelXStepFromDay = MainActivity.DIAGRAMMDAYS / 7;
        labelPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i <= xAxisLabelSteps; i++) {
            float xPos = XAXISOFSET + (i * xStepLabel);
            float yPos = getHeight() ;
            String label = xAxisValues.get(i*labelXStepFromDay);
            canvas.drawText(label, xPos, yPos, labelPaint);
        }

        // Draw labels on y-axis
        //set yAxis max and min Values
        labelPaint.setTextAlign(Paint.Align.LEFT);
        float maxYValue = yAxisValues.get(yAxisValues.size()-1);

        float yValueStep = (maxYValue - yAxisMinValue) / (numOfLabels - 1);
        for (int i = 1; i < numOfLabels; i++) {
            float xPos = 0; // Adjust the x-position of the label
            float yPos = (getHeight() - YAXISOFSET) - (i * yStepLabel); // Adjust the y-position of the label
            float labelValue = yAxisMinValue + (i * yValueStep);
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String labelText = decimalFormat.format(labelValue); // Format the label value as desired
            canvas.drawText(labelText, xPos, yPos, labelPaint);
        }
        labelPaint.setTextAlign(Paint.Align.CENTER);

    }
    private void drawLineGraph(Canvas canvas){
        // Draw line graph
        Paint linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineWidth);

        //xAxis values
        float xStep = (getWidth() - (XAXISOFSET * 2)) / (xAxisValues.size() - 1);
        //yAxis Value must be adjusted the step size. what is the value between 2 labels
        float yStep = ((float)(getHeight() - (YAXISOFSET * 2)) / (float)NUMBEROFLABELS) / yAxisLabelDifference;
        for (int i = 1; i < yValues.size(); i++) {
            if(true) {
                float prevX = XAXISOFSET + (i - 1) * xStep;
                float prevY = getHeight() - YAXISOFSET;
                if(yValues.get(i-1) > MINYVALUE) {
                    prevY = prevY - ((yValues.get(i - 1) - yAxisMinValue) * yStep);
                }
                float curX = XAXISOFSET + i * xStep;
                float curY = (getHeight() - YAXISOFSET);
                if(yValues.get(i) > MINYVALUE) {
                    curY = (getHeight() - YAXISOFSET) - ((yValues.get(i) - yAxisMinValue) * yStep);
                }

                canvas.drawLine(prevX, prevY, curX, curY, linePaint);

                // Draw the value text
                canvas.drawText(floatToString(yValues.get(i) ), curX, curY - 10, labelPaint);
            }
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
        for (int i = FIRSTCOLUMN; i < numOfColumns; i++) {
            // Calculate the color based on the value
            // from green to read
            int startColor = Color.GREEN;
            int endColor = Color.RED;
            // Minimum and Maximum value for the color range
            float minValue = columnBase / 2f;
            float maxValue = 1.5f * columnBase;

            int color = Engine.interpolateColor(startColor, endColor, minValue, maxValue, columnValues.get(i));

            // Set the color for the column
            columnPaint.setColor(color);
            columnPaint.setAlpha(75);

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
            if(paintColumnLabel) {
                String label = String.format(Locale.getDefault(), "%.0f %s", (float) columnValues.get(i), "cal");
                float labelX = (left + right) / 2f;
                float labelY = top - 10f;
                canvas.drawText(label, labelX, labelY, labelPaint);
            }
        }
        //day calorie burn line
        float x1 = XAXISOFSET;
        float y1 = (getHeight() - YAXISOFSET) - (columnBase * heightRatioColumn);
        float x2 = getWidth() - XAXISOFSET;
        float y2 = y1;
        canvas.drawLine(x1, y1, x2, y2, dottedLinePaint);
    }
    private void drawLegend(Canvas canvas)  {

        String labelWeight = getResources().getString(R.string.graph_legend_weight);
        String labelCalBurn = getResources().getString(R.string.graph_legend_base_cal);

        int lineSpaceTopBot = 25;
        int symbolLength = 20;
        int textCorrectionX =  symbolLength;
        int textCorrectionY =  lineSpaceTopBot;

        //calculate legend position
        float xStart = getWidth() - LEGENDWIDTH - XAXISOFSET;
        float yStart = 0f;
        float xEnd = xStart + LEGENDWIDTH;
        float yEnd = yStart + LEGENDEHEIGHT;

        //draw withe rectangle behind the label
        //canvas.drawRect(xStart, yStart, xEnd, yEnd, legendPaint);

        //draw symbols and labels
        canvas.drawLine(xStart, yStart + lineSpaceTopBot, xStart + symbolLength, yStart + lineSpaceTopBot, linePaint);
        canvas.drawLine(xStart, yStart + lineSpaceTopBot * 2, xStart + symbolLength, yStart + lineSpaceTopBot * 2, dottedLinePaint);
        canvas.drawText(labelWeight, xStart + textCorrectionX, yStart  + textCorrectionY + 5, legendLabelPaint);
        canvas.drawText(labelCalBurn, xStart + textCorrectionX, yStart  + textCorrectionY * 2 + 5, legendLabelPaint);

    }
    private void drawTitle(Canvas canvas){
        String title = getResources().getString(R.string.graph_title, MainActivity.DIAGRAMMDAYS);

        //calculate title position
        float xStart = XAXISOFSET + 10f;
        float yStart = YAXISOFSET + TEXTSIZETITLE/ 2;

        //draw symbols and labels
        canvas.drawText(title, xStart, yStart, titlePaint);

    }
    public void setLabelVisible(boolean columnLabel){
        this.paintColumnLabel = columnLabel;

    }
    public Bitmap createBitmapFromCanvas() {

        // Get the default display
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        // Get the screen width in pixels
        int screenWidth = displayMetrics.widthPixels;
        // Create bitmap
        Bitmap bitmap = Bitmap.createBitmap(screenWidth, 1500, Bitmap.Config.ARGB_8888);
        // Create a new canvas with the bitmap
        Canvas bitmapCanvas = new Canvas(bitmap);

        if(yAxisValues.size() == 0 || xAxisValues.size() == 0){
            return null;
        }
        drawGrid(bitmapCanvas);
        drawColumns(bitmapCanvas);
        drawLineGraph(bitmapCanvas);
        drawLegend(bitmapCanvas);

        // Draw the original canvas onto the bitmap canvas
        bitmapCanvas.drawBitmap(bitmap, 0, 0, null);

        return bitmap;
    }
    public static float getTextSizeAsPercentageOfWidth(Canvas canvas, float percentage) {
        float canvasWidth = canvas.getWidth();
        return (canvasWidth * percentage) / 100f;
    }
}
