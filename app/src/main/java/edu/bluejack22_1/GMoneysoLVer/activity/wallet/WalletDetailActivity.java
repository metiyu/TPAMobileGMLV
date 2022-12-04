package edu.bluejack22_1.GMoneysoLVer.activity.wallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.HomeActivity;
import edu.bluejack22_1.GMoneysoLVer.R;

import edu.bluejack22_1.GMoneysoLVer.model.Wallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class WalletDetailActivity extends AppCompatActivity {

    private EditText et_wallet_name, et_wallet_amount;
    private Button btn_edit_wallet, btn_delete_wallet;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private Wallet wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.wallet));

        wallet = (Wallet) getIntent().getSerializableExtra("currWallet");
        et_wallet_name = findViewById(R.id.et_wallet_name);
        et_wallet_amount = findViewById(R.id.et_wallet_amount);
        btn_edit_wallet = findViewById(R.id.btn_edit_wallet);
        btn_delete_wallet = findViewById(R.id.btn_delete_wallet);

        et_wallet_name.setText(wallet.getName());
        et_wallet_amount.setText(wallet.formatRupiah());

        et_wallet_name.setFocusable(false);
        et_wallet_amount.setFocusable(false);

        progressDialog = new ProgressDialog(WalletDetailActivity.this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.saving));

        btn_edit_wallet.setOnClickListener(x -> {
            Intent intent = new Intent(WalletDetailActivity.this, EditWalletActivity.class);
            intent.putExtra("currWallet", wallet);
            startActivity(intent);
        });

        btn_delete_wallet.setOnClickListener(x -> {
            deleteData(wallet.getId());
        });
    }

    private void deleteData(String id){
        progressDialog.show();
        db.collection("users")
                .document(currUser.getUid())
                .collection("wallets")
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(WalletDetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WalletDetailActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        Intent intent = new Intent(WalletDetailActivity.this, HomeActivity.class);
                        intent.putExtra("fragmentToGo","wallet");
                        startActivity(intent);
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent  = new Intent(WalletDetailActivity.this, HomeActivity.class);
        intent.putExtra("fragmentToGo","wallet");
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}