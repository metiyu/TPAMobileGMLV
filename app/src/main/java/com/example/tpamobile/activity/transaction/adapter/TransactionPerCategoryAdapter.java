package com.example.tpamobile.activity.transaction.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpamobile.R;
import com.example.tpamobile.activity.transaction.TransactionDetailActivity;
import com.example.tpamobile.model.Transaction;

import org.w3c.dom.Text;

import java.util.List;

public class TransactionPerCategoryAdapter extends RecyclerView.Adapter<TransactionPerCategoryAdapter.TransactionPerCategoryViewHolder> {

    private Context c;
    private List<Transaction> transactionList;

    public TransactionPerCategoryAdapter(Context c, List<Transaction> transactionList) {
        this.c = c;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionPerCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_transaction_per_category, parent, false);
        return new TransactionPerCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionPerCategoryViewHolder holder, int position) {
        holder.tv_category_name.setText(transactionList.get(position).getTransactionCategory().getName());
        holder.tv_transaction_note.setText(transactionList.get(position).getTransactionNote());
        if (transactionList.get(position).getTransactionCategory().getType().equals("expense")){
            holder.tv_transaction_amount.setText(transactionList.get(position).formatRupiah());
            holder.tv_transaction_amount.setTextColor(ContextCompat.getColor(c, R.color.expenseColor));
        }
        else{
            holder.tv_transaction_amount.setText(transactionList.get(position).formatRupiah());
            holder.tv_transaction_amount.setTextColor(ContextCompat.getColor(c, R.color.incomeColor));
        }

        holder.itemView.setOnClickListener(x -> {
            Intent intent = new Intent(c, TransactionDetailActivity.class);
            intent.putExtra("currTransaction", transactionList.get(position));
            c.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class TransactionPerCategoryViewHolder extends RecyclerView.ViewHolder {

        TextView tv_category_name, tv_transaction_note, tv_transaction_amount;

        public TransactionPerCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_category_name = itemView.findViewById(R.id.tv_category_name);
            tv_transaction_note = itemView.findViewById(R.id.tv_transaction_note);
            tv_transaction_amount = itemView.findViewById(R.id.tv_transaction_amount);
        }
    }
}
