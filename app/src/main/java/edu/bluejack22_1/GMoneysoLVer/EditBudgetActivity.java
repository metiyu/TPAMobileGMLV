package edu.bluejack22_1.GMoneysoLVer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tpamobile.R;

import edu.bluejack22_1.GMoneysoLVer.activity.budget.BudgetDetailActivity;
import edu.bluejack22_1.GMoneysoLVer.activity.transaction.SelectCategoryActivity;
import edu.bluejack22_1.GMoneysoLVer.model.Budget;
import edu.bluejack22_1.GMoneysoLVer.model.Category;

import com.example.tpamobile.databinding.ActivityEditBudgetBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EditBudgetActivity extends AppCompatActivity {

    ActivityEditBudgetBinding binding;
    EditText et_budget_amount, et_budget_category;
    Button save_btn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private Budget budget;
    private Category category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        et_budget_amount = binding.etBudgetAmount;
        et_budget_category = binding.etBudgetCategory;
        save_btn = binding.btnSaveBudget;
        budget = (Budget) getIntent().getSerializableExtra("currBudget");
        et_budget_category.setText(budget.getCategory().getName());
        et_budget_amount.setText(budget.getAmount().toString());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.budget));
        save_btn.setOnClickListener(x->{
            saveUpdate();
        });
        et_budget_category.setOnClickListener(x -> {
            Intent intent = new Intent(EditBudgetActivity.this, SelectCategoryActivity.class);
            intent.putExtra("currBudget", (Serializable)  saveCurrentData());
            intent.putExtra("fromEditBudget", "true");
            startActivity(intent);
        });
        if ((Category) getIntent().getSerializableExtra("selectedCategory") != null){
            category = (Category) getIntent().getSerializableExtra("selectedCategory");
            budget.getCategory().setId(category.getId());
            et_budget_category.setText(category.getName());
        }    }
    public void saveUpdate(){
        Map<String, Object> new_budget = new HashMap<>();
        new_budget.put("budgetAmount", Integer.parseInt(et_budget_amount.getText().toString().trim()));
        new_budget.put("category", budget.getCategory().getId());
        new_budget.put("month", budget.getMonth());
        new_budget.put("year", budget.getYear());
        db.collection("users")
                .document(currUser.getUid())
                .collection("budgets")
                .document(budget.getId())
                .set(new_budget)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                        }
                        getData();
                    }
                });
    }

    public void getData(){
        db.collection("users")
                .document(currUser.getUid())
                .collection("budgets")
                .document(budget.getId())
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Intent intent  = new Intent(EditBudgetActivity.this, BudgetDetailActivity.class);
                                DocumentSnapshot doc = task.getResult();
                                Budget new_budget = new Budget();
                                new_budget.setId(doc.getId());
                                new_budget.setMonth(doc.getLong("month").intValue());
                                new_budget.setYear(doc.getLong("year").intValue());
                                new_budget.setAmount(doc.getLong("budgetAmount").intValue());
                                new_budget.setCategory(new Category());
                                new_budget.getCategory().setId(doc.getString("category"));
                                getCategoryData(new_budget);

                            }
                        }
                );
    }
    public void getCategoryData(Budget new_budget){
        db.collection("categories")
                .document(new_budget.getCategory().getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot catDoc = task.getResult();
                        new_budget.getCategory().setId(catDoc.getId());
                        new_budget.getCategory().setName(catDoc.getString("categoryName"));
                        new_budget.getCategory().setType(catDoc.getString("categoryType"));
                        Intent intent = new Intent(EditBudgetActivity.this, BudgetDetailActivity.class);
                        intent.putExtra("currBudget", (Serializable) new_budget);
                        startActivity(intent);
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return true;
    }
    public Budget saveCurrentData(){
        if(et_budget_amount.getText()!=null){
            this.budget.setAmount(Integer.parseInt(et_budget_amount.getText().toString().trim()));
        }
        return this.budget;
    }
}
