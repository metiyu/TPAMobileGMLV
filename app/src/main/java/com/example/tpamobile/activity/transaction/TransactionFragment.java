package com.example.tpamobile.activity.transaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpamobile.R;
import com.example.tpamobile.activity.category.CategoriesFragment;
import com.example.tpamobile.activity.transaction.adapter.TransactionPagerAdapter;
import com.example.tpamobile.databinding.FragmentTransactionBinding;
import com.example.tpamobile.model.Transaction;
import com.example.tpamobile.model.TransactionGroupByDate;
import com.example.tpamobile.model.Wallet;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentTransactionBinding binding;
    private TextView tv_transaction_wallet_balance, tv_transaction_wallet_name;
    private TabLayout tl_transaction;
    private ViewPager2 vp_transaction;
    private List<TransactionGroupByDate> transactionGroupByDateList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private TransactionPagerAdapter pagerAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private Wallet wallet;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TransactionFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionFragment newInstance(String param1, String param2) {
        TransactionFragment fragment = new TransactionFragment();
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
        binding = FragmentTransactionBinding.inflate(inflater, container, false);

        tv_transaction_wallet_balance = binding.tvTransactionWalletBalance;
        tl_transaction = binding.tlTransaction;
        vp_transaction = binding.vpTransaction;
        tv_transaction_wallet_balance = binding.tvTransactionWalletBalance;
        tv_transaction_wallet_name = binding.tvTransactionWalletName;

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        pagerAdapter = new TransactionPagerAdapter(fragmentManager, getLifecycle());
        vp_transaction.setAdapter(pagerAdapter);
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(12)));
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(11)));
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(10)));
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(9)));
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(8)));
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(7)));
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(6)));
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(5)));
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(4)));
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(3)));
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(2)));
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(1)));
        tl_transaction.addTab(tl_transaction.newTab().setText(dateFormatter(0)));

        tl_transaction.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_transaction.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp_transaction.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tl_transaction.selectTab(tl_transaction.getTabAt(position));
            }
        });

        vp_transaction.setCurrentItem(13);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonWallet = sharedPreferences.getString("wallet", null);

        wallet = gson.fromJson(jsonWallet, Wallet.class);
        Log.d("transfrag", "onEvent: wallet id, " + wallet.getId());
        Log.d("transfrag", "onEvent: wallet name, " + wallet.getName());
        Log.d("transfrag", "onEvent: wallet amount, " + wallet.getAmount());
        getBalance(wallet);

        return binding.getRoot();
    }

    public String dateFormatter(Integer howManyMonthsAgo){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -howManyMonthsAgo);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
        if (howManyMonthsAgo == 0)
            return "This month";
        if (howManyMonthsAgo == 1)
            return "Last month";
        return dateFormat.format(calendar.getTime());
    }

    public void getBalance(Wallet wallet){
        db.collection("users")
                .document(currUser.getUid())
                .collection("wallets")
                .document(wallet.getId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value.getString("walletName") != null && value.getLong("walletAmount") != null){
                            wallet.setAmount(value.getLong("walletAmount").intValue());
                            wallet.setName(value.getString("walletName"));
                            tv_transaction_wallet_name.setText(wallet.getName());
                            tv_transaction_wallet_balance.setText(wallet.formatRupiah());
                        }
                    }
                });
    }

}