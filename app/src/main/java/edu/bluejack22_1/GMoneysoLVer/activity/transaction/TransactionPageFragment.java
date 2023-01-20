package edu.bluejack22_1.GMoneysoLVer.activity.transaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.activity.transaction.adapter.TransactionPerCategoryAdapter;
import edu.bluejack22_1.GMoneysoLVer.activity.transaction.adapter.TransactionAdapter;
import edu.bluejack22_1.GMoneysoLVer.model.Category;
import edu.bluejack22_1.GMoneysoLVer.model.Transaction;
import edu.bluejack22_1.GMoneysoLVer.model.TransactionGroupByDate;
import edu.bluejack22_1.GMoneysoLVer.model.Wallet;

import edu.bluejack22_1.GMoneysoLVer.databinding.FragmentTransactionPageBinding;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentTransactionPageBinding binding;

    private int howManyMonthsAgo;
    private RecyclerView rv_transactions;
    public List<TransactionGroupByDate> transactionGroupByDateList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

    private TransactionAdapter adapterPerDate;
    private TransactionPerCategoryAdapter adapterPerCategory;

    private Category currCategory;
    private Wallet currWallet;
    private Wallet sharPrefWallet;

    private final String TAG = "TransactionPageFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TransactionPageFragment(int howManyMonthsAgo) {
        this.howManyMonthsAgo = howManyMonthsAgo;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionPageFragment newInstance(String param1, String param2, Date currDate) {
        TransactionPageFragment fragment = new TransactionPageFragment(0);
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -howManyMonthsAgo);

        getDataYear(calendar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTransactionPageBinding.inflate(inflater, container, false);

        rv_transactions = binding.rvTransactions;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -howManyMonthsAgo);

//        progressDialog = new ProgressDialog(this.getContext());
//        progressDialog.setTitle("Loading");
//        progressDialog.setMessage("Fetching...");

        adapterPerDate = new TransactionAdapter(this.getContext(), transactionGroupByDateList, adapterPerCategory);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);
        rv_transactions.setLayoutManager(layoutManager);
        rv_transactions.addItemDecoration(decoration);
        rv_transactions.setAdapter(adapterPerDate);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonWallet = sharedPreferences.getString("wallet", null);
        sharPrefWallet = gson.fromJson(jsonWallet, Wallet.class);

        Log.d(TAG, "onCreateView: calendar, " + calendar.getTime());
        getDataYear(calendar);

        return binding.getRoot();
    }

    public void getDataYear(Calendar calendar) {
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot valueYear, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(TransactionPageFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshotYear : valueYear) {
                            getDataMonth(calendar, snapshotYear.getId());
                        }
                    }
                });
    }

    private void getDataMonth(Calendar calendar, String year) {
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .document(year)
                .collection("monthList")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot valueMonth, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(TransactionPageFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshotMonth : valueMonth) {
                            transactionGroupByDateList.clear();
                            getDataDay(calendar, year, snapshotMonth.getId());
                        }
                    }
                });
    }

    private void getDataDay(Calendar calendar, String year, String month) {
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
                            Toast.makeText(TransactionPageFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshotDay : valueDay) {
                            List<Transaction> transactionList = new ArrayList<>();
                            Log.d(TAG, "onEvent: calendar month, " + month.equals(String.valueOf(calendar.get(Calendar.MONTH) + 1)));
                            Log.d(TAG, "onEvent: calendar year, " + year.equals(String.valueOf(calendar.get(Calendar.YEAR))));
                            if ((Integer.parseInt(month) == calendar.get(Calendar.MONTH) + 1) &&
                                    year.equals(String.valueOf(calendar.get(Calendar.YEAR)))) {
                                Log.d(TAG, "onEvent: lewat validasi month year");
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(snapshotDay.getId()));
                                c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
                                c.set(Calendar.YEAR, Integer.parseInt(year));

                                db.collection("users")
                                        .document(currUser.getUid())
                                        .collection("transactions")
                                        .document(year)
                                        .collection("monthList")
                                        .document(month)
                                        .collection("dateList")
                                        .document(snapshotDay.getId())
                                        .collection("transactionList")
                                        .orderBy("transactionDate")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    QuerySnapshot query = task.getResult();
                                                    Log.d(TAG, "onComplete: snapshot day, " + snapshotDay.getId());
                                                    Log.d(TAG, "onComplete: query is empty, " + query.isEmpty());
                                                    Log.d(TAG, "onComplete: query size, " + query.size());
                                                    if (!query.isEmpty()) {
                                                        getData(c, year, month, snapshotDay.getId(), transactionList);
                                                        TransactionGroupByDate transactionGroupByDate = new TransactionGroupByDate(c.getTime(), transactionList);
                                                        transactionGroupByDateList.add(transactionGroupByDate);
                                                        adapterPerCategory = new TransactionPerCategoryAdapter(getContext(), transactionList);
//                                                        TransactionGroupByDate transactionGroupByDate = new TransactionGroupByDate(c.getTime(), transactionList);
//                                                        transactionGroupByDateList.add(transactionGroupByDate);
                                                    }
                                                }
                                            }
                                        });

