package com.bakananbanjinApp2;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeightItemAdapter extends RecyclerView.Adapter<WeightItemAdapter.ListItemHolder> {
    private List<Weight> weightList;
    private Context context;
    private RecyclerViewInterface recyclerViewInterface;

    public WeightItemAdapter(Context context, List<Weight> weightList) {
        this.weightList = weightList;
        this.context = context;
    }

    @NonNull
    @Override
    public WeightItemAdapter.ListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dataitem, parent, false);
        return new WeightItemAdapter.ListItemHolder(view);
    }


    public void onBindViewHolder(@NonNull WeightItemAdapter.ListItemHolder holder, int position) {
        Weight dataItem = weightList.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(dataItem.getCalendar().getTime());

        holder.mDataItemTitle.setText(dataItem.getWeight() + "");
        holder.mDataItemDate.setText(dateString);
        holder.mDataItemCal.setText("");
    }

    @Override
    public int getItemCount() {
        return weightList.size();
    }
    public void setItemClickListener(RecyclerViewInterface recyclerViewInterface){
        this.recyclerViewInterface = recyclerViewInterface;
    }
    public class ListItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mDataItemTitle;
        TextView mDataItemDate;
        TextView mDataItemCal;
        public ListItemHolder(@NonNull View itemView) {
            super(itemView);
            mDataItemTitle = itemView.findViewById(R.id.tv_dataitem_title);
            mDataItemDate = itemView.findViewById(R.id.tv_dataitem_date);
            mDataItemCal = itemView.findViewById(R.id.tv_dataitem_cal );
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