package edu.bluejack22_1.GMoneysoLVer.activity.transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tpamobile.R;
import edu.bluejack22_1.GMoneysoLVer.model.Category;
import edu.bluejack22_1.GMoneysoLVer.model.Transaction;
import edu.bluejack22_1.GMoneysoLVer.model.Wallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditTransactionActivity extends AppCompatActivity {

    private TextInputLayout til_transaction_amount, til_transaction_category, til_transaction_date, til_transaction_wallet;
    private EditText et_transaction_amount, et_transaction_category, et_transaction_date, et_transaction_wallet;
    private Button btn_save_transaction;
    private Transaction transaction;
    private Category category;
    private Wallet wallet;
    private Date dateFromET;
    private Calendar calendar = Calendar.getInstance();
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaction);

        til_transaction_amount = findViewById(R.id.til_transaction_amount);
        til_transaction_category = findViewById(R.id.til_transaction_category);
        til_transaction_date = findViewById(R.id.til_transaction_date);
        til_transaction_wallet = findViewById(R.id.til_transaction_wallet);
        et_transaction_amount = findViewById(R.id.et_transaction_amount);
        et_transaction_category = findViewById(R.id.et_transaction_category);
        et_transaction_date = findViewById(R.id.et_transaction_date);
        et_transaction_wallet = findViewById(R.id.et_transaction_wallet);
        btn_save_transaction = findViewById(R.id.btn_save_transaction);

        transaction = (Transaction) getIntent().getSerializableExtra("currTransaction");

        if (transaction != null) {
            if (transaction.getTransactionAmount() != null)
                et_transaction_amount.setText(transaction.getTransactionAmount().toString());
            if (transaction.getTransactionCategory() != null) {
                et_transaction_category.setText(transaction.getTransactionCategory().getName());
                category = transaction.getTransactionCategory();
            }
            if (transaction.getTransactionDate() != null) {
                dateFromET = transaction.getTransactionDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(transaction.getTransactionDate());
                formatDate(calendar);
            }
            if (transaction.getTransactionWallet() != null) {
                et_transaction_wallet.setText(transaction.getTransactionWallet().getName());
                wallet = transaction.getTransactionWallet();
            }
        }

        et_transaction_category.setOnClickListener(x -> {
            Intent intent = new Intent(this, SelectCategoryActivity.class);
            intent.putExtra("currTransaction", saveCurrentData());
            startActivity(intent);
        });
        if ((Category) getIntent().getSerializableExtra("selectedCategory") != null) {
            category = (Category) getIntent().getSerializableExtra("selectedCategory");
            et_transaction_category.setText(category.getName());
        }

        et_transaction_wallet.setOnClickListener(x -> {
            Intent intent = new Intent(this, SelectWalletActivity.class);
            intent.putExtra("currTransaction", saveCurrentData());
            startActivity(intent);
        });
        if ((Wallet) getIntent().getSerializableExtra("selectedWallet") != null) {
            wallet = (Wallet) getIntent().getSerializableExtra("selectedWallet");
            et_transaction_wallet.setText(wallet.getName());
        }

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                formatDate(calendar);
                dateFromET = calendar.getTime();
            }
        };

        et_transaction_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myFormat = "E, dd/MM/yy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                DatePickerDialog dialog = new DatePickerDialog(EditTransactionActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        btn_save_transaction.setOnClickListener(x -> {
            Integer transactionAmount = Integer.parseInt(et_transaction_amount.getText().toString().trim());
            Category transactionCategory = this.category;
            Wallet transactionWallet = this.wallet;

            saveData(transactionAmount, transactionCategory, dateFromET, transactionWallet);
        });
    }

    private void saveData(Integer transactionAmount, Category transactionCategory, Date transactionDate, Wallet transactionWallet) {
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("transactionAmount", transactionAmount);
        transaction.put("transactionCategory", transactionCategory.getId());
        transaction.put("transactionDate", transactionDate);
        transaction.put("transactionWallet", transactionWallet.getId());
        transaction.put("createdAt", new Date());

        progressDialog = new ProgressDialog(EditTransactionActivity.this);
        progressDialog.show();

        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .document(new SimpleDateFormat("yyyy").format(transactionDate))
                .collection("monthList")
                .document(new SimpleDateFormat("MM").format(transactionDate))
                .collection("dateList")
                .document(new SimpleDateFormat("dd").format(transactionDate))
                .collection("transactionList")
                .document(this.transaction.getTransactionID())
                .set(transaction)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            updateWallet(transactionWallet, transactionAmount, transactionCategory.getType());
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Map<String, Object> rubbish = new HashMap<>();
        rubbish.put("tes", "tes");
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .document(new SimpleDateFormat("yyyy").format(transactionDate))
                .set(rubbish)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .document(new SimpleDateFormat("yyyy").format(transactionDate))
                .collection("monthList")
                .document(new SimpleDateFormat("MM").format(transactionDate))
                .set(rubbish)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .document(new SimpleDateFormat("yyyy").format(transactionDate))
                .collection("monthList")
                .document(new SimpleDateFormat("MM").format(transactionDate))
                .collection("dateList")
                .document(new SimpleDateFormat("dd").format(transactionDate))
                .set(rubbish)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    private void updateWallet(Wallet currWallet, Integer transactionAmount, String transactionCategoryType) {
        progressDialog.show();

        if (transactionCategoryType.equals("expense")) {
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
        } else if (transactionCategoryType.equals("income")) {
            db.collection("users")
                    .document(currUser.getUid())
                    .collection("wallets")
                    .document(currWallet.getId())
                    .update("walletAmount", FieldValue.increment(transactionAmount))
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
    }

    private void formatDate(Calendar calendar) {
        String myFormat = "E, dd/MM/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        et_transaction_date.setText(dateFormat.format(calendar.getTime()));
    }

    private Transaction saveCurrentData() {
        Transaction transaction = new Transaction();
        if (!et_transaction_amount.getText().toString().isEmpty())
            transaction.setTransactionAmount(Integer.parseInt(et_transaction_amount.getText().toString().trim()));
        if (category != null)
            transaction.setTransactionCategory(category);
        if (!et_transaction_date.getText().toString().isEmpty()) {
            transaction.setTransactionDate(dateFromET);
        }
        if (wallet != null)
            transaction.setTransactionWallet(wallet);
        return transaction;
    }
}