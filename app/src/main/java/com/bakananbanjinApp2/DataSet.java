package com.bakananbanjinApp2;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

//Testclass to create and and manipulate some data in SQL
public class DataSet {
    private Context mContext;

    public DataSet(Context context) {
        mContext = context;
    }

    public void query(){
       /* List<DataItem> dataItemList = new ArrayList<DataItem>();
        DataItem tempItem = new DataItem("Steak", 500);
        DataItem temItem2 = new DataItem("Piza", 800, 7,7,15,32);
        for(int i = 0; i < 10 ; i++){
            DataItem tempItem3 = new DataItem("TEMP" + i,(int)(Math.random() * 25) + 2000, (int)(Math.random() * 13),
                    (int)(Math.random() * 31), (int)(Math.random() * 24), (int)(Math.random()*60));
            tempItem3.toString();
            dataItemList.add(tempItem3);
        }
        temItem2.toString();
        temItem2.toStringtoFile();
        dataItemList.add(temItem2);
        dataItemList.add(tempItem);

        DataSetDB dataSetDB = new DataSetDB(this.mContext);
        dataSetDB.insertList(dataItemList);
        Cursor cursor = dataSetDB.selectAll();
        while(cursor.moveToNext()){
            Log.i("DATABASE", cursor.getString(0));
        }
        List<DataItem> dbDataItemList = dataSetDB.selectAllDataItem();
        for(DataItem i : dbDataItemList){
            i.toString();
        }
        //dataSetDB.deleteAll();
        //dataSetDB.drop();*/
        DataSetDB dataSetDB = new DataSetDB(this.mContext);
        Cursor cursor = dataSetDB.selectAll();
        List<DataItem> dataItemList = dataSetDB.selectAllDataItem();

        for(DataItem i : dataItemList){
            Log.i("DATABASE", i.toString());
        }

    }
}
