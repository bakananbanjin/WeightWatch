package com.bakananbanjinApp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.ListItemHolder> {
    private OnItemDeleteListener deleteListener;
    private List<DataItem> dataItemList;
    private Context context;
    private RecyclerViewInterface recyclerViewInterface;


    public DataItemAdapter(Context context, List<DataItem> dataItemList, OnItemDeleteListener deleteListener) {
        this.dataItemList = dataItemList;
        this.deleteListener = deleteListener;
        this.context = context;
    }

    @NonNull
    @Override
    public DataItemAdapter.ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dataitem, parent, false);
        return new ListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataItemAdapter.ListItemHolder holder, int position) {
        ListItemHolder dataItemAdapter = (ListItemHolder) holder;
        DataItem dataItem = dataItemList.get(position);
        holder.mDataItemTitle.setText(dataItem.getmItemName() + "\n");
        holder.mDataItemDate.setText(dataItem.getDateTime());
        holder.mDataItemCal.setText(dataItem.getmCal() + "\n");

    }

    @Override
    public int getItemCount() {
        return dataItemList.size();
    }
    public void setItemClickListener(RecyclerViewInterface recyclerViewInterface){
        this.recyclerViewInterface = recyclerViewInterface;
    }
    public class ListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mDataItemTitle;
        TextView mDataItemDate;
        TextView mDataItemCal;
        TextView mDataItemX;
        public ListItemHolder(@NonNull View itemView) {
            super(itemView);
            mDataItemTitle = itemView.findViewById(R.id.tv_dataitem_title);
            mDataItemDate = itemView.findViewById(R.id.tv_dataitem_date);
            mDataItemCal = itemView.findViewById(R.id.tv_dataitem_cal );
            mDataItemX = itemView.findViewById(R.id.tv_dataitem_x);
            mDataItemX.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        deleteListener.onDeleteClick(position);
                    }
                }
            });
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if(recyclerViewInterface != null){
                recyclerViewInterface.onItemClick(getAdapterPosition());
            }
        }

    }
}
