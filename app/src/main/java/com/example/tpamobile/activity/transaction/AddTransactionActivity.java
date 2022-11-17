package com.example.tpamobile.activity.transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tpamobile.R;
import com.example.tpamobile.activity.wallet.AddWalletActivity;
import com.example.tpamobile.model.Category;
import com.example.tpamobile.model.Transaction;
import com.example.tpamobile.model.Wallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTransactionActivity extends AppCompatActivity {

    private TextInputLayout til_transaction_amount, til_transaction_category, til_transaction_note, til_transaction_date, til_transaction_wallet;
    private EditText et_transaction_amount, et_transaction_category, et_transaction_note, et_transaction_date, et_transaction_wallet;
    private Button btn_save_transaction;
    private Calendar calendar = Calendar.getInstance();
    private Category category;
    private Wallet wallet;
    private Transaction transaction;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

    private final String TAG = "MAKE TRANSACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        til_transaction_amount = findViewById(R.id.til_transaction_amount);
        til_transaction_category = findViewById(R.id.til_transaction_category);
        til_transaction_note = findViewById(R.id.til_transaction_note);
        til_transaction_date = findViewById(R.id.til_transaction_date);
        til_transaction_wallet = findViewById(R.id.til_transaction_wallet);
        et_transaction_amount = findViewById(R.id.et_transaction_amount);
        et_transaction_category = findViewById(R.id.et_transaction_category);
        et_transaction_note = findViewById(R.id.et_transaction_note);
        et_transaction_date = findViewById(R.id.et_transaction_date);
        et_transaction_wallet = findViewById(R.id.et_transaction_wallet);
        btn_save_transaction = findViewById(R.id.btn_save_transaction);

        transaction = (Transaction) getIntent().getSerializableExtra("currTransaction");
        if(transaction != null){
            Log.d(TAG, "onCreate: " + transaction.getTransactionAmount());
            if (transaction.getTransactionAmount() != null)
                et_transaction_amount.setText(transaction.getTransactionAmount().toString());
            if (transaction.getTransactionCategory() != null){
                et_transaction_category.setText(transaction.getTransactionCategory().getName());
                category = transaction.getTransactionCategory();
            }
            if (transaction.getTransactionNote() != null)
                et_transaction_note.setText(transaction.getTransactionNote());
            if (transaction.getTransactionDate() != null)
                et_transaction_date.setText(transaction.getTransactionDate().toString());
            if (transaction.getTransactionWallet() != null) {
                et_transaction_wallet.setText(transaction.getTransactionWallet().getName());
                wallet = transaction.getTransactionWallet();
            }
        }

        et_transaction_category.setOnClickListener(x -> {
            Intent intent = new Intent(AddTransactionActivity.this, SelectCategoryActivity.class);
            intent.putExtra("currTransaction", saveCurrentData());
            startActivity(intent);
        });
        if ((Category) getIntent().getSerializableExtra("selectedCategory") != null){
            category = (Category) getIntent().getSerializableExtra("selectedCategory");
            et_transaction_category.setText(category.getName());
        }

        et_transaction_wallet.setOnClickListener(x -> {
            Intent intent = new Intent(AddTransactionActivity.this, SelectWalletActivity.class);
            intent.putExtra("currTransaction", saveCurrentData());
            startActivity(intent);
        });
        if ((Wallet) getIntent().getSerializableExtra("selectedWallet") != null){
            wallet = (Wallet) getIntent().getSerializableExtra("selectedWallet");
            et_transaction_wallet.setText(wallet.getName());
        }

        et_transaction_note.setOnClickListener(x -> {
            Intent intent = new Intent(AddTransactionActivity.this, WriteNoteActivity.class);
            intent.putExtra("currTransaction", saveCurrentData());
            startActivity(intent);
        });
        if(getIntent().getStringExtra("transactionNote") != null){
            et_transaction_note.setText(getIntent().getStringExtra("transactionNote"));
        }

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                formatDate();
            }
        };

        et_transaction_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myFormat="E, dd/MM/yy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                DatePickerDialog dialog = new DatePickerDialog(AddTransactionActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        btn_save_transaction.setOnClickListener(x -> {
            Integer transactionAmount = Integer.parseInt(et_transaction_amount.getText().toString().trim());
            Category transactionCategory = this.category;
            String transactionNote = et_transaction_note.getText().toString().trim();
            Date transactionDate = new Date(et_transaction_date.getText().toString());
            Wallet transactionWallet = this.wallet;

            saveData(transactionAmount, transactionCategory, transactionNote, transactionDate, transactionWallet);
        });
    }

    private void saveData(Integer transactionAmount, Category transactionCategory, String transactionNote, Date transactionDate, Wallet transactionWallet){
        Log.d(TAG, "saveData: " + transactionCategory.getId());
        Log.d(TAG, "saveData: " + transactionWallet.getId());

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("transactionAmount", transactionAmount);
        transaction.put("transactionCategory", transactionCategory.getId());
        transaction.put("transactionNote", transactionNote);
        transaction.put("transactionDate", transactionDate);
        transaction.put("transactionWallet", transactionWallet.getId());

        progressDialog = new ProgressDialog(AddTransactionActivity.this);
        progressDialog.show();

        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .add(transaction)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        updateWallet(transactionWallet, transactionAmount);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateWallet(Wallet currWallet, Integer transactionAmount){
        progressDialog.show();

        db.collection("users")
                .document(currUser.getUid())
                .collection("wallets")
                .document(currWallet.getId())
                .update("walletAmount", FieldValue.increment(-transactionAmount))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void formatDate(){
        String myFormat="E, dd/MM/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        et_transaction_date.setText(dateFormat.format(calendar.getTime()));
    }

    private Transaction saveCurrentData(){
        Transaction transaction = new Transaction();
        if(!et_transaction_amount.getText().toString().isEmpty())
            transaction.setTransactionAmount(Integer.parseInt(et_transaction_amount.getText().toString().trim()));
        if(category != null)
            transaction.setTransactionCategory(category);
        if (!et_transaction_note.getText().toString().isEmpty())
            transaction.setTransactionNote(et_transaction_note.getText().toString().trim());
        if (!et_transaction_date.getText().toString().isEmpty())
            transaction.setTransactionDate(new Date(et_transaction_date.getText().toString().trim()));
        if(wallet != null)
            transaction.setTransactionWallet(wallet);
        return transaction;
    }
}