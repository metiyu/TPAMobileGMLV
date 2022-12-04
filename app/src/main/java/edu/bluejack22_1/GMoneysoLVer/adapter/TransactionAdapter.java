package edu.bluejack22_1.GMoneysoLVer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.activity.transaction.adapter.TransactionPerCategoryAdapter;
import edu.bluejack22_1.GMoneysoLVer.model.TransactionGroupByDate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context c;
    private List<TransactionGroupByDate> transactionList;
    private TransactionPerCategoryAdapter adapter;

    public TransactionAdapter(Context c, List<TransactionGroupByDate> transactionList, TransactionPerCategoryAdapter adapter) {
        this.c = c;
        this.transactionList = transactionList;
        this.adapter = adapter;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Date date = transactionList.get(position).getDate();
        holder.tv_transaction_date.setText(new SimpleDateFormat("dd").format(date));
        holder.tv_transaction_day.setText(new SimpleDateFormat("EEEE").format(date));
        holder.tv_transaction_month_year.setText(new SimpleDateFormat("MMMM").format(date) + " " + new SimpleDateFormat("yyyy").format(date));
        holder.tv_transaction_subtotal.setText(transactionList.get(position).getSubTotalAmountFormatted());
        if (transactionList.get(position).getSubTotalAmount() < 0){
            holder.tv_transaction_subtotal.setTextColor(ContextCompat.getColor(c, R.color.expenseColor));
        } else {
            holder.tv_transaction_subtotal.setTextColor(ContextCompat.getColor(c, R.color.incomeColor));
        }

        adapter = new TransactionPerCategoryAdapter(c, transactionList.get(position).getTransactionList());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(holder.itemView.getContext(), DividerItemDecoration.VERTICAL);
        holder.rv_transactions.setLayoutManager(layoutManager);
        holder.rv_transactions.addItemDecoration(decoration);
//        holder.rv_transactions.setHasFixedSize(true);
        holder.rv_transactions.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView tv_transaction_date, tv_transaction_day, tv_transaction_month_year, tv_transaction_subtotal;
        RecyclerView rv_transactions;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_transaction_date = itemView.findViewById(R.id.tv_transaction_date);
            tv_transaction_day = itemView.findViewById(R.id.tv_transaction_day);
            tv_transaction_month_year = itemView.findViewById(R.id.tv_transaction_month_year);
            tv_transaction_subtotal = itemView.findViewById(R.id.tv_transaction_subtotal);
            rv_transactions = itemView.findViewById(R.id.rv_transactions);
        }
    }

}