//                                getData(c, year, month, snapshotDay.getId(), transactionList);
//                                TransactionGroupByDate transactionGroupByDate = new TransactionGroupByDate(c.getTime(), transactionList);
//                                transactionGroupByDateList.add(transactionGroupByDate);
//                                adapterPerCategory = new TransactionPerCategoryAdapter(getContext(), transactionList);
                            }
                            adapterPerDate.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void getData(Calendar calendar, String year, String month, String day, List<Transaction> transactionList) {
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
                            Toast.makeText(TransactionPageFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshot : value) {
                            Transaction transaction = new Transaction();

//                            for (TransactionGroupByDate t : transactionGroupByDateList){
//                                Calendar c = Calendar.getInstance();
//                                c.setTime(t.getDate());
//                                Log.d(TAG, "onEvent: date from TransactionGroupByDate, " + c.getTime());
//                                Log.d(TAG, "onEvent: day from TransactionGroupByDate, " + c.get(Calendar.DAY_OF_MONTH));
//                                Log.d(TAG, "onEvent: month from TransactionGroupByDate, " + c.get(Calendar.MONTH));
//                                Log.d(TAG, "onEvent: year from TransactionGroupByDate, " + c.get(Calendar.YEAR));
//
//                                Calendar c1 = Calendar.getInstance();
//                                c1.setTime(snapshot.getDate("transactionDate"));
//                                Log.d(TAG, "onEvent: date from snapshot, " + c1.getTime());
//                                Log.d(TAG, "onEvent: day from snapshot, " + c1.get(Calendar.DAY_OF_MONTH));
//                                Log.d(TAG, "onEvent: month from snapshot, " + c1.get(Calendar.MONTH));
//                                Log.d(TAG, "onEvent: year from snapshot, " + c1.get(Calendar.YEAR));
//
//                                if (t.getDate().getDay() == snapshot.getDate("transactionDate").getDay()){
//
//                                }
//                            }

                            Log.d(TAG, "onEvent: tanggal1, " + snapshot.getDate("transactionDate"));

                            if (snapshot.getLong("transactionAmount") != null &&
                                    snapshot.getString("transactionCategory") != null &&
                                    snapshot.getDate("transactionDate") != null &&
                                    snapshot.getString("transactionWallet") != null) {
                                Calendar snapshotCalendar = Calendar.getInstance();
                                snapshotCalendar.setTime(snapshot.getDate("transactionDate"));


                                if (!sharPrefWallet.getId().equals(snapshot.getString("transactionWallet"))) {

                                    continue;
                                } else {
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
                                                                    transaction.setTransactionAmount(snapshot.getLong("transactionAmount").intValue());
                                                                    transaction.setTransactionDate(snapshot.getDate("transactionDate"));

                                                                    if (snapshotCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                                                                        Log.d(TAG, "onComplete: tran id, " + transaction.getTransactionID());
                                                                        transactionList.add(transaction);
                                                                    }

                                                                    Log.d(TAG, "onComplete: tran list is empty, " + transactionList.isEmpty());

                                                                    adapterPerCategory.notifyDataSetChanged();
                                                                    adapterPerDate.notifyDataSetChanged();

                                                                    Log.d(TAG, "onEvent: tran list size, " + transactionList.size());
                                                                }
                                                            });
                                                }
                                            });
                                }
                            }
                        }
                        Log.d(TAG, "onEvent: tran list in end of loop, " + transactionList.size());
                    }
                });
    }

