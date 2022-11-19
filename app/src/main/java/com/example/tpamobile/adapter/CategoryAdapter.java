package com.example.tpamobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpamobile.R;
import com.example.tpamobile.activity.category.CategoryDetailActivity;
import com.example.tpamobile.activity.transaction.AddTransactionActivity;
import com.example.tpamobile.model.Category;
import com.example.tpamobile.model.Transaction;

import java.io.Serializable;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements Serializable {

    private Context c;
    private List<Category> categoryList;
    private Transaction transaction;

    public CategoryAdapter(Context c, List<Category> categoryList){
        this.c = c;
        this.categoryList = categoryList;
    }

    public CategoryAdapter(Context c, List<Category> categoryList, Transaction transaction){
        this.c = c;
        this.categoryList = categoryList;
        this.transaction = transaction;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Log.d("onBindViewHolder", "onBindViewHolder: " + c.getClass().getName());
        Log.d("CategoryAdapter", "onBindViewHolder: pos: " + position);
        holder.tv_category_name.setText(categoryList.get(position).getName());
        if(c.getClass().getName().equals("com.example.tpamobile.HomeActivity")){
            holder.itemView.setOnClickListener(x->{
                Intent intent = new Intent(c, CategoryDetailActivity.class);
                intent.putExtra("currCategory", categoryList.get(position));
                c.startActivity(intent);
            });
        } else if (c.getClass().getName().equals("com.example.tpamobile.activity.transaction.SelectCategoryActivity")){
            holder.itemView.setOnClickListener(x->{
                Intent intent = new Intent(c, AddTransactionActivity.class);
                intent.putExtra("selectedCategory", categoryList.get(position));
                intent.putExtra("currTransaction", transaction);
                c.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tv_category_name;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category_name = itemView.findViewById(R.id.tv_category_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (dialog != null){
//                        dialog.onClick(getLayoutPosition());
//                    }
                }
            });
        }
    }
}
