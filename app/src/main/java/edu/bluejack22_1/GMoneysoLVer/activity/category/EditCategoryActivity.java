package edu.bluejack22_1.GMoneysoLVer.activity.category;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;

import android.util.Log;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


import edu.bluejack22_1.GMoneysoLVer.R;

import edu.bluejack22_1.GMoneysoLVer.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EditCategoryActivity extends AppCompatActivity {

    private EditText et_category_name;
    private RadioButton rb_income, rb_expense;
    private Button btn_save_category;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.category));

        category = (Category) getIntent().getSerializableExtra("currCategory");
        et_category_name = findViewById(R.id.et_category_name);
        rb_income = findViewById(R.id.rb_income);
        rb_expense = findViewById(R.id.rb_expense);
        btn_save_category = findViewById(R.id.btn_save_category);

        et_category_name.setText(category.getName());
        if(category.getType().equals("income")){
            rb_income.setChecked(true);
        } else if (category.getType().equals("expense")){
            rb_expense.setChecked(true);
        }

        progressDialog = new ProgressDialog(EditCategoryActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Saving...");

        btn_save_category.setOnClickListener(x -> {
            String categoryName = et_category_name.getText().toString().trim();
            String categoryType;
            if(rb_income.isChecked()) {
                categoryType = "income";
            } else if (rb_expense.isChecked()){
                categoryType = "expense";
            } else {
                return;
            }

            saveData(categoryName, categoryType);
        });
    }

    private void saveData(String categoryName, String categoryType) {
        Map<String, Object> category = new HashMap<>();
        category.put("categoryName", categoryName);
        category.put("categoryType", categoryType);

        progressDialog.show();

        db.collection("categories")
                .document(this.category.getId())
                .set(category)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        getData();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent  = new Intent(EditCategoryActivity.this, CategoryDetailActivity.class);
        intent.putExtra("currCategory", category);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private void getData(){
        progressDialog.show();
        db.collection("categories")
                .document(category.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            Intent intent  = new Intent(EditCategoryActivity.this, CategoryDetailActivity.class);
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("document", "DocumentSnapshot data: " + document.getData());
                                Category category = new Category(document.getId(), document.getString("categoryName"), document.getString("categoryType"));
                                intent.putExtra("currCategory", (Serializable) category);
                                startActivity(intent);
                            }

                        }
                    }
                });
    }

}