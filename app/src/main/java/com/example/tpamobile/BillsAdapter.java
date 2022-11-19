package com.example.tpamobile;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpamobile.model.Bill;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillsViewHolder> {

    private Context c;
    private List<Bill> billList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

//    public BillsAdapter(Context c, String s[], String t[],String u[]){
//        this.c = c;
//        this.data1 = s;
//        this.data2 = t;
//        this.data3 = u;
//    }

    public BillsAdapter(Context context, List<Bill> billList) {
        this.c = context;
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_view, parent, false);
        return new BillsViewHolder(view);
    }

//    @NonNull
//    @Override
//    public BillsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return null;
//    }

    @Override
    public void onBindViewHolder(@NonNull BillsViewHolder holder, int position) {
        Log.d("CategoryAdapter", "onBindViewHolder: pos: " + position);
        holder.tv_bill_category.setText("Category");
        holder.tv_bill_price.setText(billList.get(position).getBillAmount().toString());
        if(billList.get(position).getPaidStatus().toString().equals("Unpaid")){
            holder.finished_bill.setText("PAY");
        }
        else{
            holder.finished_bill.setText("PAID");
            holder.finished_bill.setBackgroundColor(Color.parseColor("#b0b0b0"));
            holder.finished_bill.setClickable(false);
        }
        holder.finished_bill.setOnClickListener(x->{
            updatePaidStatus(position);
        });
        holder.tv_bill_due.setText(billList.get(position).getDueDate());
    }

    public void updatePaidStatus(int position){
        if(billList.get(position).getPaidStatus().toString().equals("Unpaid")){
            this.billList.get(position).setPaidStatus("Paid");
            Map<String, Object> bill = new HashMap<>();
            bill.put("billDescription", billList.get(position).getDescription());
            bill.put("billAmount", billList.get(position).getBillAmount().intValue());
            bill.put("paidStatus", billList.get(position).getPaidStatus());
            bill.put("repeatValue", billList.get(position).getRepeatValue());

            db.collection("users")
                    .document(currUser.getUid())
                    .collection("bills")
                    .document(this.billList.get(position).getId())
                    .set(bill)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
        }
    }


    @Override
    public int getItemCount() {
        return billList.size();
    }

    public class BillsViewHolder extends RecyclerView.ViewHolder {

        TextView tv_bill_category, tv_bill_price, tv_bill_due;
        Button finished_bill;

        public BillsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_bill_category = itemView.findViewById(R.id.category_card);
            tv_bill_price = itemView.findViewById(R.id.price_card);
            finished_bill = itemView.findViewById(R.id.finished_status_card);
            tv_bill_due = itemView.findViewById(R.id.bill_due);
        }
    }
}