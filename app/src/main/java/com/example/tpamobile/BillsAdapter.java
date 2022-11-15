package com.example.tpamobile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillsViewHolder> {

    Context c;
    String data1[];
    String data2[];
    String data3[];

    public BillsAdapter(Context c, String s[], String t[],String u[]){
        this.c = c;
        this.data1 = s;
        this.data2 = t;
        this.data3 = u;
    }

    @NonNull
    @Override
    public BillsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.card_view, parent, false);
        return new BillsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillsViewHolder holder, int position) {
        Log.d("CategoryAdapter", "onBindViewHolder: pos: " + position);
        holder.tv_bill_category.setText(data1[position]);
        holder.tv_bill_category.setText(data2[position]);
        holder.tv_finished_bill.setText(data3[position]);
    }


    @Override
    public int getItemCount() {
        return data1.length;
    }

    public class BillsViewHolder extends RecyclerView.ViewHolder {

        TextView tv_bill_category, tv_bill_price, tv_finished_bill;

        public BillsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_bill_category = itemView.findViewById(R.id.category_card);
            tv_bill_price = itemView.findViewById(R.id.price_card);
            tv_finished_bill = itemView.findViewById(R.id.finished_status_card);
        }
    }
}