package com.bakananbanjinApp2;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Engine {
    private static DataSetDB mDB;
    private static Engine engine;
    private Context mContext;
    public Engine(Context context) {
        mContext = context;
        mDB = new DataSetDB(context);
        engine = this;
    }
    //function gets called when insert btn in Insertfield is pressed
    //class needs to be initialised
    public static boolean insertNewDataItem(DataItem dataItem){
        if(engine == null){
            Log.e("ERROR ENGINE", "Engine is not initialized");
            return false;
        }
        mDB.insert(dataItem);
        return true;
    }
}