//    private void getData(Calendar calendar) {
//        progressDialog.show();
//        db.collection("users")
//                .document(currUser.getUid())
//                .collection("transactions")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        transactionGroupByDateList.clear();
//                        if (error != null) {
//                            Toast.makeText(TransactionPageFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        for (QueryDocumentSnapshot snapshot : value) {
//                            if (snapshot.getLong("transactionAmount") != null &&
//                                    snapshot.getString("transactionCategory") != null &&
//                                    snapshot.getDate("transactionDate") != null &&
//                                    snapshot.getString("transactionNote") != null &&
//                                    snapshot.getString("transactionWallet") != null) {
//                                Calendar calendar = Calendar.getInstance();
//                                calendar.add(Calendar.MONTH, -howManyMonthsAgo);
//                                Log.d(TAG, "onEvent: month validation, " + String.valueOf(snapshot.getDate("transactionDate").getMonth() == calendar.getTime().getMonth()));
//                                Log.d(TAG, "onEvent: year validation, " + String.valueOf(snapshot.getDate("transactionDate").getYear() == calendar.getTime().getYear()));
//                                if (snapshot.getDate("transactionDate").getMonth() == calendar.getTime().getMonth() && snapshot.getDate("transactionDate").getYear() == calendar.getTime().getYear()) {
//
//                                    Log.d(TAG, "onEvent: category flag, " + categoryFlag);
//                                    Log.d(TAG, "onEvent: wallet flag, " + walletFlag);
//
//                                    db.collection("categories")
//                                            .document(snapshot.getString("transactionCategory"))
//                                            .get()
//                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                    if (task.isSuccessful()) {
//                                                        DocumentSnapshot document = task.getResult();
//                                                        if (document.exists()) {
//                                                            Log.d(TAG, "onComplete: di getCategory, " + document.getId());
//                                                            currCategory = new Category(document.getId(), document.getString("categoryName"), document.getString("categoryType"));
//                                                            categoryFlag = 1;
//                                                        }
//                                                    }
//
//                                                    db.collection("users")
//                                                            .document(currUser.getUid())
//                                                            .collection("wallets")
//                                                            .document(snapshot.getString("transactionWallet"))
//                                                            .get()
//                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                                @Override
//                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                                    if (task.isSuccessful()) {
//                                                                        DocumentSnapshot document = task.getResult();
//                                                                        if (document.exists()) {
//                                                                            Log.d(TAG, "onComplete: di getWallet, " + document.getId());
//                                                                            currWallet = new Wallet(document.getId(), document.getString("walletName"), document.getLong("walletAmount").intValue());
//                                                                            walletFlag = 1;
//                                                                        }
//                                                                    }
//
//                                                                    Transaction transaction = new Transaction(snapshot.getId(),
//                                                                            snapshot.getString("transactionNote"),
//                                                                            snapshot.getLong("transactionAmount").intValue(),
//                                                                            currCategory,
//                                                                            currWallet,
//                                                                            snapshot.getDate("transactionDate"));
//                                                                    transactionList.add(transaction);
//
//                                                                    Log.d(TAG, "onEvent: id, " + transaction.getTransactionID());
//                                                                    Log.d(TAG, "onEvent: wallet, " + transaction.getTransactionWallet());
//                                                                    Log.d(TAG, "onEvent: amount, " + transaction.getTransactionAmount());
//                                                                    Log.d(TAG, "onEvent: date, " + transaction.getTransactionDate());
//                                                                    Log.d(TAG, "onEvent: category, " + transaction.getTransactionCategory());
//
//                                                                    Integer length = transactionList.size();
//                                                                    Log.d(TAG, "onCreateView: transactionList.size() di onCreateView, " + transactionList.size());
//                                                                    tv_how_many_transactions.setText(length.toString());
//
//                                                                    transactionGroupByDateList.add(new TransactionGroupByDate(calendar.getTime(), transactionList));
//                                                                    for (TransactionGroupByDate t : transactionGroupByDateList){
//                                                                        Log.d(TAG, "onEvent: transactionGroupByDateList : date, " + t.getDate());
//                                                                        Log.d(TAG, "onEvent: transactionGroupByDateList : size tran list, " + t.getTransactionList().size());
//                                                                    }
//
//                                                                    adapterPerDate.notifyDataSetChanged();
//
//
//                                                                }
//                                                            });
//                                                }
//                                            });
//                                }
//                            }
//                        }
//                        progressDialog.dismiss();
//                    }
//                });
//    }

}