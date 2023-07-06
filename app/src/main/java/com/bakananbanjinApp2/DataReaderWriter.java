package com.bakananbanjinApp2;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
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
    public static void readFileData(String file, Context context){
        List<String> fileLines = new ArrayList<String>();
            try {
                String line;
                br = new BufferedReader(new InputStreamReader(context.getAssets().open(file), "UTF-8"));
                while((line = br.readLine()) != null){
                    fileLines.add(line);
                }
            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }
            //Dataset has to be in one line and sepereted by ;
            for(int i = 0; i < fileLines.size(); i++) {
                    String[] tempString = fileLines.get(i).split(";");
            }
            /*Enhanced For loop same as above not in use atm
            for(String i : fileLines){
                String[] tempString = i.split(";");
            }
            */
    }
    //write Dataset as File need to add List Object in arguments
    public boolean writeFileData(String file, Context context){
        //get app root
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/Dataset");
        //make Folder Dataset if not present
        dir.mkdir();
        File newFile = new File(dir, file);
        //Write file.
        try {
            FileOutputStream f = new FileOutputStream(newFile);
            PrintWriter pw = new PrintWriter(f);

            //here we have to write the file with a loop implemented later

            //close writer
            f.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
