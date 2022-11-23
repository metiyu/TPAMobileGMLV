package com.example.tpamobile.activity.transaction;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tpamobile.R;
import com.example.tpamobile.activity.wallet.WalletsFragment;
import com.example.tpamobile.adapter.CategoryAdapter;
import com.example.tpamobile.adapter.WalletAdapter;
import com.example.tpamobile.model.Bill;
import com.example.tpamobile.model.Transaction;
import com.example.tpamobile.model.Wallet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectWalletActivity extends AppCompatActivity {

    private RecyclerView rv_wallets;
    private List<Wallet> walletList = new ArrayList<>();
    private WalletAdapter walletAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog progressDialog;
    public static Bill bill_in_select_wallet;
    public static Transaction transaction_in_select_wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_wallet);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Select Wallet");

        Transaction transaction = (Transaction) getIntent().getSerializableExtra("currTransaction");

        rv_wallets = findViewById(R.id.rv_wallets);
        walletAdapter = new WalletAdapter(SelectWalletActivity.this, walletList, transaction);

        progressDialog = new ProgressDialog(SelectWalletActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching...");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SelectWalletActivity.this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(SelectWalletActivity.this, DividerItemDecoration.VERTICAL);
        rv_wallets.setLayoutManager(layoutManager);
        rv_wallets.addItemDecoration(decoration);
        rv_wallets.setAdapter(walletAdapter);
        if ((Bill) getIntent().getSerializableExtra("currBill") != null){
            bill_in_select_wallet = (Bill) getIntent().getSerializableExtra("currBill");
            SelectWalletActivity.transaction_in_select_wallet = null;
        }
        if ((Transaction) getIntent().getSerializableExtra("currTransaction") != null){
            transaction_in_select_wallet= (Transaction) getIntent().getSerializableExtra("currTransaction");
            bill_in_select_wallet = null;
        }

        getData();
    }

    private void getData(){
        progressDialog.show();
        db.collection("users")
                .document(currUser.getUid())
                .collection("wallets")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        walletList.clear();
                        if(error != null){
                            Toast.makeText(SelectWalletActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshot : value){
                            if(snapshot.getString("walletName") != null && snapshot.getLong("walletAmount") != null){
                                Wallet wallet = new Wallet(snapshot.getId(), snapshot.getString("walletName"), snapshot.getLong("walletAmount").intValue());
                                walletList.add(wallet);
                                Log.d("onEvent", "onEvent: " + wallet.getName());
                                Log.d("onEvent", "onEvent: " + wallet.getAmount());
                            }
                            walletAdapter.notifyDataSetChanged();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}