package com.example.tpamobile.activity.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.tpamobile.HomeActivity;
import com.example.tpamobile.R;
import com.example.tpamobile.model.Category;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CategoryDetailActivity extends AppCompatActivity {

    private EditText et_category_name;
    private RadioButton rb_income, rb_expense;
    private Button btn_save_category;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Category");

        category = (Category) getIntent().getSerializableExtra("currCategory");
        et_category_name = findViewById(R.id.et_category_name);
        rb_income = findViewById(R.id.rb_income);
        rb_expense = findViewById(R.id.rb_expense);
        btn_save_category = findViewById(R.id.btn_save_category);

        et_category_name.setText(category.getName());
        if(category.getType() == "income"){
            rb_income.setChecked(true);
        } else if (category.getType() == "expense"){
            rb_expense.setChecked(true);
        }

        progressDialog = new ProgressDialog(CategoryDetailActivity.this);
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
                .add(category)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
<<<<<<< Updated upstream
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
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
=======
                        Intent intent = new Intent(CategoryDetailActivity.this, HomeActivity.class);
                        intent.putExtra("fragmentToGo","category");
                        startActivity(intent);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent  = new Intent(CategoryDetailActivity.this, HomeActivity.class);
        intent.putExtra("fragmentToGo","category");
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
>>>>>>> Stashed changes
}