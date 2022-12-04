package edu.bluejack22_1.GMoneysoLVer.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.bluejack22_1.GMoneysoLVer.EditBudgetActivity;
import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.activity.bill.AddBillActivity;
import edu.bluejack22_1.GMoneysoLVer.activity.budget.AddBudgetActivity;
import edu.bluejack22_1.GMoneysoLVer.activity.category.CategoryDetailActivity;
import edu.bluejack22_1.GMoneysoLVer.activity.transaction.AddTransactionActivity;
import edu.bluejack22_1.GMoneysoLVer.activity.transaction.SelectCategoryActivity;
import edu.bluejack22_1.GMoneysoLVer.model.Bill;
import edu.bluejack22_1.GMoneysoLVer.model.Category;
import edu.bluejack22_1.GMoneysoLVer.model.Transaction;

import java.io.Serializable;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements Serializable {

    private Context c;
    private List<Category> categoryList;
    private Transaction transaction;
    private Bill bill;

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
        if(c.getClass().getName().equals("edu.bluejack22_1.GMoneysoLVer.HomeActivity")){
            holder.itemView.setOnClickListener(x->{
                Log.d("CATEGORY ADAPTER", "onBindViewHolder: click dri home");
                Intent intent = new Intent(c, CategoryDetailActivity.class);
                intent.putExtra("currCategory", categoryList.get(position));
                c.startActivity(intent);
            });
        } else if (c.getClass().getName().equals("edu.bluejack22_1.GMoneysoLVer.activity.transaction.SelectCategoryActivity")){
            if(SelectCategoryActivity.bill_in_select_category!=null){
                holder.itemView.setOnClickListener(x->{
                    Intent intent = new Intent(c, AddBillActivity.class);
                    intent.putExtra("selectedCategory", categoryList.get(position));
                    intent.putExtra("currBill", SelectCategoryActivity.bill_in_select_category);
                    c.startActivity(intent);
                });
            }
            else if(SelectCategoryActivity.transaction_in_select_category!=null){
                holder.itemView.setOnClickListener(x->{
                    Intent intent = new Intent(c, AddTransactionActivity.class);
                    intent.putExtra("selectedCategory", categoryList.get(position));
                    intent.putExtra("currTransaction", SelectCategoryActivity.transaction_in_select_category);
                    c.startActivity(intent);
                });
            }
            else if(SelectCategoryActivity.budget_in_select_category!=null){
                if(SelectCategoryActivity.from_edit_budget==true){
                    holder.itemView.setOnClickListener(x->{
                        Intent intent = new Intent(c, EditBudgetActivity.class);
                        intent.putExtra("selectedCategory", categoryList.get(position));
                        intent.putExtra("currBudget", SelectCategoryActivity.budget_in_select_category);
                        c.startActivity(intent);
                    });
                }
                else{
                    holder.itemView.setOnClickListener(x->{
                        Intent intent = new Intent(c, AddBudgetActivity.class);
                        intent.putExtra("selectedCategory", categoryList.get(position));
                        intent.putExtra("currBudget", SelectCategoryActivity.budget_in_select_category);
                        c.startActivity(intent);
                    });
                }

            }

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
