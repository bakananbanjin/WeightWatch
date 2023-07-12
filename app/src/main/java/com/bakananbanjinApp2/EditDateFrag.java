package com.bakananbanjinApp2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EditDateFrag extends Fragment implements RecyclerViewInterface{

    private RecyclerView mRecyclerViewEdit;
    private List<DataItem> dataItemList;
    public static DataItemAdapter myAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_data_frag, container, false);


        mRecyclerViewEdit = view.findViewById(R.id.recyclerview);
        mRecyclerViewEdit.setHasFixedSize(true);

        dataItemList = new ArrayList<DataItem>();
        dataItemList = Engine.mDB.selectAllDataItem();
        mRecyclerViewEdit.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewEdit.setHasFixedSize(true);
        myAdapter = new DataItemAdapter(getContext(), dataItemList);
        myAdapter.setItemClickListener(this);
        mRecyclerViewEdit.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();



        return view;
    }

    @Override
    public void onItemClick(int position) {
        if(dataItemList.get(position) != null) {
            EditDialog insertDialog = new EditDialog(dataItemList.get(position));
            insertDialog.show(getParentFragmentManager(), null);
        }

        Log.i("ITEM CLICK", "Item on Position " + position + " got clicked");
    }
    public void notifyChange(){
        myAdapter.notifyDataSetChanged();

    }
}
