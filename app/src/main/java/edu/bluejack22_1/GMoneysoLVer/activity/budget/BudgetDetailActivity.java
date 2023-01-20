package edu.bluejack22_1.GMoneysoLVer.activity.budget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.activity.main.HomeActivity;
import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.activity.transaction.adapter.TransactionAdapter;
import edu.bluejack22_1.GMoneysoLVer.activity.transaction.adapter.TransactionPerCategoryAdapter;
import edu.bluejack22_1.GMoneysoLVer.databinding.ActivityBudgetDetailBinding;
import edu.bluejack22_1.GMoneysoLVer.model.Budget;
import edu.bluejack22_1.GMoneysoLVer.model.Category;
import edu.bluejack22_1.GMoneysoLVer.model.Transaction;
import edu.bluejack22_1.GMoneysoLVer.model.TransactionGroupByDate;
import edu.bluejack22_1.GMoneysoLVer.model.Wallet;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

public class BudgetDetailActivity extends AppCompatActivity {

    ActivityBudgetDetailBinding binding;
    EditText et_budget_amount, et_budget_category;
    Button btn_delete, btn_edit;
    private Budget budget;
    ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private RecyclerView rv_transactions;
    private List<TransactionGroupByDate> transactionGroupByDateList = new ArrayList<>();
    private List<Transaction> transactionList = new ArrayList<>();
    private Category currCategory;
    private Wallet currWallet;
    private Transaction transaction = new Transaction();
    private TransactionAdapter adapterPerDate;
    private TransactionPerCategoryAdapter adapterPerCategory;

    private String TAG = "BudgetDetailActivity";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.budget));
        binding = ActivityBudgetDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        budget = (Budget) getIntent().getSerializableExtra("currBudget");
        et_budget_amount = binding.etBudgetAmount;
        et_budget_category = binding.etBudgetCategory;
        btn_delete = binding.btnDeleteBudget;
        btn_edit = binding.btnEditBudget;
        et_budget_amount.setText(budget.getAmount().toString().trim());
        et_budget_category.setText(budget.getCategory().getName());
        btn_delete.setOnClickListener(x -> {
            deleteBudget(budget.getId());
        });
        btn_edit.setOnClickListener(x -> {
            Intent intent = new Intent(BudgetDetailActivity.this, EditBudgetActivity.class);
            intent.putExtra("currBudget", budget);
            startActivity(intent);
        });
        progressDialog = new ProgressDialog(BudgetDetailActivity.this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.saving));

        Log.d(TAG, "onCreate: tran size, " + budget.getTransactionList().size());
        for (Transaction t : budget.getTransactionList()) {
            Log.d(TAG, "onCreate: tran id, " + t.getTransactionID());
            Log.d(TAG, "onCreate: tran id, " + t.getTransactionAmount());
            Log.d(TAG, "onCreate: tran id, " + t.getTransactionCategory().getName());
        }

        rv_transactions = findViewById(R.id.rv_transactions);
        adapterPerDate = new TransactionAdapter(BudgetDetailActivity.this, transactionGroupByDateList, adapterPerCategory);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BudgetDetailActivity.this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(BudgetDetailActivity.this, DividerItemDecoration.VERTICAL);
        rv_transactions.setLayoutManager(layoutManager);
        rv_transactions.addItemDecoration(decoration);
        rv_transactions.setAdapter(adapterPerDate);

        getTransactionData();
    }

    private void getTransactionData() {
        db.collection("users")
                .document(currUser.getUid())
                .collection("budgets")
                .document(budget.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ArrayList<DocumentReference> tranPaths = new ArrayList<>();
                                if(document.get("transactionList") != null){
                                    tranPaths = (ArrayList<DocumentReference>) document.get("transactionList");
                                    Calendar calendar = Calendar.getInstance();
                                    Log.d(TAG, "onComplete: budget month, " + budget.getMonth());
                                    Log.d(TAG, "onComplete: budget year, " + budget.getYear());
                                    calendar.set(Calendar.MONTH, budget.getMonth()-1);
                                    calendar.set(Calendar.YEAR, budget.getYear());
                                    Log.d(TAG, "onComplete: calendar time, " + calendar.getTime());
                                    TransactionGroupByDate transactionGroupByDate = new TransactionGroupByDate(calendar.getTime(), transactionList);
                                    transactionGroupByDateList.add(transactionGroupByDate);
                                    adapterPerCategory = new TransactionPerCategoryAdapter(BudgetDetailActivity.this, transactionList);
                                    for (DocumentReference s : tranPaths) {
                                        db.document(s.getPath())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                db.collection("categories")
                                                                        .document(document.getString("transactionCategory"))
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    DocumentSnapshot document = task.getResult();
                                                                                    if (document.exists()) {
                                                                                        currCategory = new Category(document.getId(), document.getString("categoryName"), document.getString("categoryType"));
                                                                                        transaction.setTransactionCategory(currCategory);
                                                                                    }
                                                                                }

                                                                                db.collection("users")
                                                                                        .document(currUser.getUid())
                                                                                        .collection("wallets")
                                                                                        .document(document.getString("transactionWallet"))
                                                                                        .get()
                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    DocumentSnapshot document = task.getResult();
                                                                                                    if (document.exists()) {
                                                                                                        currWallet = new Wallet(document.getId(), document.getString("walletName"), document.getLong("walletAmount").intValue());
                                                                                                        transaction.setTransactionWallet(currWallet);
                                                                                                    }
                                                                                                }
                                                                                                transaction.setTransactionID(document.getId());
                                                                                                transaction.setTransactionAmount(document.getLong("transactionAmount").intValue());
                                                                                                transaction.setTransactionDate(document.getDate("transactionDate"));
                                                                                                transactionList.add(transaction);

                                                                                                adapterPerCategory.notifyDataSetChanged();
                                                                                                adapterPerDate.notifyDataSetChanged();

                                                                                                Log.d(TAG, "onComplete: tran group, " + transactionGroupByDate.getDate());
                                                                                                Log.d(TAG, "onComplete: tran, " + transaction.getTransactionID());
                                                                                            }
                                                                                        });
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(BudgetDetailActivity.this, getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                });
                                    }
                                    adapterPerDate.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(BudgetDetailActivity.this, getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
    }

    public void deleteBudget(String id) {
        progressDialog.show();
        Log.d("budget delete", "deleteBudget: " + budget.getId());
        db.collection("users")
                .document(currUser.getUid())
                .collection("budgets")
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(getString(R.string.success), "onComplete: success delete");
                            Toast.makeText(BudgetDetailActivity.this, getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("failed", "onComplete: failed");
                            Toast.makeText(BudgetDetailActivity.this, getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                        finish();
//                        Intent intent = new Intent(BudgetDetailActivity.this, HomeActivity.class);
//                        intent.putExtra("fragmentToGo","budget");
//                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BudgetDetailActivity.this, HomeActivity.class);
                        intent.putExtra("fragmentToGo", "budget");
                        startActivity(intent);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return true;
    }
}