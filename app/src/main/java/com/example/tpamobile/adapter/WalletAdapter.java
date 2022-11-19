package com.example.tpamobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpamobile.R;
import com.example.tpamobile.activity.category.CategoryDetailActivity;
import com.example.tpamobile.activity.transaction.AddTransactionActivity;
import com.example.tpamobile.activity.wallet.WalletDetailActivity;
import com.example.tpamobile.model.Category;
import com.example.tpamobile.model.Transaction;
import com.example.tpamobile.model.Wallet;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletViewHolder>  {

    private Context c;
    private List<Wallet> walletList;
    private Transaction transaction;

    public WalletAdapter(Context c, List<Wallet> walletList) {
        this.c = c;
        this.walletList = walletList;
    }

    public WalletAdapter(Context c, List<Wallet> walletList, Transaction transaction) {
        this.c = c;
        this.walletList = walletList;
        this.transaction = transaction;
    }

    @NonNull
    @Override
    public WalletAdapter.WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_wallet, parent, false);
        return new WalletViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletAdapter.WalletViewHolder holder, int position) {
        holder.tv_wallet_name.setText(walletList.get(position).getName());
        holder.tv_wallet_amount.setText(walletList.get(position).formatRupiah());
//        holder.tv_wallet_amount.setText(formatRupiah(Double.parseDouble(walletList.get(position).getAmount().toString())));
        if(c.getClass().getName().equals("com.example.tpamobile.HomeActivity")){
            holder.itemView.setOnClickListener(x -> {
                Intent intent = new Intent(c, WalletDetailActivity.class);
                intent.putExtra("currWallet", walletList.get(position));
                c.startActivity(intent);
            });
        } else if (c.getClass().getName().equals("com.example.tpamobile.activity.transaction.SelectWalletActivity")){
            holder.itemView.setOnClickListener(x->{
                Intent intent = new Intent(c, AddTransactionActivity.class);
                intent.putExtra("selectedWallet", walletList.get(position));
                intent.putExtra("currTransaction", transaction);
                c.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }

    public class WalletViewHolder extends RecyclerView.ViewHolder {
        TextView tv_wallet_name, tv_wallet_amount;

        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_wallet_name = itemView.findViewById(R.id.tv_wallet_name);
            tv_wallet_amount = itemView.findViewById(R.id.tv_wallet_amount);
        }
    }

    private String formatRupiah(Double number){
        Locale localeID = new Locale("IND", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        String rupiahFormatted = formatRupiah.format(number);
        String[] split = rupiahFormatted.split(",");
        int length = split[0].length();
        return split[0].substring(0,2)+". "+split[0].substring(2,length);
    }
}
