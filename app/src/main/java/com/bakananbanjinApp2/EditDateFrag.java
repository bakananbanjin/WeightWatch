package com.bakananbanjinApp2;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EditDateFrag extends Fragment implements RecyclerViewInterface{

    private RecyclerView mRecyclerViewEdit;
    private List<DataItem> dataItemList;
    private List<Weight> weightList;
    private static boolean dataItemView = true;
    public static DataItemAdapter myAdapter;
    public static WeightItemAdapter myWeightAdapter;
    public static TextView tv_edit_weight;
    private View mView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.edit_data_frag, container, false);
        tv_edit_weight = mView.findViewById(R.id.tv_edit_weight);
        TextView tv_edit_meals = mView.findViewById(R.id.tv_edit_data);
        tv_edit_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              dataItemView = false;
              weightView(mView);
              reloadFragment();
              Log.i("TVEDITWEIGHT", "weight pressed");
            }
        });
        tv_edit_meals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataItemView = true;
                dataItemView(mView);
                reloadFragment();
                Log.i("TVEDITMEAL", "meals pressed");
            }
        });
        if(dataItemView){
            dataItemView(mView);
        } else {
            weightView(mView);
        }
        return mView;
    }
    @Override
    public void onItemClick(int position) {
        if(dataItemView) {
            if(dataItemList.get(position) != null) {
                    EditDialog insertDialog = new EditDialog(dataItemList.get(position));
                    insertDialog.show(getParentFragmentManager(), null);
            }
        } else{
            if(weightList.get(position) != null){
                    InsertWeightDialog insertDialog = new InsertWeightDialog(weightList.get(position));
                    insertDialog.show(getParentFragmentManager(), null);
            }
        }

        Log.i("ITEM CLICK", "Item on Position " + position + " got clicked");
    }
    public void weightView(View view){
        mRecyclerViewEdit = view.findViewById(R.id.recyclerview);
        mRecyclerViewEdit.setHasFixedSize(true);
        weightList = new ArrayList<>();
        weightList = Engine.mDB.selectAllWeightList();
        mRecyclerViewEdit.setLayoutManager(new LinearLayoutManager(getContext()));
        myWeightAdapter = new WeightItemAdapter(getContext(), weightList);
        myWeightAdapter.setItemClickListener(this);
        mRecyclerViewEdit.setAdapter(myWeightAdapter);
    }
    public void dataItemView(View view){
        mRecyclerViewEdit = view.findViewById(R.id.recyclerview);
        mRecyclerViewEdit.setHasFixedSize(true);
        dataItemList = new ArrayList<>();
        dataItemList = Engine.mDB.selectAllDataItem();
        mRecyclerViewEdit.setLayoutManager(new LinearLayoutManager(getContext()));
        myAdapter = new DataItemAdapter(getContext(), dataItemList);
        myAdapter.setItemClickListener(this);
        mRecyclerViewEdit.setAdapter(myAdapter);
    }
    private void reloadFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.attach(this);
        fragmentTransaction.commit();
    }
}
