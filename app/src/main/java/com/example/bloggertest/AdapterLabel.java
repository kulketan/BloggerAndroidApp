package com.example.bloggertest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterLabel extends RecyclerView.Adapter<AdapterLabel.HolderLabel>{

    private Context context;
    private ArrayList<ModelLabel> labelArrayList;

    public AdapterLabel(Context context, ArrayList<ModelLabel> labelArrayList) {
        this.context = context;
        this.labelArrayList = labelArrayList;
    }

    @NonNull
    @Override
    public HolderLabel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_label.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_label,parent,false);
        return new HolderLabel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderLabel holder, int position) {

        ModelLabel modelLabel = labelArrayList.get(position);
        String label = modelLabel.getLabel();

        holder.labelTv.setText(label);
    }

    @Override
    public int getItemCount() {
        return labelArrayList.size();
    }


    /*viewhold class that will hold row_label.xml */
    class HolderLabel extends RecyclerView.ViewHolder{

        //ui views for row_label.xml
        private TextView labelTv;

        public HolderLabel(@NonNull View itemView) {
            super(itemView);

            labelTv = itemView.findViewById(R.id.labelTv);
        }


    }
}
