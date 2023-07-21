package com.bakananbanjinApp2;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DataReaderWriter {
    public static String FILENAME_USER = "user";
    public static String FILENAME_DATA = "data";
    public static String FILENAME_WEIGHT = "weight";
    public static String FOLDER_NAME = "Data";

    private static BufferedReader br;
    private static String line;
    private static String split;

    public DataReaderWriter() {

    }
    //read Database from file

    public static boolean writeFileWeight(String file, Context context, Cursor cursor) {
        File root = context.getFilesDir();
        File dir = new File(root + "/Data");
        dir.mkdir();

        File newFile = new File(dir, file);

        try {
            FileOutputStream f = new FileOutputStream(newFile);
            PrintWriter pw = new PrintWriter(f);

            //check if is data in cursor
            if(cursor != null || cursor.moveToFirst()) {
                for(int i = 0; i < cursor.getCount(); i++){
                    String writeString = "";
                    cursor.moveToPosition(i);
                    for (int j = 0; j < DataSetDB.TABELWEIGHT_ROW_SIZE; j++) {
                        writeString += cursor.getString(j) + ";";
                    }
                    writeString += "\n";
                    f.write(writeString.getBytes());
                }
            }
            //close writer
            f.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean writeFileData(String file, Context context, List<DataItem> dataItemList){

        //get Application storage
        //add own Folder Data
        //and create file
        File root = context.getFilesDir();
        File dir = new File(root + "/Data");
        dir.mkdir();

        File newFile = new File(dir, file);
        //Write file.
        try {
            FileOutputStream f = new FileOutputStream(newFile);
            PrintWriter pw = new PrintWriter(f);

            for(DataItem i: dataItemList){
                f.write(i.toStringtoFile().getBytes());
            }
            //close writer
            f.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean writeFileDB(String file, Context context,  Cursor cursor){
        File root = context.getFilesDir();
        File dir = new File(root + "/Data");
        dir.mkdir();

        File newFile = new File(dir, file);

        try {
            FileOutputStream f = new FileOutputStream(newFile);
            PrintWriter pw = new PrintWriter(f);

            if(cursor != null) {
                for (int j = 0; j < cursor.getCount(); j++) {
                    cursor.moveToPosition(j);
                    String writeString = "";
                    for (int i = 0; i < DataSetDB.TABEL_ROW_SIZE; i++) {
                        writeString += cursor.getString(i) + ";";
                    }
                    writeString += "\n";
                    f.write(writeString.getBytes());
                }
            }
            //close writer
            f.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean writeFileUser(String file, Context context,  User user){
        File root = context.getFilesDir();
        File dir = new File(root + "/Data");
        dir.mkdir();

        File newFile = new File(dir, file);

        try {
            FileOutputStream f = new FileOutputStream(newFile);
            f.write(user.toStringtoFile().getBytes());
            //close writer
            f.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public static boolean readAllFile(){

        return true;
    }
    public static List<DataItem> readFileData(Context context){

        List<String> fileLines = new ArrayList<>();
        List<DataItem> dataItemList = new ArrayList<>();

        File fileDataItem = new File(context.getFilesDir() + File.separator + FOLDER_NAME, FILENAME_DATA);
        try {
            FileInputStream fis = new FileInputStream(fileDataItem);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while((line = br.readLine()) != null){
                //Log.i("LINE", line);
                fileLines.add(line);
            }
        } catch (FileNotFoundException e) {
            Log.e("FILE NOT FOUND", "Reader could not find File");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Dataset has to be in one line and sepereted by ;
        for(String i : fileLines) {
            String[] tempString = i.split(";");
            //if String length is correct create new DataItem and pass add it to List
            if(tempString.length == DataSetDB.TABEL_ROW_SIZE) {
                DataItem tempDataItem =
                        new DataItem(tempString[1], Integer.parseInt(tempString[2]),
                                Integer.parseInt(tempString[3]), Integer.parseInt(tempString[4]),
                                Integer.parseInt(tempString[5]), Integer.parseInt(tempString[6]),
                                Integer.parseInt(tempString[7]));
                //item toSting to check delete at relase
                dataItemList.add(tempDataItem);
            } else {
                Log.e("READ ERROR", "Read dataItem wrong length");
            }
        }

        return dataItemList;
    }
    //basicly a copy from readFileItem
    public static List<Weight> readFileWeight(Context context){
        List<String> fileLines = new ArrayList<>();
        List<Weight> dataWeightList = new ArrayList<>();

        File fileDataItem = new File(context.getFilesDir() + File.separator + FOLDER_NAME, FILENAME_WEIGHT);
        try {
            FileInputStream fis = new FileInputStream(fileDataItem);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while((line = br.readLine()) != null){
                //Log.i("LINE", line);
                fileLines.add(line);
            }
        } catch (FileNotFoundException e) {
            Log.i("FILE NOT FOUND", "Reader could not find File");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String i : fileLines) {
            String[] tempString = i.split(";");
            //if String length is correct create new DataItem and pass add it to List
            if(tempString.length == DataSetDB.TABELWEIGHT_ROW_SIZE) {
                Weight tempDataWeight =
                        new Weight(Integer.parseInt(tempString[0]), Float.parseFloat(tempString[1]),
                                Integer.parseInt(tempString[2]), Integer.parseInt(tempString[3]),
                                Integer.parseInt(tempString[4]), Integer.parseInt(tempString[5]),
                                Integer.parseInt(tempString[6]));
                dataWeightList.add(tempDataWeight);
            } else {
                Log.i("READ ERROR", "Read dataItem wrong length");
            }
        }
        return dataWeightList;
    }
    public static User readFileUser(Context context){
        String lineUser;
        User user;

        File fileDataItem = new File(context.getFilesDir() + File.separator + FOLDER_NAME, FILENAME_USER);
        try {
            FileInputStream fis = new FileInputStream(fileDataItem);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            lineUser = br.readLine();
            //we only handle one user atm
            if(lineUser == null){
                Log.e("NOUSER", "no user or file Found");
                throw new FileNotFoundException();
            }
            String[] tempString = lineUser.split(";");
            user = new User(tempString[0], Boolean.parseBoolean(tempString[1]), Integer.parseInt(tempString[2]),
                    Integer.parseInt(tempString[3]), Float.parseFloat(tempString[4]), Integer.parseInt(tempString[5]),
                    Float.parseFloat(tempString[6]));
            return user;
        } catch (FileNotFoundException e) {
            Log.i("FILE NOT FOUND", "Reader could not find File");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
