package com.example.tpamobile.activity.category;

<<<<<<< Updated upstream
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
=======
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
>>>>>>> Stashed changes

import com.example.tpamobile.HomeActivity;
import com.example.tpamobile.R;
<<<<<<< Updated upstream
=======
import com.example.tpamobile.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
>>>>>>> Stashed changes

public class EditCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);
<<<<<<< Updated upstream
=======
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Category");
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
                .add(category)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        Intent intent = new Intent(EditCategoryActivity.this, HomeActivity.class);
//                        intent.putExtra("fragmentToGo","category");
//                        startActivity(intent);
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

>>>>>>> Stashed changes
    }
}