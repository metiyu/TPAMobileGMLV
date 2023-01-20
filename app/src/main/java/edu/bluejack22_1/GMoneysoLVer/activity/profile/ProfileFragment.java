package edu.bluejack22_1.GMoneysoLVer.activity.profile;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.activity.main.HomeActivity;
import edu.bluejack22_1.GMoneysoLVer.activity.main.MainActivity;
import edu.bluejack22_1.GMoneysoLVer.activity.notification.NotificationFragment;
import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.activity.category.CategoriesFragment;
import edu.bluejack22_1.GMoneysoLVer.activity.wallet.WalletsFragment;
import edu.bluejack22_1.GMoneysoLVer.databinding.FragmentProfileBinding;
import edu.bluejack22_1.GMoneysoLVer.model.CategoryTotal;
import edu.bluejack22_1.GMoneysoLVer.model.Transaction;
import edu.bluejack22_1.GMoneysoLVer.model.TransactionGroupByDate;

import edu.bluejack22_1.GMoneysoLVer.utilities.ValidationBeforeUpdateFragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String TAG = "ProfileFragment";
    private FirebaseUser user;
    private String email;
    private LinearLayout btn_categories, btn_edit_profile, btn_my_wallets, btn_sign_out, btn_notification;
    private FragmentProfileBinding binding;
    private PieChart pieChart, pieChartIncome;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private GoogleSignInClient googleSignInClient;
    private List<String> yearList = new ArrayList<>();
    private List<String> monthList = new ArrayList<>();
    private List<String> dayList = new ArrayList<>();
    private List<CategoryTotal> categoryListExpense = new ArrayList<>();
    private List<CategoryTotal> categoryListIncome = new ArrayList<>();
    private List<CategoryTotal> categoryList = new ArrayList<>();
    private CategoryTotal currCategory;
    private List<TransactionGroupByDate> transactionGroupByDateList = new ArrayList<>();
    private Integer sumExpense, sumIncome;
    private ArrayList<PieEntry> entriesExpense = new ArrayList<>();
    private ArrayList<PieEntry> entriesIncome = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(
                inflater, container, false);
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.profile));
        sumIncome = 0;
        sumExpense = 0;
        btn_sign_out = binding.btnSignOut;
        pieChart = binding.piechart;
        pieChartIncome = binding.piechartincome;
        Calendar calendar = Calendar.getInstance();
        getDataYear(calendar);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
            Log.d("value of email is ", String.valueOf(email));
            TextView tv = binding.currentUserEmailTv;
            tv.setText(email);
        }

        btn_edit_profile = binding.btnEditProfile;
        btn_edit_profile.setOnClickListener(x -> {
            replaceFragment(new ValidationBeforeUpdateFragment());
        });

        btn_notification = binding.btnNotification;
        btn_notification.setOnClickListener(x -> {
            replaceFragment(new NotificationFragment());
        });

        btn_categories = binding.btnCategories;
        btn_categories.setOnClickListener(x -> {
            replaceFragment(new CategoriesFragment());
        });

        btn_my_wallets = binding.btnMyWallets;
        btn_my_wallets.setOnClickListener(x -> {
            replaceFragment(new WalletsFragment());
        });

        btn_sign_out.setOnClickListener(x -> {
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
            FirebaseAuth.getInstance().signOut();
            googleSignInClient.signOut();
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return binding.getRoot();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void getDataYear(Calendar calendar) {
        db.collection("users")
                .document(currUser.getUid())
                .collection("transactions")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot valueYear, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(ProfileFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshotYear : valueYear) {
                            Log.d(TAG, "onEvent: year " + snapshotYear.getId());
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
                            Toast.makeText(ProfileFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshotMonth : valueMonth) {
                            transactionGroupByDateList.clear();
                            Log.d(TAG, "onEvent: month " + snapshotMonth.getId());
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
                            Toast.makeText(ProfileFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshotDay : valueDay) {
                            List<Transaction> transactionList = new ArrayList<>();
                            Log.d(TAG, "onEvent: month " + month);
                            Log.d(TAG, "onEvent: calendar month " + String.valueOf(calendar.get(Calendar.MONTH) + 1));
                            Log.d(TAG, "onEvent: year " + year);
                            Log.d(TAG, "onEvent: calendar year " + calendar.get(Calendar.YEAR));
                            Log.d(TAG, "onEvent: if value " + String.valueOf((Integer.parseInt(month) == calendar.get(Calendar.MONTH) + 1) &&
                                    year.equals(String.valueOf(calendar.get(Calendar.YEAR)))));
                            if ((Integer.parseInt(month) == calendar.get(Calendar.MONTH) + 1) &&
                                    year.equals(String.valueOf(calendar.get(Calendar.YEAR)))) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(snapshotDay.getId()));
                                c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
                                c.set(Calendar.YEAR, Integer.parseInt(year));

                                Log.d(TAG, "onEvent: day " + snapshotDay.getId());
                                getData(c, year, month, snapshotDay.getId(), transactionList);

                            }
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
                            Toast.makeText(ProfileFragment.this.getContext(), getString(R.string.failed_to_fetch), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshot : value) {
                            Transaction transaction = new Transaction();
                            if (snapshot.getLong("transactionAmount") != null &&
                                    snapshot.getString("transactionCategory") != null &&
                                    snapshot.getDate("transactionDate") != null &&
                                    snapshot.getString("transactionWallet") != null) {
                                Log.d(TAG, "onEvent: tran " + snapshot.getId());
                                Calendar snapshotCalendar = Calendar.getInstance();
                                snapshotCalendar.setTime(snapshot.getDate("transactionDate"));
                                int lastAmount = snapshot.getLong("transactionAmount").intValue();
                                db.collection("categories")
                                        .document(snapshot.getString("transactionCategory"))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
//                                                        categoryListExpense.clear();
//                                                        categoryListIncome.clear();
                                                        boolean added = false;
                                                        if (document.getString("categoryType").equals("expense")) {
                                                            sumExpense += lastAmount;
                                                            if (categoryListExpense.size() == 0) {
                                                                currCategory = new CategoryTotal(document.getId(), document.getString("categoryName"), document.getString("categoryType"), lastAmount);
                                                                categoryListExpense.add(currCategory);
                                                                categoryList.add(currCategory);
                                                                entriesExpense.add(new PieEntry(currCategory.getTotal() / (float) sumExpense, currCategory.getName()));
                                                            } else {
                                                                for (CategoryTotal cat : categoryList) {
                                                                    if (cat.getId().equals(document.getId())) {
                                                                        cat.setTotal(cat.getTotal() + lastAmount);
                                                                        added = true;
                                                                        Log.d("PROFILE FRAGMENT", "onComplete: ada category yg sama");
                                                                        break;
                                                                    }
                                                                }
                                                                if (!added) {
                                                                    currCategory = new CategoryTotal(document.getId(), document.getString("categoryName"), document.getString("categoryType"), lastAmount);
                                                                    categoryListExpense.add(currCategory);
                                                                    categoryList.add(currCategory);
                                                                    entriesExpense.add(new PieEntry(currCategory.getTotal() / (float) sumExpense, currCategory.getName()));
                                                                }
                                                            }
                                                        } else {
                                                            sumIncome += lastAmount;
                                                            if (categoryListIncome.size() == 0) {
                                                                currCategory = new CategoryTotal(document.getId(), document.getString("categoryName"), document.getString("categoryType"), lastAmount);
                                                                categoryListIncome.add(currCategory);
                                                                categoryList.add(currCategory);
                                                                entriesIncome.add(new PieEntry(currCategory.getTotal() / (float) sumIncome, currCategory.getName()));
                                                            } else {
                                                                for (CategoryTotal cat : categoryList) {
                                                                    if (cat.getId().equals(document.getId())) {
                                                                        cat.setTotal(cat.getTotal() + lastAmount);
                                                                        added = true;
                                                                        break;
                                                                    }
                                                                }
                                                                if (!added) {
                                                                    currCategory = new CategoryTotal(document.getId(), document.getString("categoryName"), document.getString("categoryType"), lastAmount);
                                                                    categoryListIncome.add(currCategory);
                                                                    categoryList.add(currCategory);
                                                                    entriesIncome.add(new PieEntry(currCategory.getTotal() / (float) sumIncome, currCategory.getName()));
                                                                }
                                                            }
                                                        }

                                                    }
//                                                    for (CategoryTotal cat : categoryListExpense) {
//                                                        entriesExpense.add(new PieEntry(cat.getTotal() / (float) sumExpense, cat.getName()));
//                                                    }
//                                                    for (CategoryTotal cat : categoryListIncome) {
//                                                        entriesIncome.add(new PieEntry(cat.getTotal() / (float) sumIncome, cat.getName()));
//                                                    }
                                                    ArrayList<Integer> colors = new ArrayList<>();
                                                    for (int color : ColorTemplate.MATERIAL_COLORS) {
                                                        colors.add(color);
                                                    }
                                                    for (int color : ColorTemplate.VORDIPLOM_COLORS) {
                                                        colors.add(color);
                                                    }
                                                    PieDataSet dataSet = new PieDataSet(entriesExpense, "");
                                                    PieDataSet dataSetIncome = new PieDataSet(entriesIncome, "");
                                                    dataSet.setColors(colors);
                                                    dataSetIncome.setColors(colors);

                                                    PieData data = new PieData(dataSet);
                                                    PieData dataIncome = new PieData(dataSetIncome);
                                                    data.setDrawValues(true);
                                                    data.setValueFormatter(new PercentFormatter(pieChart));
                                                    data.setValueTextSize(12f);
                                                    data.setValueTextColor(Color.BLACK);

                                                    dataIncome.setDrawValues(true);
                                                    dataIncome.setValueFormatter(new PercentFormatter(pieChart));
                                                    dataIncome.setValueTextSize(12f);
                                                    dataIncome.setValueTextColor(Color.BLACK);

                                                    pieChart.setData(data);
                                                    pieChart.invalidate();

                                                    pieChart.setDrawHoleEnabled(true);
                                                    pieChart.setUsePercentValues(true);
                                                    pieChart.setCenterText(getString(R.string.expense));
                                                    pieChart.getDescription().setEnabled(false);
                                                    pieChart.animateXY(2000, 2000);

                                                    pieChartIncome.setData(dataIncome);
                                                    pieChartIncome.invalidate();

                                                    pieChartIncome.setDrawHoleEnabled(true);
                                                    pieChartIncome.setUsePercentValues(true);
                                                    pieChartIncome.setCenterText(getString(R.string.income));
                                                    pieChartIncome.getDescription().setEnabled(false);
                                                    pieChartIncome.animateXY(2000, 2000);
                                                }
                                            }
                                        });
                            }
                        }

                    }
                });
    }
}