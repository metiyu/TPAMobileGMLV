package com.example.tpamobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpamobile.activity.transaction.TransactionPageFragment;
import com.example.tpamobile.activity.transaction.adapter.TransactionPerCategoryAdapter;
import com.example.tpamobile.activity.wallet.WalletsFragment;
import com.example.tpamobile.databinding.FragmentHomeBinding;
import com.example.tpamobile.model.Category;
import com.example.tpamobile.model.Transaction;
import com.example.tpamobile.model.TransactionGroupByDate;
import com.example.tpamobile.model.Wallet;
import com.github.mikephil.charting.charts.BarChart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentHomeBinding binding;
    private TextView tv_all_balance, tv_wallet_name, tv_wallet_amount, tv_see_all_wallets, tv_see_all_spend_reports, tv_see_all_recent_transactions;
    private BarChart bc_spend_report;
    private RecyclerView rv_recent_transactions;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog progressDialog;
    private Wallet wallet;
    private Category currCategory;
    private Wallet currWallet;

    private Integer allBalance = 0;

    private String TAG = "HOME_FRAGMENT";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("Home");

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Saving...");

        tv_all_balance = binding.tvAllBalance;
        tv_wallet_name = binding.tvWalletName;
        tv_wallet_amount = binding.tvWalletAmount;
        tv_see_all_wallets = binding.tvSeeAllWallets;
        tv_see_all_spend_reports = binding.tvSeeAllSpendReports;
        tv_see_all_recent_transactions = binding.tvSeeAllRecentTransactions;
        bc_spend_report = binding.bcSpendReport;
        rv_recent_transactions = binding.rvRecentTransactions;

        getBalanceFromAllWallets();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonWallet = sharedPreferences.getString("wallet", null);
        wallet = gson.fromJson(jsonWallet, Wallet.class);
        getBalance(wallet);

        tv_see_all_wallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new WalletsFragment());
            }
        });


        return binding.getRoot();
    }

    public void getDataYear() {
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot valueYear, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(HomeFragment.this.getContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshotYear : valueYear) {
                            getDataMonth(snapshotYear.getId());
                        }
                    }
                });
    }

    private void getDataMonth(String year) {
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .document(year)
                .collection("monthList")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot valueMonth, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(HomeFragment.this.getContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshotMonth : valueMonth) {
                            getDataDay(year, snapshotMonth.getId());
                        }
                    }
                });
    }

    private void getDataDay(String year, String month) {
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .document(year)
                .collection("monthList")
                .document(month)
                .collection("dateList")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot valueDay, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(HomeFragment.this.getContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshotDay : valueDay) {
                                getData(year, month, snapshotDay.getId());
                        }
                    }
                });
    }

    private void getData(String year, String month, String day) {
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .document(year)
                .collection("monthList")
                .document(month)
                .collection("dateList")
                .document(day)
                .collection("transactionList")
                .orderBy("transactionDate")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(HomeFragment.this.getContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshot : value) {
                            Transaction transaction = new Transaction();
                            if (snapshot.getLong("transactionAmount") != null &&
                                    snapshot.getString("transactionCategory") != null &&
                                    snapshot.getDate("transactionDate") != null &&
                                    snapshot.getString("transactionNote") != null &&
                                    snapshot.getString("transactionWallet") != null) {

                                db.collection("categories")
                                        .document(snapshot.getString("transactionCategory"))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        currCategory = new Category(document.getId(), document.getString("categoryName"), document.getString("categoryType"));
                                                        transaction.setTransactionCategory(currCategory);
                                                    }
                                                }

                                                db.collection("users")
                                                        .document(currUser.getUid())
                                                        .collection("wallets")
                                                        .document(snapshot.getString("transactionWallet"))
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot document = task.getResult();
                                                                    if (document.exists()) {
                                                                        currWallet = new Wallet(document.getId(), document.getString("walletName"), document.getLong("walletAmount").intValue());
                                                                        transaction.setTransactionWallet(currWallet);
                                                                    }
                                                                }

                                                                transaction.setTransactionID(snapshot.getId());
                                                                transaction.setTransactionNote(snapshot.getString("transactionNote"));
                                                                transaction.setTransactionAmount(snapshot.getLong("transactionAmount").intValue());
                                                                transaction.setTransactionDate(snapshot.getDate("transactionDate"));


                                                            }
                                                        });
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void getBalance(Wallet wallet) {
        db.collection("users")
                .document(currUser.getUid())
                .collection("wallets")
                .document(wallet.getId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value.getString("walletName") != null && value.getLong("walletAmount") != null) {
                            wallet.setAmount(value.getLong("walletAmount").intValue());
                            wallet.setName(value.getString("walletName"));
                            tv_wallet_name.setText(wallet.getName());
                            tv_wallet_amount.setText(wallet.formatRupiah());
                        }
                    }
                });
    }

    private void getBalanceFromAllWallets() {
        progressDialog.show();
        db.collection("users")
                .document(currUser.getUid())
                .collection("wallets")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshot : value) {
                            if (snapshot.getLong("walletAmount") != null) {
                                Log.d(TAG, "onEvent: walletAmount, " + snapshot.getLong("walletAmount").intValue());
                                allBalance += snapshot.getLong("walletAmount").intValue();
                            }
                        }
                        tv_all_balance.setText(formatRupiah(allBalance));
                        progressDialog.dismiss();
                    }
                });
    }

    public String formatRupiah(Integer amount) {
        DecimalFormat IndExcRate = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        IndExcRate.setDecimalFormatSymbols(formatRp);
        return IndExcRate.format(amount);
    }

}