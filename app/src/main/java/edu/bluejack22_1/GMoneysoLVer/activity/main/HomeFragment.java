package edu.bluejack22_1.GMoneysoLVer.activity.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.activity.wallet.WalletsFragment;
import edu.bluejack22_1.GMoneysoLVer.databinding.FragmentHomeBinding;
import edu.bluejack22_1.GMoneysoLVer.model.Category;
import edu.bluejack22_1.GMoneysoLVer.model.Transaction;
import edu.bluejack22_1.GMoneysoLVer.model.Wallet;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.text.SimpleDateFormat;
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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressDialog progressDialog;
    private Wallet wallet;
    private Category currCategory;
    private Wallet currWallet;

    private List<Transaction> transactionListThisMonth = new ArrayList<>();
    private List<Transaction> transactionListLastMonth = new ArrayList<>();
    private List<Transaction> transactionListRecent = new ArrayList<>();
    private Integer allBalance = 0, thisMonthSpend = 0, lastMonthSpend = 0, recentTrans = 0;

    final ArrayList<String> xAxisLabel = new ArrayList<>();
    private RelativeLayout rl_top_category1, rl_top_category2, rl_top_category3;
    private TextView tv_category_name1, tv_category_name2, tv_category_name3, tv_transaction_amount1, tv_transaction_amount2, tv_transaction_amount3, tv_transaction_date1, tv_transaction_date2, tv_transaction_date3;
    private ImageView is_switcher_arrow;
    private TextView tv_percent_this_last_month;

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
        actionBar.setTitle(getString(R.string.home));

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.saving));

        tv_all_balance = binding.tvAllBalance;
        tv_wallet_name = binding.tvWalletName;
        tv_wallet_amount = binding.tvWalletAmount;
        tv_see_all_wallets = binding.tvSeeAllWallets;
        tv_see_all_spend_reports = binding.tvSeeAllSpendReports;
        tv_see_all_recent_transactions = binding.tvSeeAllRecentTransactions;
        bc_spend_report = binding.bcSpendReport;
        is_switcher_arrow = binding.isSwitcherArrow;
        tv_percent_this_last_month = binding.tvPercentThisLastMonth;

        rl_top_category1 = binding.rlTopCategory1;
        rl_top_category2 = binding.rlTopCategory2;
        rl_top_category3 = binding.rlTopCategory3;
        tv_category_name1 = binding.tvCategoryName1;
        tv_category_name2 = binding.tvCategoryName2;
        tv_category_name3 = binding.tvCategoryName3;
        tv_transaction_date1 = binding.tvTransactionDate1;
        tv_transaction_date2 = binding.tvTransactionDate2;
        tv_transaction_date3 = binding.tvTransactionDate3;
        tv_transaction_amount1 = binding.tvTransactionAmount1;
        tv_transaction_amount2 = binding.tvTransactionAmount2;
        tv_transaction_amount3 = binding.tvTransactionAmount3;

        tv_percent_this_last_month.setText("0%");
        rl_top_category1.setVisibility(View.GONE);
        rl_top_category2.setVisibility(View.GONE);
        rl_top_category3.setVisibility(View.GONE);

        getBalanceFromAllWallets();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonWallet = sharedPreferences.getString("wallet", null);
        Log.d(TAG, "onCreateView: jsonWallet, " + jsonWallet);
        wallet = gson.fromJson(jsonWallet, Wallet.class);
        getBalance(wallet);

        tv_see_all_wallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new WalletsFragment());
            }
        });

        xAxisLabel.add(getString(R.string.last_month));
        xAxisLabel.add(getString(R.string.this_month));

        getDataYear();

        return binding.getRoot();
    }

    public void getDataYear() {
//        ArrayList<BarEntry> defaultbarData = new ArrayList<>();
//        defaultbarData.add(new BarEntry(0, lastMonthSpend));
//        defaultbarData.add(new BarEntry(1, thisMonthSpend));
//        BarDataSet defaultBarDataSet = new BarDataSet(defaultbarData, "data");
//        BarData defBarData = new BarData(defaultBarDataSet);
//        bc_spend_report.setData(defBarData);
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot valueYear, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(HomeFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(valueYear.isEmpty()){
                            bc_spend_report.setVisibility(View.INVISIBLE);
                        }
                        for (QueryDocumentSnapshot snapshotYear : valueYear) {
                            Log.d(TAG, "onEvent: data year, " + snapshotYear.getId());
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
                            Toast.makeText(HomeFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshotMonth : valueMonth) {
                            Log.d(TAG, "onEvent: data month, " + snapshotMonth.getId());
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
                            Toast.makeText(HomeFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshotDay : valueDay) {
                            Log.d(TAG, "onEvent: data day, " + snapshotDay.getId());
                                getData(year, month, snapshotDay.getId());
                        }
                    }
                });
    }

    private void getData(String year, String month, String day) {
        Log.d(TAG, "getData: year, " + year + "month ," + month + "day ," + day);
//        ArrayList<BarEntry> defaultbarData = new ArrayList<>();
//        defaultbarData.add(new BarEntry(0, lastMonthSpend));
//        defaultbarData.add(new BarEntry(1, thisMonthSpend));
//        BarDataSet defaultBarDataSet = new BarDataSet(defaultbarData, "data");
//        BarData defBarData = new BarData(defaultBarDataSet);
//        bc_spend_report.setData(defBarData);
        Log.d(TAG, "getData: masuk di sini");
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .document(year)
                .collection("monthList")
                .document(month)
                .collection("dateList")
                .document(day)
                .collection("transactionList")
//                .orderBy("transactionDate")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(HomeFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshot : value) {
                            Transaction transaction = new Transaction();
                            if (snapshot.getLong("transactionAmount") != null &&
                                    snapshot.getString("transactionCategory") != null &&
                                    snapshot.getDate("transactionDate") != null &&
                                    snapshot.getString("transactionWallet") != null) {

                                Log.d(TAG, "onEvent: tran id, " + snapshot.getId());
                                Log.d(TAG, "onEvent: tanggal," + snapshot.getDate("transactionDate"));

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
//                                                                transaction.setTransactionNote(snapshot.getString("transactionNote"));
                                                                transaction.setTransactionAmount(snapshot.getLong("transactionAmount").intValue());
                                                                transaction.setTransactionDate(snapshot.getDate("transactionDate"));

                                                                Calendar calendar = Calendar.getInstance();

//                                                                Log.d(TAG, "onComplete: month in calendar, " + calendar.get(Calendar.MONTH));
//                                                                Log.d(TAG, "onComplete: month di transaction, " + transaction.getTransactionDate().getMonth());
//                                                                Log.d(TAG, "onComplete: tran id, " + transaction.getTransactionID());

                                                                if (calendar.get(Calendar.MONTH) == transaction.getTransactionDate().getMonth()){
                                                                    if (transaction.getTransactionCategory().getType().equals("expense")){
                                                                        thisMonthSpend += transaction.getTransactionAmount();
                                                                    }
                                                                    transactionListThisMonth.add(transaction);
                                                                    if (recentTrans < 3){
                                                                        transactionListRecent.add(transaction);
                                                                        recentTrans++;
                                                                    }
                                                                }
                                                                calendar.add(Calendar.MONTH, -1);
//                                                                Log.d(TAG, "onComplete: month in calendar 2, " + calendar.get(Calendar.MONTH));
//                                                                Log.d(TAG, "onComplete: month di transaction 2,  " + transaction.getTransactionDate().getMonth());
                                                                if (calendar.get(Calendar.MONTH) == transaction.getTransactionDate().getMonth()){
                                                                    if (transaction.getTransactionCategory().getType().equals("expense")){
                                                                        lastMonthSpend += transaction.getTransactionAmount();
                                                                    }
                                                                    transactionListLastMonth.add(transaction);
                                                                }


                                                                Float percent = (thisMonthSpend - lastMonthSpend)/(float)thisMonthSpend*100;
                                                                tv_percent_this_last_month.setText(Math.abs(Math.round(percent * 100.0)/100.0) + "%");
                                                                if (percent < 0){
                                                                    is_switcher_arrow.setImageResource(R.drawable.ic_baseline_arrow_downward_24);
                                                                    is_switcher_arrow.setColorFilter(getResources().getColor(R.color.incomeColor));
                                                                    tv_percent_this_last_month.setTextColor(getContext().getColor(R.color.incomeColor));

                                                                } else if (percent > 0){
                                                                    is_switcher_arrow.setImageResource(R.drawable.ic_baseline_arrow_upward_24);
                                                                    is_switcher_arrow.setColorFilter(getResources().getColor(R.color.expenseColor));
                                                                    tv_percent_this_last_month.setTextColor(getContext().getColor(R.color.expenseColor));
                                                                }

                                                                //BarChart
                                                                ArrayList<BarEntry> data = new ArrayList<>();
                                                                data.add(new BarEntry(0, lastMonthSpend));
                                                                data.add(new BarEntry(1, thisMonthSpend));

                                                                BarDataSet barDataSet = new BarDataSet(data, "data");
                                                                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                                                                barDataSet.setValueTextColor(Color.BLACK);
                                                                barDataSet.setValueTextSize(16f);
                                                                BarData barData=null;
                                                                Log.d(TAG, "onComplete: "+transactionListLastMonth.size());
                                                                Log.d(TAG, "onComplete: "+transactionListThisMonth.size());
                                                                if(transactionListLastMonth.size()==0 && transactionListThisMonth.size()==0){
                                                                    bc_spend_report.setMinimumWidth(0);
                                                                }
                                                                else{
                                                                    barData = new BarData(barDataSet);
                                                                }
                                                                if(barData!=null) {
                                                                    bc_spend_report.setData(barData);


                                                                    bc_spend_report.setDrawGridBackground(false);
                                                                    bc_spend_report.setDrawBarShadow(false);
                                                                    bc_spend_report.setDrawBorders(false);

                                                                    Description description = new Description();
                                                                    description.setEnabled(false);
                                                                    bc_spend_report.setDescription(description);

                                                                    bc_spend_report.animateY(1000);
                                                                    bc_spend_report.animateX(1000);

                                                                    XAxis xAxis = bc_spend_report.getXAxis();
                                                                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                                                    xAxis.setGranularity(1f);
                                                                    xAxis.setDrawAxisLine(false);
                                                                    xAxis.setDrawGridLines(false);
                                                                    xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

                                                                    YAxis leftAxis = bc_spend_report.getAxisLeft();
                                                                    leftAxis.setAxisMinimum(0);
//                                                                leftAxis.setAxisMaximum(Float.intBitsToFloat(Integer.max(thisMonthSpend, lastMonthSpend)));
                                                                    leftAxis.setDrawAxisLine(false);

                                                                    YAxis rightAxis = bc_spend_report.getAxisRight();
                                                                    rightAxis.setDrawAxisLine(false);

                                                                    Legend legend = bc_spend_report.getLegend();
                                                                    legend.setEnabled(false);
                                                                }
                                                                ////////////////////////////////////////////

                                                                //Recent Transactions
                                                                Log.d(TAG, "onComplete: tran recent, " + transactionListRecent.size());

                                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

                                                                if (transactionListRecent.size() > 0){
                                                                    if (recentTrans == 3){
                                                                        rl_top_category1.setVisibility(View.VISIBLE);
                                                                        tv_category_name1.setText(transactionListRecent.get(recentTrans-1).getTransactionCategory().getName());
                                                                        tv_transaction_date1.setText(dateFormat.format(transactionListRecent.get(recentTrans-1).getTransactionDate()));
                                                                        tv_transaction_amount1.setText(transactionListRecent.get(recentTrans-1).formatRupiah());
                                                                    }
                                                                    else if (recentTrans == 2){
                                                                        rl_top_category2.setVisibility(View.VISIBLE);
                                                                        tv_category_name2.setText(transactionListRecent.get(recentTrans-1).getTransactionCategory().getName());
                                                                        tv_transaction_date2.setText(dateFormat.format(transactionListRecent.get(recentTrans-1).getTransactionDate()));
                                                                        tv_transaction_amount2.setText(transactionListRecent.get(recentTrans-1).formatRupiah());
                                                                    }
                                                                    else if (recentTrans == 1){
                                                                        rl_top_category3.setVisibility(View.VISIBLE);
                                                                        tv_category_name3.setText(transactionListRecent.get(recentTrans-1).getTransactionCategory().getName());
                                                                        tv_transaction_date3.setText(dateFormat.format(transactionListRecent.get(recentTrans-1).getTransactionDate()));
                                                                        tv_transaction_amount3.setText(transactionListRecent.get(recentTrans-1).formatRupiah());
                                                                    }
                                                                }
                                                                //////////////////////////////////////////
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