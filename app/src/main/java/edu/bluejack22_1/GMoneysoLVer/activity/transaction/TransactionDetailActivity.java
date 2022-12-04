package edu.bluejack22_1.GMoneysoLVer.activity.transaction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.R;

import edu.bluejack22_1.GMoneysoLVer.model.Transaction;
import edu.bluejack22_1.GMoneysoLVer.model.Wallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionDetailActivity extends AppCompatActivity {

    private TextView tv_category_name, tv_transaction_amount, tv_transaction_date, tv_transaction_wallet;
    private Button btn_edit_transaction, btn_delete_transaction;
    private Transaction transaction;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.saving));

        tv_category_name = findViewById(R.id.tv_category_name);
        tv_transaction_amount = findViewById(R.id.tv_transaction_amount);
        tv_transaction_date = findViewById(R.id.tv_transaction_date);
        tv_transaction_wallet = findViewById(R.id.tv_transaction_wallet);
        btn_edit_transaction = findViewById(R.id.btn_edit_transaction);
        btn_delete_transaction = findViewById(R.id.btn_delete_transaction);

        transaction = (Transaction) getIntent().getSerializableExtra("currTransaction");

        tv_category_name.setText(transaction.getTransactionCategory().getName());
        tv_transaction_amount.setText(transaction.formatRupiah());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy");
        tv_transaction_date.setText(dateFormat.format(transaction.getTransactionDate()));
        tv_transaction_wallet.setText(transaction.getTransactionWallet().getName());

        if (transaction.getTransactionCategory().getType().equals("expense"))
            tv_transaction_amount.setTextColor(ContextCompat.getColor(this, R.color.expenseColor));
        else
            tv_transaction_amount.setTextColor(ContextCompat.getColor(this, R.color.incomeColor));

        btn_edit_transaction.setOnClickListener(x -> {
            Intent intent = new Intent(this, EditTransactionActivity.class);
            intent.putExtra("currTransaction", transaction);
            startActivity(intent);
        });

        btn_delete_transaction.setOnClickListener(x -> {
            deleteData(transaction.getTransactionID(), transaction.getTransactionDate());
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return true;
    }

    private void deleteData(String id, Date date){
        progressDialog.show();
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .document(new SimpleDateFormat("yyyy").format(date))
                .collection("monthList")
                .document(new SimpleDateFormat("MM").format(date))
                .collection("dateList")
                .document(new SimpleDateFormat("dd").format(date))
                .collection("transactionList")
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(TransactionDetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TransactionDetailActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        updateWallet(transaction.getTransactionWallet(), transaction.getTransactionAmount(), transaction.getTransactionCategory().getType());
                        TransactionDetailActivity.this.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(TransactionDetailActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        TransactionDetailActivity.this.finish();
                    }
                });
    }

    private void updateWallet(Wallet currWallet, Integer transactionAmount, String transactionCategoryType){
        progressDialog.show();

        if (transactionCategoryType.equals("expense")){
            db.collection("users")
                    .document(currUser.getUid())
                    .collection("wallets")
                    .document(currWallet.getId())
                    .update("walletAmount", FieldValue.increment(transactionAmount))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        } else if (transactionCategoryType.equals("income")){
            db.collection("users")
                    .document(currUser.getUid())
                    .collection("wallets")
                    .document(currWallet.getId())
                    .update("walletAmount", FieldValue.increment(-transactionAmount))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}