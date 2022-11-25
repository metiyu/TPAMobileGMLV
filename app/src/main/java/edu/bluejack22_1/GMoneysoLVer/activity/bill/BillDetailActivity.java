package edu.bluejack22_1.GMoneysoLVer.activity.bill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.HomeActivity;
import com.example.tpamobile.R;

import edu.bluejack22_1.GMoneysoLVer.model.Bill;

import com.example.tpamobile.databinding.ActivityBillDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class BillDetailActivity extends AppCompatActivity {

    EditText et_bill_description, et_bill_amount, et_bill_date;
    Button delete_btn, edit_btn, unpaid_btn;
    ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private Bill bill;
    private ActivityBillDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.bills));
        binding = ActivityBillDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bill = (Bill) getIntent().getSerializableExtra("currBill");
        et_bill_amount = binding.etBillAmount;
        et_bill_date = binding.etBillDate;
        et_bill_description = binding.etBillDescription;
        delete_btn = binding.btnDeleteBill;
        edit_btn = binding.btnEditBill;
        unpaid_btn = binding.btnUnpaid;

        et_bill_description.setText(bill.getDescription());
        et_bill_amount.setText(bill.formatRupiah());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = formatter.format(bill.getBillDate());
        et_bill_date.setText(strDate);

        unpaid_btn.setOnClickListener(x->{
            updatePaidStatus();
        });
        if(bill.getPaidStatus().equals("Unpaid")) {
            unpaid_btn.setVisibility(View.INVISIBLE);
        }
        progressDialog = new ProgressDialog(BillDetailActivity.this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.saving));

        edit_btn.setOnClickListener(x -> {
            Intent intent = new Intent(BillDetailActivity.this, EditBillActivity.class);
            intent.putExtra("currBill", bill);
            startActivity(intent);
        });

        delete_btn.setOnClickListener(x -> {
            deleteData(bill.getId());
        });
    }
    private void deleteData(String id){
        progressDialog.show();
        db.collection("users")
                .document(currUser.getUid())
                .collection("bills")
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(BillDetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BillDetailActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        Intent intent = new Intent(BillDetailActivity.this, HomeActivity.class);
                        intent.putExtra("fragmentToGo","bill");
                        startActivity(intent);
                    }
                });
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent  = new Intent(BillDetailActivity.this, HomeActivity.class);
        intent.putExtra("fragmentToGo","bill");
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    public void updatePaidStatus(){
        this.bill.setPaidStatus("Unpaid");
        Map<String, Object> bill = new HashMap<>();
        bill.put("billDescription", this.bill.getDescription());
        bill.put("billAmount", this.bill.getBillAmount().intValue());
        bill.put("paidStatus", this.bill.getPaidStatus());
        bill.put("repeatValue", this.bill.getRepeatValue());
        bill.put("billDate", this.bill.getBillDate());
        bill.put("billWallet", this.bill.getWallet().getId());
        bill.put("billCategory", this.bill.getCategory().getId());

        db.collection("users")
                .document(currUser.getUid())
                .collection("bills")
                .document(this.bill.getId())
                .set(bill)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        unpaid_btn.setVisibility(View.INVISIBLE);
                    }
                });

    }
}