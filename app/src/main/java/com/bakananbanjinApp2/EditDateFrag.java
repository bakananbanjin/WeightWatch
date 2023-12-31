package com.bakananbanjinApp2;

import android.os.Bundle;

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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class EditDateFrag extends Fragment implements RecyclerViewInterface, OnItemDeleteListener{

    private RecyclerView mRecyclerViewEdit;
    private List<DataItem> dataItemList;
    private List<Weight> weightList;
    private static boolean dataItemView = true;
    public static DataItemAdapter myAdapter;
    public static WeightItemAdapter myWeightAdapter;
    public static TextView tv_edit_weight;
    public static TextView tv_edit_meals;
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

        tv_edit_meals = mView.findViewById(R.id.tv_edit_data);
        tv_edit_meals.setBackgroundResource(R.drawable.round_corner_view);
        tv_edit_weight.setBackgroundResource(R.drawable.round_corner_view);

        tv_edit_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              dataItemView = false;
              weightView(mView);
              reloadFragment();
            }
        });
        tv_edit_meals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataItemView = true;
                dataItemView(mView);
                reloadFragment();
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

        //Log.i("ITEM CLICK", "Item on Position " + position + " got clicked");
    }

    public void weightView(View view){
        mRecyclerViewEdit = view.findViewById(R.id.recyclerview);
        mRecyclerViewEdit.setHasFixedSize(true);
        weightList = new ArrayList<>();
        weightList = Engine.mDB.selectAllWeightList();
        mRecyclerViewEdit.setLayoutManager(new LinearLayoutManager(getContext()));
        myWeightAdapter = new WeightItemAdapter(getContext(), weightList, this);
        myWeightAdapter.setItemClickListener(this);
        mRecyclerViewEdit.setAdapter(myWeightAdapter);
        tv_edit_weight.setBackgroundResource(R.drawable.round_corner_view_light);
        tv_edit_meals.setBackgroundResource(R.drawable.round_corner_view);
    }
    public void dataItemView(View view){
        mRecyclerViewEdit = view.findViewById(R.id.recyclerview);
        mRecyclerViewEdit.setHasFixedSize(true);
        dataItemList = new ArrayList<>();
        dataItemList = Engine.mDB.selectAllDataItem();
        mRecyclerViewEdit.setLayoutManager(new LinearLayoutManager(getContext()));
        myAdapter = new DataItemAdapter(getContext(), dataItemList, this);
        myAdapter.setItemClickListener(this);
        mRecyclerViewEdit.setAdapter(myAdapter);
        tv_edit_weight.setBackgroundResource(R.drawable.round_corner_view);
        tv_edit_meals.setBackgroundResource(R.drawable.round_corner_view_light);
    }
    private void reloadFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.attach(this);
        fragmentTransaction.commit();
    }

    @Override
    public void onDeleteClick(int position) {
        if(dataItemView) {
            Engine.mDB.deleteByID(dataItemList.get(position).getId());
            dataItemList.remove(position);
            myAdapter.notifyDataSetChanged();
        } else {
            Engine.mDB.deleteWeightById(weightList.get(position).getId());
            weightList.remove(position);
            myWeightAdapter.notifyDataSetChanged();
        }
    }
}
