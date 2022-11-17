package com.example.tpamobile.activity.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.tpamobile.R;
import com.example.tpamobile.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private void saveData(String categoryName, String categoryType){
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
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}