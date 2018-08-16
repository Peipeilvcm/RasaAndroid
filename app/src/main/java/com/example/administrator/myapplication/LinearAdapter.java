package com.example.administrator.myapplication;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/15.
 */

public class LinearAdapter extends RecyclerView.Adapter<LinearAdapter.LinearViewHolder> {

    private Context context;
    private List<String> dataList;
    private OnItemClickListener listener;

    public LinearAdapter(Context context,  OnItemClickListener listener){
        this.context = context;
        this.dataList = new ArrayList<String>();
        this.listener = listener;
    }

    @Override
    public LinearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LinearViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_linear_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final LinearViewHolder holder, final int position) {
        holder.textView.setText(dataList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(position % 2 == 0){
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addItem(int position, String item){
        if(position > dataList.size() - 1){
            position = dataList.size();
        }
        if(position < dataList.size()){
            position = 0;
        }
        dataList.add(position, item);
        notifyItemInserted(position);
    }
    public void append(String item){
        addItem(getItemCount(), item);
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        public LinearViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.listItem);
        }
    }

    public interface OnItemClickListener{
        void onClick(int pos);
    }
}
