package com.example.tpamobile.activity.bill;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.tpamobile.BillsAdapter;
import com.example.tpamobile.HomeActivity;
import com.example.tpamobile.R;
import com.example.tpamobile.util.DateDisplayUtils;
import com.example.tpamobile.widgets.SimpleDatePickerDialog;
import com.example.tpamobile.widgets.SimpleDatePickerDialogFragment;
import com.example.tpamobile.databinding.FragmentBillsBinding;
import com.example.tpamobile.model.Bill;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
 * Use the {@link BillsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BillsFragment extends Fragment implements SimpleDatePickerDialog.OnDateSetListener, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentBillsBinding binding;
    private RecyclerView rv_bills;
    private Button btn_add_bill;
    private BillsAdapter billsAdapter;
    private ProgressDialog progressDialog;
    private TextView tv_this_month_amount, tv_overdue_amount;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private List<Bill> billList = new ArrayList<>();
    private Button mPickDateButton;
    private Integer totalOverDue, totalThisMonth;
    String s1[]= {"asd", "asd1", "asd2"};
    String s2[]= {"asd", "asd1", "asd3"};
    String s3[]= {"asd", "asd1", "asd4"};


    public BillsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BillsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BillsFragment newInstance(String param1, String param2) {
        BillsFragment fragment = new BillsFragment();
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
        binding = FragmentBillsBinding.inflate(
                inflater,  container, false);
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Bills");
        rv_bills = binding.rvBills;
        btn_add_bill = binding.btnAddBill;
        tv_overdue_amount = binding.tvOverdueAmount;
        tv_this_month_amount = binding.tvThisMonthAmount;
//        MonthYearPickerDialog pd = new MonthYearPickerDialog();
//        pd.setListener(this);
//        pd.show(this.getFragmentManager(), "MonthYearPickerDialog");
//        BillsAdapter billsAdapter = new BillsAdapter(this.getContext(), s1, s2, s3);
        mPickDateButton = (Button) binding.btnMonthDate;
        mPickDateButton.setOnClickListener(this);
        int year,month;
        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        mPickDateButton.setText(DateDisplayUtils.formatMonthYear(year, month));
        billsAdapter = new BillsAdapter(BillsFragment.this.getContext(), billList);
        rv_bills.setAdapter(billsAdapter);
        rv_bills.setLayoutManager(new LinearLayoutManager(this.getContext()));
        btn_add_bill.setOnClickListener(x->{
            startActivity(new Intent(BillsFragment.this.getActivity(), AddBillActivity.class));
        });
        rv_bills = binding.rvBills;


        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching...");

        getData(year, month);

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
    private void getData(int year, int month){
        totalOverDue =0;
        totalThisMonth = 0;
        progressDialog.show();
        db.collection("users")
                .document(currUser.getUid())
                .collection("bills")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        billList.clear();
                        if(error != null){
                            Toast.makeText(BillsFragment.this.getContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshot : value){
                            if(snapshot.getDate("billDate")!=null&&snapshot.getString("billDescription") != null && snapshot.getString("paidStatus") != null && snapshot.getString("repeatValue") != null && snapshot.getLong("billAmount") != null){
                                Calendar today_cal = Calendar.getInstance(Locale.getDefault());
                                Calendar current_cal = dateToCalendar(snapshot.getDate("billDate"));
                                Log.d("year", "onEvent: "+current_cal.get(Calendar.YEAR));
                                Log.d("year", "onEvent: "+today_cal.get(Calendar.YEAR));
                                Log.d("m", "onEvent: "+current_cal.get(Calendar.MONTH));
                                Log.d("m", "onEvent: "+today_cal.get(Calendar.MONTH));
                                if(snapshot.getString("paidStatus").equals("Unpaid") &&  current_cal.get(Calendar.YEAR)==today_cal.get(Calendar.YEAR) && current_cal.get(Calendar.MONTH)==today_cal.get(Calendar.MONTH)){
                                    totalThisMonth+=snapshot.getLong("billAmount").intValue();

                                }
                                if(snapshot.getString("paidStatus").equals("Unpaid") && ((current_cal.get(Calendar.YEAR)==today_cal.get(Calendar.YEAR) && current_cal.get(Calendar.MONTH)<today_cal.get(Calendar.MONTH)) || (current_cal.get(Calendar.YEAR)<today_cal.get(Calendar.YEAR)))){
                                    totalOverDue+=snapshot.getLong("billAmount").intValue();
                                }
                                tv_this_month_amount.setText("Rp"+totalThisMonth+",00");
                                tv_overdue_amount.setText("Rp"+totalOverDue+",00");
                                if(current_cal.get(Calendar.YEAR)==year && current_cal.get(Calendar.MONTH)==month){
                                    Bill bill  = new Bill(snapshot.getId(), snapshot.getString("billDescription"), snapshot.getString("repeatValue"), snapshot.getString("paidStatus"), snapshot.getLong("billAmount").intValue(), current_cal.get(Calendar.YEAR), current_cal.get(Calendar.MONTH), current_cal.get(Calendar.DAY_OF_MONTH));
                                    billList.add(bill);
//                                Wallet wallet = new Wallet(snapshot.getId(), snapshot.getString("walletName"), snapshot.getLong("walletAmount").intValue());
//                                walletList.add(wallet);
                                    Log.d("onEvent", "Description: " + bill.getDescription());
                                    Log.d("onEvent", "onEvent: " + bill.getBillAmount());
                                }
                            }
                            billsAdapter.notifyDataSetChanged();
                        }
                        progressDialog.dismiss();
                    }
                });
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
        getData(year, monthOfYear);
    }
    private Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

    //Convert Calendar to Date
    private Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }
}
