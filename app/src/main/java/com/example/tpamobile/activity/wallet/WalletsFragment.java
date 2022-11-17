package com.example.tpamobile.activity.wallet;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tpamobile.R;
import com.example.tpamobile.activity.category.AddCategoryActivity;
import com.example.tpamobile.activity.category.CategoriesFragment;
import com.example.tpamobile.adapter.CategoryAdapter;
import com.example.tpamobile.adapter.WalletAdapter;
import com.example.tpamobile.databinding.FragmentWalletBinding;
import com.example.tpamobile.model.Category;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalletsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentWalletBinding binding;
    private RecyclerView rv_wallets;
    private List<Wallet> walletList = new ArrayList<>();
    private WalletAdapter walletAdapter;
    private Button btn_add_wallet;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog progressDialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WalletsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WalletFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WalletsFragment newInstance(String param1, String param2) {
        WalletsFragment fragment = new WalletsFragment();
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
        binding = FragmentWalletBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        btn_add_wallet = binding.btnAddWallet;
        btn_add_wallet.setOnClickListener(x -> {
            startActivity(new Intent(WalletsFragment.this.getActivity(), AddWalletActivity.class));
        });

        rv_wallets = binding.rvWallets;
        walletAdapter = new WalletAdapter(WalletsFragment.this.getContext(), walletList);

        progressDialog = new ProgressDialog(WalletsFragment.this.getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching...");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WalletsFragment.this.getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(WalletsFragment.this.getContext(), DividerItemDecoration.VERTICAL);
        rv_wallets.setLayoutManager(layoutManager);
        rv_wallets.addItemDecoration(decoration);
        rv_wallets.setAdapter(walletAdapter);

        getData();

        return binding.getRoot();
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
                            Toast.makeText(WalletsFragment.this.getContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
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