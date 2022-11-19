package com.example.tpamobile.activity.bill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tpamobile.HomeActivity;
import com.example.tpamobile.R;
import com.example.tpamobile.databinding.ActivityAddBillBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddBillActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText et_bill_description, et_bill_amount, et_occurences;
    Date first_date;
    Button btn_save_bill;
    ProgressDialog progressDialog;
    ActivityAddBillBinding binding;
    Spinner spin_repeat;
    String[] repeatValues={"Every Week","Every Month","Every Year","Once Only"};
    EditText date;
    DatePickerDialog datePickerDialog;
    int iCurrentSelection;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);
        binding = ActivityAddBillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Bill");
        spin_repeat = (Spinner) binding.spinnerRepeatValue;
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,repeatValues);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_repeat.setAdapter(aa);
        et_occurences = binding.etRepeatUntilOccurrences;
        et_bill_amount = binding.etBillAmount;
        et_bill_description = binding.etBillDescription;
        btn_save_bill = binding.btnAddBill;
        progressDialog = new ProgressDialog(AddBillActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Saving...");
        date = binding.date;
        btn_save_bill.setOnClickListener(x -> {
            SimpleDateFormat formatter1=new SimpleDateFormat("dd/MM/yyyy");
            String billDescription = et_bill_description.getText().toString().trim();
            Integer billAmount = Integer.parseInt(et_bill_amount.getText().toString().trim());
            String repeatValue =spin_repeat.getSelectedItem().toString();
            Integer occurences = Integer.parseInt(et_occurences.getText().toString().trim());
            Date billDate=null;
            try {
                billDate = formatter1.parse(date.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            saveData(billDescription, billAmount, repeatValue,"Unpaid", billDate, occurences);
        });
        iCurrentSelection = spin_repeat.getSelectedItemPosition();

        spin_repeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                iCurrentSelection = spin_repeat.getSelectedItemPosition();
                Log.d("current S", ""+iCurrentSelection);
                if (iCurrentSelection == 3){
                    et_occurences.setText("0");
                    et_occurences.setEnabled(false);
                }
                else{
                    et_occurences.setText("");
                    et_occurences.setEnabled(true);
                }

            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        spin_repeat.setSelection(3);

        // perform click event on edit text
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(AddBillActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), repeatValues[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void saveData(String bill_description, Integer bill_amount, String repeat_value, String paid_status, Date bill_date, Integer occurences){
        Map<String, Object> bill = new HashMap<>();
        bill.put("billDescription", bill_description);
        bill.put("billAmount", bill_amount);
        bill.put("repeatValue", repeat_value);
        bill.put("paidStatus", paid_status);
        bill.put("billDate", bill_date);

        progressDialog.show();
        if(repeat_value.equals("Once Only")){
            db.collection("users")
                    .document(currUser.getUid())
                    .collection("bills")
                    .add(bill)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            Intent intent  = new Intent(AddBillActivity.this, HomeActivity.class);
                            intent.putExtra("fragmentToGo","bill");
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent  = new Intent(AddBillActivity.this, HomeActivity.class);
                            intent.putExtra("fragmentToGo","bill");
                            startActivity(intent);
                        }
                    });
        }
        else if(repeat_value.equals("Every Week")){
            for (int i =1 ; i<=occurences; i++){
                Calendar new_cal= dateToCalendar(bill_date);
                new_cal.add(Calendar.DATE, 7*i);
                Date new_date = calendarToDate(new_cal);
                bill.put("billDate", new_date);
                db.collection("users")
                        .document(currUser.getUid())
                        .collection("bills")
                        .add(bill)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                Intent intent  = new Intent(AddBillActivity.this, HomeActivity.class);
                                intent.putExtra("fragmentToGo","bill");
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent  = new Intent(AddBillActivity.this, HomeActivity.class);
                                intent.putExtra("fragmentToGo","bill");
                                startActivity(intent);
                            }
                        });
            }
        }
        else if(repeat_value.equals("Every Week")){
            for (int i =1 ; i<=occurences; i++){
                Calendar new_cal= dateToCalendar(bill_date);
                new_cal.add(Calendar.DATE, 7*i);
                Date new_date = calendarToDate(new_cal);
                bill.put("billDate", new_date);
                db.collection("users")
                        .document(currUser.getUid())
                        .collection("bills")
                        .add(bill)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                Intent intent  = new Intent(AddBillActivity.this, HomeActivity.class);
                                intent.putExtra("fragmentToGo","bill");
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent  = new Intent(AddBillActivity.this, HomeActivity.class);
                                intent.putExtra("fragmentToGo","bill");
                                startActivity(intent);
                            }
                        });
            }
        }
        else if(repeat_value.equals("Every Month")){
            for (int i =1 ; i<=occurences; i++){
                Calendar new_cal= dateToCalendar(bill_date);
                new_cal.add(Calendar.MONTH, i);
                Date new_date = calendarToDate(new_cal);
                bill.put("billDate", new_date);
                db.collection("users")
                        .document(currUser.getUid())
                        .collection("bills")
                        .add(bill)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                Intent intent  = new Intent(AddBillActivity.this, HomeActivity.class);
                                intent.putExtra("fragmentToGo","bill");
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent  = new Intent(AddBillActivity.this, HomeActivity.class);
                                intent.putExtra("fragmentToGo","bill");
                                startActivity(intent);
                            }
                        });
            }
        }
        else if(repeat_value.equals("Every Year")){
            for (int i =1 ; i<=occurences; i++){
                Calendar new_cal= dateToCalendar(bill_date);
                new_cal.add(Calendar.YEAR, i);
                Date new_date = calendarToDate(new_cal);
                bill.put("billDate", new_date);
                db.collection("users")
                        .document(currUser.getUid())
                        .collection("bills")
                        .add(bill)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                Intent intent  = new Intent(AddBillActivity.this, HomeActivity.class);
                                intent.putExtra("fragmentToGo","bill");
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent  = new Intent(AddBillActivity.this, HomeActivity.class);
                                intent.putExtra("fragmentToGo","bill");
                                startActivity(intent);
                            }
                        });
            }
        }

    }

    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

    //Convert Calendar to Date
    private Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }

}