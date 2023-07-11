package com.bakananbanjinApp2;

import android.os.Bundle;
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

public class EditDateFrag extends Fragment{

    private RecyclerView mRecyclerViewEdit;
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
        List DataItemList = new ArrayList();
        DataItemList = Engine.mDB.selectAllDataItem();
        mRecyclerViewEdit.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewEdit.setHasFixedSize(true);
        DataItemAdapter myAdapter = new DataItemAdapter(getContext(), DataItemList);
        mRecyclerViewEdit.setAdapter( myAdapter);
        myAdapter.notifyDataSetChanged();



        return view;
    }
}
