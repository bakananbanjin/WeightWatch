package com.bakananbanjinApp2;

import android.content.Context;
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

    private static BufferedReader br;
    private static String line;
    private static String split;

    public DataReaderWriter() {

    }
    //read Database from file
    public static List<DataItem> readFileData(String file, Context context){

        //Temp Root direction maybe replaced later
        List<String> fileLines = new ArrayList<>();
        List<DataItem> dataItemList = new ArrayList<>();

        String folderName = "Data";
        File file12 = new File(context.getFilesDir() + File.separator + folderName, "test");
            try {
                FileInputStream fis = new FileInputStream(file12);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);

                String line;
                while((line = br.readLine()) != null){
                    Log.i("LINE", line);
                    fileLines.add(line);
                }
            } catch (FileNotFoundException e) {
                   Log.i("FILE NOT FOUND", "Reader could not find File");
            } catch (IOException e) {
                    e.printStackTrace();
            }
            //Dataset has to be in one line and sepereted by ;
            for(String i : fileLines) {
                    String[] tempString = i.split(";");
                    //if String length is correct create new DataItem and pass add it to List
                    if(tempString.length == 8) {
                        DataItem tempDataItem =
                                new DataItem(tempString[1], Integer.parseInt(tempString[2]),
                                        Integer.parseInt(tempString[3]), Integer.parseInt(tempString[4]),
                                        Integer.parseInt(tempString[5]), Integer.parseInt(tempString[6]),
                                        Integer.parseInt(tempString[5]));
                        //item toSting to check delete at relase
                        tempDataItem.toString();
                        dataItemList.add(tempDataItem);
                    } else {
                        Log.i("READ ERROR", "Read dataItem wrong length");
                    }
            }

        return dataItemList;
    }


    public boolean writeFileData(String file, Context context, List<DataItem> dataItemList){

        //get Applicatin storage
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
}
