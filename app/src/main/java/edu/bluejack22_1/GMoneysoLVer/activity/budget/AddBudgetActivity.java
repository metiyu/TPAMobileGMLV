package edu.bluejack22_1.GMoneysoLVer.activity.budget;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.activity.main.HomeActivity;
import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.activity.transaction.SelectCategoryActivity;
import edu.bluejack22_1.GMoneysoLVer.model.Budget;
import edu.bluejack22_1.GMoneysoLVer.model.Category;
import edu.bluejack22_1.GMoneysoLVer.utilities.DateDisplayUtils;
import edu.bluejack22_1.GMoneysoLVer.widgets.SimpleDatePickerDialog;
import edu.bluejack22_1.GMoneysoLVer.widgets.SimpleDatePickerDialogFragment;

import edu.bluejack22_1.GMoneysoLVer.databinding.ActivityAddBudgetBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddBudgetActivity extends AppCompatActivity implements SimpleDatePickerDialog.OnDateSetListener, View.OnClickListener {

    ActivityAddBudgetBinding binding;
    EditText et_budget_amount, et_category, et_month;
    Button save_btn;
    ProgressDialog progressDialog;
    DatePickerDialog datePickerDialog;
    int iCurrentSelection;
    Category category;
    Budget budget;
    Calendar today_cal;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);
        binding = ActivityAddBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.budget));
        et_budget_amount = binding.etBudgetAmount;
        et_category = binding.etBudgetCategory;
        et_month = binding.etBudgetMonth;
        save_btn = binding.btnSave;
        budget = (Budget) getIntent().getSerializableExtra("currBudget");
        today_cal = Calendar.getInstance(Locale.getDefault());
        et_month.setText(today_cal.get(Calendar.MONTH)+1+"/"+today_cal.get(Calendar.YEAR));
        if(budget!=null){
            if(budget.getAmount()!=null){
                et_budget_amount.setText(budget.getAmount().toString());
            }
            if(budget.getCategory()!=null){
                et_category.setText(budget.getCategory().getName());
                category = budget.getCategory();
            }
            if(budget.getMonth()!=null && budget.getYear()!=null){
                et_month.setText(budget.getMonth().toString().toString()+"/"+budget.getYear().toString());
            }
        }
        if ((Category) getIntent().getSerializableExtra("selectedCategory") != null){
            category = (Category) getIntent().getSerializableExtra("selectedCategory");
            et_category.setText(category.getName());
        }
        et_category.setOnClickListener(x->{
            Intent intent = new Intent(AddBudgetActivity.this, SelectCategoryActivity.class);
            intent.putExtra("currBudget", (Serializable) saveCurrentData());
            startActivity(intent);
        });
        save_btn.setOnClickListener(x->{
            saveData();
        });
        et_month.setOnClickListener(this);
    }

    private void saveData(){
        Map<String, Object> budget = new HashMap<>();
        budget.put("budgetAmount", Integer.parseInt(et_budget_amount.getText().toString().trim()));
        budget.put("category", category.getId());
        int save_month = Integer.parseInt(et_month.getText().toString().split("/")[0]);
        if(save_month>12){
            save_month%=12;
        }
        budget.put("month", save_month);
        budget.put("year", Integer.parseInt(et_month.getText().toString().split("/")[1]));

        db.collection("users")
            .document(currUser.getUid())
            .collection("budgets")
                .add(budget)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddBudgetActivity.this, HomeActivity.class);
                        intent.putExtra("fragmentToGo", "budget");
                        startActivity(intent);
                    }
                });
    }
    private void displaySimpleDatePickerDialogFragment() {
        SimpleDatePickerDialogFragment datePickerDialogFragment;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        datePickerDialogFragment.setOnDateSetListener(this);
        datePickerDialogFragment.show(getSupportFragmentManager(), null);
    }
    private Budget saveCurrentData(){
        Budget budget = new Budget();
        if(!et_budget_amount.getText().toString().isEmpty())
            budget.setAmount(Integer.parseInt(et_budget_amount.getText().toString().trim()));
        if(category != null)
            budget.setCategory(category);
        if (!et_month.getText().toString().isEmpty()){
            budget.setMonth(Integer.parseInt(et_month.getText().toString().split("/")[0]));
            budget.setYear(Integer.parseInt(et_month.getText().toString().split("/")[1]));
        }

        return budget;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.et_budget_month) {
            displaySimpleDatePickerDialogFragment();
        }
    }

    @Override
    public void onDateSet(int year, int monthOfYear) {
        et_month.setText(DateDisplayUtils.formatMonthYear(year, monthOfYear));
    }
}