package com.example.tpamobile.activity.budget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tpamobile.EditBudgetActivity;
import com.example.tpamobile.HomeActivity;
import com.example.tpamobile.R;
import com.example.tpamobile.activity.category.CategoryDetailActivity;
import com.example.tpamobile.databinding.ActivityBudgetDetailBinding;
import com.example.tpamobile.model.Budget;
import com.example.tpamobile.model.Transaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class BudgetDetailActivity extends AppCompatActivity {

    ActivityBudgetDetailBinding binding;
    EditText et_budget_amount, et_budget_category;
    Button btn_delete, btn_edit;
    private Budget budget;
    ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

    private String TAG = "BudgetDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Budget");
        binding = ActivityBudgetDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        budget = (Budget) getIntent().getSerializableExtra("currBudget");
        et_budget_amount = binding.etBudgetAmount;
        et_budget_category = binding.etBudgetCategory;
        btn_delete = binding.btnDeleteBudget;
        btn_edit = binding.btnEditBudget;
        et_budget_amount.setText(budget.getAmount().toString().trim());
        et_budget_category.setText(budget.getCategory().getName());
        btn_delete.setOnClickListener(x->{
            deleteBudget(budget.getId());
        });
        btn_edit.setOnClickListener(x->{
            Intent intent = new Intent(BudgetDetailActivity.this, EditBudgetActivity.class);
            intent.putExtra("currBudget", budget);
            startActivity(intent);
        });
        progressDialog = new ProgressDialog(BudgetDetailActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Saving...");

        Log.d(TAG, "onCreate: tran size, " + budget.getTransactionList().size());
        for (Transaction t : budget.getTransactionList()){
            Log.d(TAG, "onCreate: tran id, " + t.getTransactionID());
            Log.d(TAG, "onCreate: tran id, " + t.getTransactionAmount());
            Log.d(TAG, "onCreate: tran id, " + t.getTransactionCategory().getName());
        }
    }
    public void deleteBudget(String id){
        progressDialog.show();
        Log.d("budget delete", "deleteBudget: "+budget.getId());
        db.collection("users")
                .document(currUser.getUid())
                .collection("budgets")
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("success", "onComplete: success delete");
                            Toast.makeText(BudgetDetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("failed", "onComplete: failed");
                            Toast.makeText(BudgetDetailActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BudgetDetailActivity.this, HomeActivity.class);
                        intent.putExtra("fragmentToGo","budget");
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