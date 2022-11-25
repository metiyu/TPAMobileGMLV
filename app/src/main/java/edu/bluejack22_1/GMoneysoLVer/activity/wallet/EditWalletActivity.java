package edu.bluejack22_1.GMoneysoLVer.activity.wallet;

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

import com.example.tpamobile.R;

import edu.bluejack22_1.GMoneysoLVer.model.Wallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EditWalletActivity extends AppCompatActivity {

    EditText et_wallet_name, et_wallet_amount;
    Button btn_save_wallet;
    ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private Wallet wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wallet);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Wallet");
        wallet = (Wallet) getIntent().getSerializableExtra("currWallet");
        et_wallet_name = findViewById(R.id.et_wallet_name);
        et_wallet_amount = findViewById(R.id.et_wallet_amount);
        btn_save_wallet = findViewById(R.id.btn_save_wallet);

        et_wallet_name.setText(wallet.getName());
        et_wallet_amount.setText(wallet.getAmount().toString());

        progressDialog = new ProgressDialog(EditWalletActivity.this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.saving));

        btn_save_wallet.setOnClickListener(x -> {
            String walletName = et_wallet_name.getText().toString().trim();
            Integer walletAmount = Integer.parseInt(et_wallet_amount.getText().toString().trim());

            saveData(walletName, walletAmount);
        });
    }

    private void saveData(String walletName, Integer walletAmount){
        Map<String, Object> wallet = new HashMap<>();
        wallet.put("walletName", walletName);
        wallet.put("walletAmount", walletAmount);

        progressDialog.show();

        db.collection("users")
                .document(currUser.getUid())
                .collection("wallets")
                .document(this.wallet.getId())
                .set(wallet)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                        getData();

                    }
                });
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent  = new Intent(EditWalletActivity.this, WalletDetailActivity.class);
        intent.putExtra("currWallet", wallet);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
    private void getData(){
        progressDialog.show();
        db.collection("users")
                .document(currUser.getUid())
                .collection("wallets")
                .document(wallet.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            Intent intent  = new Intent(EditWalletActivity.this, WalletDetailActivity.class);
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("document", "DocumentSnapshot data: " + document.getData());
                                Wallet new_wallet = new Wallet(document.getId(), document.getString("walletName"), document.getLong("walletAmount").intValue());
                                intent.putExtra("currWallet", (Serializable) new_wallet);
                                startActivity(intent);
                            }
                            progressDialog.dismiss();
                        }

                    }
                });
    }
}