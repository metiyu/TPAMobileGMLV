package com.example.tpamobile.activity.budget;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpamobile.HomeActivity;
import com.example.tpamobile.PlanningFragment;
import com.example.tpamobile.R;
import com.example.tpamobile.activity.bill.BillsFragment;
import com.example.tpamobile.adapter.BillsAdapter;
import com.example.tpamobile.adapter.BudgetAdapter;
import com.example.tpamobile.databinding.FragmentBudgetBinding;
import com.example.tpamobile.model.Budget;
import com.example.tpamobile.model.Category;
import com.example.tpamobile.util.DateDisplayUtils;
import com.example.tpamobile.widgets.SimpleDatePickerDialog;
import com.example.tpamobile.widgets.SimpleDatePickerDialogFragment;
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
public class BudgetFragment extends Fragment implements  SimpleDatePickerDialog.OnDateSetListener, View.OnClickListener {


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
    private List<Budget> budgetList = new ArrayList<>();;
    private RecyclerView rv_budgets;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private Budget budget;
    private Button mPickDateButton;


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
        binding = FragmentBudgetBinding.inflate(inflater,  container, false);
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Budget");
        btn_add = binding.btnAddBudget;
        mPickDateButton = (Button) binding.btnMonthDate;
        mPickDateButton.setOnClickListener(this);
        BillsFragment.year = Calendar.getInstance().get(Calendar.YEAR);
        BillsFragment.month = Calendar.getInstance().get(Calendar.MONTH);
        mPickDateButton.setText(DateDisplayUtils.formatMonthYear(BillsFragment.year, BillsFragment.month));

        btn_add.setOnClickListener(x->{
            Intent intent = new Intent(BudgetFragment.this.getActivity(), AddBudgetActivity.class);
            startActivity(intent);
        });
        budgetAdapter = new BudgetAdapter(BudgetFragment.this.getContext(), budgetList);
        rv_budgets = binding.rvBudgets;
        rv_budgets.setAdapter(budgetAdapter);
        rv_budgets.setLayoutManager(new LinearLayoutManager((this.getContext())));

        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching...");

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

    public void getData(int year, int month){
        db.collection("users")
                .document(currUser.getUid())
                .collection("budgets")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        budgetList.clear();
                        if(error!=null){
                            Toast.makeText(BudgetFragment.this.getContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d("Size budget", "onEvent: "+value.size());
                        for(QueryDocumentSnapshot snapshot: value){
                            int curr_month = snapshot.getLong("month").intValue();
                            int curr_year = snapshot.getLong("year").intValue();
                            int cvt_month = month+1;
                            if(cvt_month>12){
                                cvt_month%=12;
                            }
                            if(snapshot.getString("category")!=null && snapshot.getLong("budgetAmount")!=null && curr_month == cvt_month && curr_year==year){
                                budget = new Budget();
                                if(snapshot.getString("category")!=null){
                                    budget.setCategory(new Category());
                                    budget.getCategory().setId(snapshot.getString("category"));
                                    budget = getCategoryData(budget.getCategory().getId(), budget);
                                    Log.d("category id", "onEvent: "+budget.getCategory().getId());
                                }
                                budget.setId(snapshot.getId());
                                budget.setAmount(snapshot.getLong("budgetAmount").intValue());
                                budget.setMonth(snapshot.getLong("month").intValue());
                                budget.setYear(snapshot.getLong("year").intValue());
                                budgetList.add(budget);
                            }
                            budgetAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public Budget getCategoryData(String categoryId, Budget budget){
        db.collection("categories")
                .document(categoryId)
                .get()
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    Category category = new Category(document.getId(), document.getString("categoryName"), document.getString("categoryType"));
                                    budget.setCategory(category);
//                                    progressDialog.dismiss();
                                }
                                budgetAdapter.notifyDataSetChanged();
                            }
                        }
                );
        return budget;
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
        Log.d("year", "onDateSet: "+year);
        Log.d("month", "onDateSet: "+monthOfYear);
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