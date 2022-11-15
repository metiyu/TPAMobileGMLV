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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    Context c;
    String data1[];
    int images[];

    public CategoryAdapter(Context c, String s[], int img[]){
        this.c = c;
        this.data1 = s;
        this.images = img;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(c);
        View view = inflater.inflate(R.layout.card_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Log.d("CategoryAdapter", "onBindViewHolder: pos: " + position);
        holder.tv_category_name.setText(data1[position]);
        holder.iv_category_name.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return data1.length;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView tv_category_name;
        ImageView iv_category_name;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category_name = itemView.findViewById(R.id.tv_category_name);
            iv_category_name = itemView.findViewById(R.id.iv_category_image);
        }
    }
}
