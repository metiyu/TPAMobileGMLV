package com.example.tpamobile.activity.bill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tpamobile.R;
import com.example.tpamobile.activity.category.CategoryDetailActivity;
import com.example.tpamobile.activity.category.EditCategoryActivity;
import com.example.tpamobile.activity.wallet.EditWalletActivity;
import com.example.tpamobile.activity.wallet.WalletDetailActivity;
import com.example.tpamobile.databinding.ActivityBillDetailBinding;
import com.example.tpamobile.databinding.ActivityEditBillBinding;
import com.example.tpamobile.model.Bill;
import com.example.tpamobile.model.Wallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditBillActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ActivityEditBillBinding binding;
    EditText et_bill_description, et_bill_amount, et_bill_date;
    Button save_btn;
    Spinner spin_repeat;
    ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private Bill bill;
    int iCurrentSelection;
    Date beforeUpdate;
    DatePickerDialog datePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.bills));

        binding = ActivityEditBillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bill = (Bill) getIntent().getSerializableExtra("currBill");
        et_bill_amount = binding.etBillAmount;
        et_bill_date = binding.etBillDate;
        et_bill_description = binding.etBillDescription;
        save_btn = binding.btnSaveBill;

        et_bill_description.setText(bill.getDescription());
        beforeUpdate = bill.getBillDate();
        et_bill_amount.setText(bill.getBillAmount().toString());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = formatter.format(bill.getBillDate());
        et_bill_date.setText(strDate);

        progressDialog = new ProgressDialog(EditBillActivity.this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.saving));

        et_bill_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(EditBillActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                et_bill_date.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        save_btn.setOnClickListener(x -> {
            SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy");
            String billDescription = et_bill_description.getText().toString().trim();
            Integer billAmount = Integer.parseInt(et_bill_amount.getText().toString().trim());
            Date billDate=null;
            try {
                billDate = formatter1.parse(et_bill_date.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            saveData(billDescription, billAmount, billDate);
        });
    }
    private void saveData(String billDescription, Integer billAmount, Date billDate){
        Map<String, Object> new_bill = new HashMap<>();
        new_bill.put("billDescription", billDescription);
        new_bill.put("billAmount", billAmount);
        new_bill.put("billDate", billDate);
        new_bill.put("paidStatus", this.bill.getPaidStatus());
        new_bill.put("repeatValue", this.bill.getRepeatValue());
//        bill.put("", billDate);


        progressDialog.show();

        db.collection("users")
                .document(currUser.getUid())
                .collection("bills")
                .document(this.bill.getId())
                .set(new_bill)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        getData();

                    }
                });
    }
    private void getData(){
        progressDialog.show();
        db.collection("users")
                .document(currUser.getUid())
                .collection("bills")
                .document(bill.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            Intent intent  = new Intent(EditBillActivity.this, BillDetailActivity.class);
                            DocumentSnapshot document = task.getResult();
                            Calendar current_cal = dateToCalendar(document.getDate("billDate"));
                            if (document.exists()) {
                                Log.d("document", "DocumentSnapshot data: " + document.getData());
                                Bill new_bill = new Bill(document.getId(), document.getString("billDescription"), document.getString("repeatValue"), document.getString("paidStatus"), document.getLong("billAmount").intValue(),current_cal.get(Calendar.YEAR), current_cal.get(Calendar.MONTH),current_cal.get(Calendar.DAY_OF_MONTH)  ,document.getDate("billDate"));
                                intent.putExtra("currBill", (Serializable) new_bill);
                                startActivity(intent);
                            }
                            progressDialog.dismiss();
                        }

                    }
                });
    }
    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent  = new Intent(EditBillActivity.this, BillDetailActivity.class);
        intent.putExtra("currBill", bill);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}