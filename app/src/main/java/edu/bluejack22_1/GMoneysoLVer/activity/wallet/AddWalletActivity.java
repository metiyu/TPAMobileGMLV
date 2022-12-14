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

import edu.bluejack22_1.GMoneysoLVer.activity.main.HomeActivity;
import edu.bluejack22_1.GMoneysoLVer.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddWalletActivity extends AppCompatActivity {

    EditText et_wallet_name, et_wallet_amount;
    Button btn_save_wallet;
    ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.wallet));
        et_wallet_name = findViewById(R.id.et_wallet_name);
        et_wallet_amount = findViewById(R.id.et_wallet_amount);
        btn_save_wallet = findViewById(R.id.btn_save_wallet);

//        et_wallet_amount.addTextChangedListener(new TextWatcher() {
//            private String et = et_wallet_amount.getText().toString().trim();
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if(!charSequence.toString().equals(et)){
//                    et_wallet_amount.removeTextChangedListener(this);
//                    String replace = charSequence.toString().replaceAll("[Rp. ]", "");
//                    if(!replace.isEmpty()){
//                        et = formatRupiah(Double.parseDouble(replace));
//                    } else {
//                        et = "";
//                    }
//                    et_wallet_amount.setText(et);
//                    et_wallet_amount.setSelection(et.length());
//                    et_wallet_amount.addTextChangedListener(this);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        progressDialog = new ProgressDialog(AddWalletActivity.this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.saving));

        btn_save_wallet.setOnClickListener(x -> {
            String walletName = et_wallet_name.getText().toString().trim();
            Integer walletAmount = Integer.parseInt(et_wallet_amount.getText().toString().trim());

            saveData(walletName, walletAmount);
        });
    }

    private String formatRupiah(Double number){
        Locale localeID = new Locale("IND", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        String rupiahFormatted = formatRupiah.format(number);
        String[] split = rupiahFormatted.split(",");
        int length = split[0].length();
        return split[0].substring(0,2)+". "+split[0].substring(2,length);
    }

    public void saveData(String walletName, Integer walletAmount){
        Map<String, Object> wallet = new HashMap<>();
        wallet.put("walletName", walletName);
        wallet.put("walletAmount", walletAmount);

        progressDialog.show();

        db.collection("users")
                .document(currUser.getUid())
                .collection("wallets")
                .add(wallet)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                        Intent intent  = new Intent(AddWalletActivity.this, HomeActivity.class);
                        intent.putExtra("fragmentToGo","wallet");
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                        Intent intent  = new Intent(AddWalletActivity.this, HomeActivity.class);
                        intent.putExtra("fragmentToGo","wallet");
                        startActivity(intent);
                    }
                });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent  = new Intent(AddWalletActivity.this, HomeActivity.class);
        intent.putExtra("fragmentToGo","wallet");
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}