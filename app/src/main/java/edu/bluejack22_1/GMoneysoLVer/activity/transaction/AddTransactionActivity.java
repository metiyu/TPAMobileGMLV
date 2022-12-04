package edu.bluejack22_1.GMoneysoLVer.activity.transaction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.activity.main.HomeActivity;
import edu.bluejack22_1.GMoneysoLVer.R;

import edu.bluejack22_1.GMoneysoLVer.model.Category;
import edu.bluejack22_1.GMoneysoLVer.model.Transaction;
import edu.bluejack22_1.GMoneysoLVer.model.Wallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTransactionActivity extends AppCompatActivity {

    private TextInputLayout til_transaction_amount, til_transaction_category, til_transaction_date, til_transaction_wallet;
    private EditText et_transaction_amount, et_transaction_category, et_transaction_date, et_transaction_wallet;
    private Button btn_save_transaction;
    private Calendar calendar = Calendar.getInstance();
    private Category category;
    private Wallet wallet;
    private Transaction transaction;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

    private Date dateFromET;

    private final String TAG = "MAKE TRANSACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.add_transaction));
        if (transaction != null) {
            Log.d(TAG, "onCreate: date, " + transaction.getTransactionDate());
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
            Intent intent = new Intent(AddTransactionActivity.this, SelectCategoryActivity.class);
            intent.putExtra("currTransaction", saveCurrentData());
            startActivity(intent);
        });
        if ((Category) getIntent().getSerializableExtra("selectedCategory") != null) {
            category = (Category) getIntent().getSerializableExtra("selectedCategory");
            et_transaction_category.setText(category.getName());
        }

        et_transaction_wallet.setOnClickListener(x -> {
            Intent intent = new Intent(AddTransactionActivity.this, SelectWalletActivity.class);
            intent.putExtra("currTransaction", saveCurrentData());
            startActivity(intent);
        });
        if ((Wallet) getIntent().getSerializableExtra("selectedWallet") != null) {
            wallet = (Wallet) getIntent().getSerializableExtra("selectedWallet");
            et_transaction_wallet.setText(wallet.getName());
        }

//        et_transaction_note.setOnClickListener(x -> {
//            Intent intent = new Intent(AddTransactionActivity.this, WriteNoteActivity.class);
//            intent.putExtra("currTransaction", saveCurrentData());
//            startActivity(intent);
//        });
//        if (getIntent().getStringExtra("transactionNote") != null) {
//            et_transaction_note.setText(getIntent().getStringExtra("transactionNote"));
//        }

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
                DatePickerDialog dialog = new DatePickerDialog(AddTransactionActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        btn_save_transaction.setOnClickListener(x -> {
            Integer transactionAmount = Integer.parseInt(et_transaction_amount.getText().toString().trim());
            Category transactionCategory = this.category;
//            String transactionNote = et_transaction_note.getText().toString().trim();
            Wallet transactionWallet = this.wallet;

            saveData(transactionAmount, transactionCategory, dateFromET, transactionWallet);
        });
    }

    private void checkBudget(Integer transactionAmount, Category transactionCategory, String transactionNote, Date transactionDate, Wallet transactionWallet) {
        db.collection("users")
                .document(currUser.getUid())
                .collection("budgets")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(AddTransactionActivity.this, getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getString("category") != null) {
                                if (snapshot.getString("category").equals(transactionCategory)) {
                                    db.collection("users")
                                            .document(currUser.getUid())
                                            .collection("budgets")
                                            .document(snapshot.getId())
                                            .update("budgetAmount", FieldValue.increment(-transactionAmount))
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    db.collection("users")
                                            .document(currUser.getUid())
                                            .collection("budgets")
                                            .document(snapshot.getId())
                                            .update("transactionList", FieldValue.arrayUnion())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }

    private void saveData(Integer transactionAmount, Category transactionCategory, Date transactionDate, Wallet transactionWallet) {
        Log.d(TAG, "saveData: " + transactionCategory.getId());
        Log.d(TAG, "saveData: " + transactionWallet.getId());

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("transactionAmount", transactionAmount);
        transaction.put("transactionCategory", transactionCategory.getId());
        transaction.put("transactionDate", transactionDate);
        transaction.put("transactionWallet", transactionWallet.getId());
        transaction.put("createdAt", new Date());

        Map<String, Object> notification = new HashMap<>();
        notification.put("message", "new transaction succesfully added");

        progressDialog = new ProgressDialog(AddTransactionActivity.this);
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
                .add(transaction)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                        updateWallet(transactionWallet, transactionAmount, transactionCategory.getType());
                        db.collection("users")
                                .document(currUser.getUid())
                                .collection("notifications")
                                .add(notification)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        db.collection("users")
                                                .document(currUser.getUid())
                                                .collection("budgets")
                                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                        if (error != null) {
                                                            Toast.makeText(AddTransactionActivity.this, getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                        for (QueryDocumentSnapshot snapshot : value) {
                                                            if (snapshot.getString("category") != null) {
                                                                if (snapshot.getString("category").equals(transactionCategory.getId())) {
                                                                    Log.d(TAG, "onEvent: id doc, " + documentReference.getId());
                                                                    db.collection("users")
                                                                            .document(currUser.getUid())
                                                                            .collection("budgets")
                                                                            .document(snapshot.getId())
                                                                            .update("transactionList", FieldValue.arrayUnion(documentReference))
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                                                                                        startActivity(new Intent(AddTransactionActivity.this, HomeActivity.class));
                                                                                        finish();
                                                                                    } else {
                                                                                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                    startActivity(new Intent(AddTransactionActivity.this, HomeActivity.class));
//                                                                                    finish();

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        }
//                                                        startActivity(new Intent(AddTransactionActivity.this, HomeActivity.class));
//                                                        finish();
                                                    }
                                                });
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
//        Intent intent = new Intent(AddTransactionActivity.this, HomeActivity.class);
//        startActivity(intent);
//        return super.onOptionsItemSelected(item);
        this.finish();
        return true;
    }
}