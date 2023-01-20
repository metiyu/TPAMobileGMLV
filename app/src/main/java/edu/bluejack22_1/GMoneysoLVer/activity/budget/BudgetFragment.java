package edu.bluejack22_1.GMoneysoLVer.activity.budget;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.activity.main.HomeActivity;

import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.activity.bill.BillsFragment;
import edu.bluejack22_1.GMoneysoLVer.activity.budget.adapter.BudgetAdapter;
import edu.bluejack22_1.GMoneysoLVer.databinding.FragmentBudgetBinding;
import edu.bluejack22_1.GMoneysoLVer.model.Budget;
import edu.bluejack22_1.GMoneysoLVer.model.Category;
import edu.bluejack22_1.GMoneysoLVer.model.Transaction;
import edu.bluejack22_1.GMoneysoLVer.model.Wallet;
import edu.bluejack22_1.GMoneysoLVer.utilities.DateDisplayUtils;
import edu.bluejack22_1.GMoneysoLVer.widgets.SimpleDatePickerDialog;
import edu.bluejack22_1.GMoneysoLVer.widgets.SimpleDatePickerDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BudgetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BudgetFragment extends Fragment implements SimpleDatePickerDialog.OnDateSetListener, View.OnClickListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentBudgetBinding binding;
    private Button btn_add;
    private BudgetAdapter budgetAdapter;
    private List<Budget> budgetList = new ArrayList<>();
    private RecyclerView rv_budgets;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
//    private Budget budget;
    private Button mPickDateButton;

    private String TAG = "BudgetFragment";
    private Category currCategory;
    private Wallet currWallet;
    private List<Transaction> transactionListForBudget = new ArrayList<>();

    public BudgetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BudgetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BudgetFragment newInstance(String param1, String param2) {
        BudgetFragment fragment = new BudgetFragment();
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
//        return inflater.inflate(R.layout.fragment_budget, container, false);
        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.budget));
        btn_add = binding.btnAddBudget;
        mPickDateButton = binding.btnMonthDate;
        mPickDateButton.setOnClickListener(this);
        BillsFragment.year = Calendar.getInstance().get(Calendar.YEAR);
        BillsFragment.month = Calendar.getInstance().get(Calendar.MONTH);
        mPickDateButton.setText(DateDisplayUtils.formatMonthYear(BillsFragment.year, BillsFragment.month));

        btn_add.setOnClickListener(x -> {
            Intent intent = new Intent(BudgetFragment.this.getActivity(), AddBudgetActivity.class);
            startActivity(intent);
        });
        budgetAdapter = new BudgetAdapter(BudgetFragment.this.getContext(), budgetList);
        rv_budgets = binding.rvBudgets;
        rv_budgets.setAdapter(budgetAdapter);
        rv_budgets.setLayoutManager(new LinearLayoutManager((this.getContext())));

        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.fetching));

        getData(BillsFragment.year, BillsFragment.month);

        return binding.getRoot();
    }

    private void displaySimpleDatePickerDialogFragment() {
        SimpleDatePickerDialogFragment datePickerDialogFragment;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        datePickerDialogFragment = SimpleDatePickerDialogFragment.getInstance(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        datePickerDialogFragment.setOnDateSetListener(this);
        datePickerDialogFragment.show(getChildFragmentManager(), null);
    }

//    @Override
//    public void onDateSet(int year, int monthOfYear) {
//
//    }

    public void getData(int year, int month) {
        db.collection("users")
                .document(currUser.getUid())
                .collection("budgets")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        budgetList.clear();
                        if (error != null) {
                            Toast.makeText(BudgetFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d(TAG, "onEvent: " + value.size());
                        for (QueryDocumentSnapshot snapshot : value) {
                            Log.d(TAG, "onEvent: budget id " + snapshot.getId());
                            int curr_month = snapshot.getLong("month").intValue();
                            int curr_year = snapshot.getLong("year").intValue();
                            int cvt_month = month + 1;
                            if (cvt_month > 12) {
                                cvt_month %= 12;
                            }

                            Budget budget = new Budget();
                                if (snapshot.getString("category") != null && snapshot.getLong("budgetAmount") != null && curr_month == cvt_month && curr_year == year) {
                                    if (snapshot.get("transactionList") != null) {
                                        for (DocumentReference tranId : (List<DocumentReference>) snapshot.get("transactionList")) {
                                            Log.d(TAG, "onEvent: tranid path, " + tranId.getPath());
                                            db.document(tranId.getPath().toString())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot doc = task.getResult();
                                                                if (doc.exists()) {
                                                                    if (doc.getData() != null) {
                                                                        Transaction transaction = new Transaction();

                                                                        if (doc.getLong("transactionAmount") != null &&
                                                                                doc.getString("transactionCategory") != null &&
                                                                                doc.getDate("transactionDate") != null &&
                                                                                doc.getString("transactionWallet") != null) {
                                                                            Log.d(TAG, "onComplete: snapshot tran, " + doc.getId());
                                                                            db.collection("categories")
                                                                                    .document(doc.getString("transactionCategory"))
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
                                                                                                    .document(doc.getString("transactionWallet"))
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
                                                                                                            transaction.setTransactionID(doc.getId());
                                                                                                            transaction.setTransactionAmount(doc.getLong("transactionAmount").intValue());
                                                                                                            transaction.setTransactionDate(doc.getDate("transactionDate"));

                                                                                                            transactionListForBudget.add(transaction);

                                                                                                            int flag = 0;
                                                                                                            for (Budget b : budgetList) {
                                                                                                                Log.d(TAG, "onComplete: budget id foreach " + b.getId());
                                                                                                                Log.d(TAG, "onComplete: budget id snapshot foreach " + snapshot.getId());
                                                                                                                if (b.getId().equals(snapshot.getId())) {
                                                                                                                    b.setTransactionList(transactionListForBudget);
                                                                                                                    flag = 1;
                                                                                                                } else {
                                                                                                                    continue;
                                                                                                                }
                                                                                                            }

                                                                                                            if (flag == 0) {
                                                                                                                Budget budget1 = new Budget();
                                                                                                                budget1.setTransactionList(transactionListForBudget);
                                                                                                                Log.d(TAG, "onComplete: tran list, " + budget1.getTransactionList().size());
                                                                                                                budget1.setId(snapshot.getId());
                                                                                                                budget1.setAmount(snapshot.getLong("budgetAmount").intValue());
                                                                                                                budget1.setMonth(snapshot.getLong("month").intValue());
                                                                                                                budget1.setYear(snapshot.getLong("year").intValue());
                                                                                                                budget1.setCategory(new Category());
                                                                                                                budget1.getCategory().setId(snapshot.getString("category"));
                                                                                                                budget1 = getCategoryData(budget1.getCategory().getId(), budget1);
                                                                                                                Log.d(TAG, "onEvent: budget1 tran list, " + budget1.getTransactionList().size());
                                                                                                                Log.d(TAG, "onComplete: budget1 id added to list " + budget1.getId());
                                                                                                                budgetList.add(budget1);
                                                                                                            }

                                                                                                            budgetAdapter.notifyDataSetChanged();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    });
                                                                        }

                                                                    }
                                                                }
                                                            } else {
                                                                Log.d(TAG, "onComplete: error task incomplete, ");
                                                            }
                                                        }
                                                    });
                                        }
                                    } else {
                                        Log.d(TAG, "onEvent: snapshot id " + snapshot.getId());
                                        budget.setId(snapshot.getId());
                                        budget.setAmount(snapshot.getLong("budgetAmount").intValue());
                                        budget.setMonth(snapshot.getLong("month").intValue());
                                        budget.setYear(snapshot.getLong("year").intValue());
                                        budget.setCategory(new Category());
                                        budget.getCategory().setId(snapshot.getString("category"));
                                        budget = getCategoryData(budget.getCategory().getId(), budget);
                                        Log.d(TAG, "onEvent: budget category no transaction " + budget.getCategory().getId());
                                        Log.d(TAG, "onComplete: budget id added to list " + budget.getId());
                                        budgetList.add(budget);
                                        budgetAdapter.notifyDataSetChanged();
                                    }
                                }

