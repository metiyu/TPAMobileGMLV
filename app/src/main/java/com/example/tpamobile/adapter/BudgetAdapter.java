package com.example.tpamobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpamobile.activity.budget.BudgetDetailActivity;
import com.example.tpamobile.R;
import com.example.tpamobile.model.Budget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>{

    private Context c;
    private List<Budget> budgetList;
    private Budget budget;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    String categoryName;

    public BudgetAdapter(Context context, List<Budget> budgetList) {
        this.c = context;
        this.budgetList = budgetList;
    }

    @NonNull
    @Override
    public BudgetAdapter.BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetAdapter.BudgetViewHolder holder, int position) {
        holder.tv_budget_amount.setText("Amount");
        holder.tv_budget_category.setText("category");
        if(budgetList.get(position).getAmount()!=null){
            holder.tv_budget_amount.setText("Rp"+budgetList.get(position).getAmount().toString().trim());
        }
        if(budgetList.get(position).getCategory()!=null){
            holder.tv_budget_category.setText(budgetList.get(position).getCategory().getName());
        }
        holder.itemView.setOnClickListener(x->{
            Intent intent = new Intent(c, BudgetDetailActivity.class);
            intent.putExtra("currBudget", budgetList.get(position));
            c.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }
    public class BudgetViewHolder extends RecyclerView.ViewHolder{
        TextView tv_budget_category, tv_budget_amount;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_budget_category = itemView.findViewById(R.id.tv_category_name);
            tv_budget_amount = itemView.findViewById(R.id.tv_amount_left);
        }
    }
}
