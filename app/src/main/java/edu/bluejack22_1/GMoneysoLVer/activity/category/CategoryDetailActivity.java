package edu.bluejack22_1.GMoneysoLVer.activity.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.activity.main.HomeActivity;
import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class CategoryDetailActivity extends AppCompatActivity {

    private EditText et_category_name;
    private RadioButton rb_income, rb_expense;
    private Button btn_edit_category, btn_delete_category;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.category));

        category = (Category) getIntent().getSerializableExtra("currCategory");
        et_category_name = findViewById(R.id.et_category_name);
        rb_income = findViewById(R.id.rb_income);
        rb_expense = findViewById(R.id.rb_expense);
        btn_edit_category = findViewById(R.id.btn_edit_category);
        btn_delete_category = findViewById(R.id.btn_delete_category);

        et_category_name.setText(category.getName());
        if(category.getType().equals("income")){
            rb_income.setChecked(true);
        } else if (category.getType().equals("expense")){
            rb_expense.setChecked(true);
        }

        et_category_name.setFocusable(false);
        rb_expense.setEnabled(false);
        rb_income.setEnabled(false);

        progressDialog = new ProgressDialog(CategoryDetailActivity.this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.saving));

        btn_edit_category.setOnClickListener(x -> {
            Intent intent = new Intent(CategoryDetailActivity.this, EditCategoryActivity.class);
            intent.putExtra("currCategory", category);
            startActivity(intent);
        });

        btn_delete_category.setOnClickListener(x -> {
            deleteData(category.getId());
        });
    }

    private void deleteData(String id){
        progressDialog.show();
        db.collection("categories")
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(CategoryDetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CategoryDetailActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CategoryDetailActivity.this, HomeActivity.class);
                        intent.putExtra("fragmentToGo","category");
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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

}