//                            if (snapshot.getString("category") != null && snapshot.getLong("budgetAmount") != null && curr_month == cvt_month && curr_year == year) {
//                                budget = new Budget();
//                                if (snapshot.getString("category") != null) {
//                                    budget.setCategory(new Category());
//                                    budget.getCategory().setId(snapshot.getString("category"));
//                                    budget = getCategoryData(budget.getCategory().getId(), budget);
//                                    Log.d(TAG, "onEvent: budget category " + budget.getCategory().getId());
//
//                                    if (snapshot.get("transactionList") != null) {
//                                        for (DocumentReference tranId : (List<DocumentReference>) snapshot.get("transactionList")) {
//                                            Log.d(TAG, "onEvent: tranid path, " + tranId.getPath());
//                                            db.document(tranId.getPath().toString())
//                                                    .get()
//                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                            if (task.isSuccessful()) {
//                                                                DocumentSnapshot doc = task.getResult();
//                                                                if (doc.exists()) {
//                                                                    if (doc.getData() != null) {
//                                                                        Transaction transaction = new Transaction();
//
//                                                                        if (doc.getLong("transactionAmount") != null &&
//                                                                                doc.getString("transactionCategory") != null &&
//                                                                                doc.getDate("transactionDate") != null &&
//                                                                                doc.getString("transactionWallet") != null) {
//                                                                            Log.d(TAG, "onComplete: snapshot tran, " + doc.getId());
//                                                                            db.collection("categories")
//                                                                                    .document(doc.getString("transactionCategory"))
//                                                                                    .get()
//                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                                                        @Override
//                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                                                            if (task.isSuccessful()) {
//                                                                                                DocumentSnapshot document = task.getResult();
//                                                                                                if (document.exists()) {
//                                                                                                    currCategory = new Category(document.getId(), document.getString("categoryName"), document.getString("categoryType"));
//                                                                                                    transaction.setTransactionCategory(currCategory);
//                                                                                                }
//                                                                                            }
//
//                                                                                            db.collection("users")
//                                                                                                    .document(currUser.getUid())
//                                                                                                    .collection("wallets")
//                                                                                                    .document(doc.getString("transactionWallet"))
//                                                                                                    .get()
//                                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                                                                        @Override
//                                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                                                                            if (task.isSuccessful()) {
//                                                                                                                DocumentSnapshot document = task.getResult();
//                                                                                                                if (document.exists()) {
//                                                                                                                    currWallet = new Wallet(document.getId(), document.getString("walletName"), document.getLong("walletAmount").intValue());
//                                                                                                                    transaction.setTransactionWallet(currWallet);
//                                                                                                                }
//                                                                                                            }
//                                                                                                            transaction.setTransactionID(doc.getId());
//                                                                                                            transaction.setTransactionAmount(doc.getLong("transactionAmount").intValue());
//                                                                                                            transaction.setTransactionDate(doc.getDate("transactionDate"));
//
//                                                                                                            transactionListForBudget.add(transaction);
//
//                                                                                                            int flag = 0;
//                                                                                                            for (Budget b : budgetList){
//                                                                                                                Log.d(TAG, "onComplete: budget id foreach " + b.getId());
//                                                                                                                Log.d(TAG, "onComplete: budget id snapshot foreach " + snapshot.getId());
//                                                                                                                if (b.getId().equals(snapshot.getId())){
//                                                                                                                    b.setTransactionList(transactionListForBudget);
//                                                                                                                    flag = 1;
//                                                                                                                } else {
//                                                                                                                    continue;
//                                                                                                                }
//                                                                                                            }
//
//                                                                                                            if (flag == 0){
//                                                                                                                budget.setTransactionList(transactionListForBudget);
//                                                                                                                Log.d(TAG, "onComplete: tran list, " + budget.getTransactionList().size());
//                                                                                                                budget.setId(snapshot.getId());
//                                                                                                                budget.setAmount(snapshot.getLong("budgetAmount").intValue());
//                                                                                                                budget.setMonth(snapshot.getLong("month").intValue());
//                                                                                                                budget.setYear(snapshot.getLong("year").intValue());
//                                                                                                                Log.d(TAG, "onEvent: budget tran list, " + budget.getTransactionList().size());
//                                                                                                                Log.d(TAG, "onComplete: budget id added to list " + budget.getId());
//                                                                                                                budgetList.add(budget);
//                                                                                                            }
//
//                                                                                                            budgetAdapter.notifyDataSetChanged();
//                                                                                                        }
//                                                                                                    });
//                                                                                        }
//                                                                                    });
//                                                                        }
//
//                                                                    }
//                                                                }
//                                                            } else {
//                                                                Log.d(TAG, "onComplete: error task incomplete, ");
//                                                            }
//                                                        }
//                                                    });
//                                        }
//                                    } else {
//                                        budget.setId(snapshot.getId());
//                                        budget.setAmount(snapshot.getLong("budgetAmount").intValue());
//                                        budget.setMonth(snapshot.getLong("month").intValue());
//                                        budget.setYear(snapshot.getLong("year").intValue());
//                                        budget = getCategoryData(budget.getCategory().getId(), budget);
//                                        Log.d(TAG, "onEvent: budget category no transaction " + budget.getCategory().getId());
//                                        Log.d(TAG, "onComplete: budget id added to list " + budget.getId());
//                                        budgetList.add(budget);
//                                        budgetAdapter.notifyDataSetChanged();
//                                    }
//                                }
//                            }
                        }
                    }
                });
    }

    public Budget getCategoryData(String categoryId, Budget budgett) {
        db.collection("categories")
                .document(categoryId)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Category category = new Category(document.getId(), document.getString("categoryName"), document.getString("categoryType"));
                                    budgett.setCategory(category);
//                                    progressDialog.dismiss();
                                }
                                budgetAdapter.notifyDataSetChanged();
                            }
                        }
                );
        return budgett;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_month_date) {
            displaySimpleDatePickerDialogFragment();
        }
    }

    @Override
    public void onDateSet(int year, int monthOfYear) {
        mPickDateButton.setText(DateDisplayUtils.formatMonthYear(year, monthOfYear));
        Log.d(TAG, "onDateSet: " + year);
        Log.d(TAG, "onDateSet: " + monthOfYear);
        transactionListForBudget.clear();
        budgetList.clear();
        budgetAdapter.notifyDataSetChanged();
        getData(year, monthOfYear);
    }

    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        replaceFragment(new PlanningFragment());
//        return true;
//    }
//    private void replaceFragment(Fragment fragment){
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout,fragment);
//        fragmentTransaction.commit();
//    }
}