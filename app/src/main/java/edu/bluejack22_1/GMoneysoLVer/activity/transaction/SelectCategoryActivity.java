package edu.bluejack22_1.GMoneysoLVer.activity.transaction;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.R;

import edu.bluejack22_1.GMoneysoLVer.activity.transaction.adapter.CategoryPagerAdapter;
import edu.bluejack22_1.GMoneysoLVer.adapter.CategoryAdapter;
import edu.bluejack22_1.GMoneysoLVer.model.Bill;
import edu.bluejack22_1.GMoneysoLVer.model.Budget;
import edu.bluejack22_1.GMoneysoLVer.model.Category;
import edu.bluejack22_1.GMoneysoLVer.model.Transaction;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectCategoryActivity extends AppCompatActivity {

    private RecyclerView rv_categories;
    private List<Category> categoryList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TabLayout tl_category;
    private ViewPager2 vp_category;
    private CategoryPagerAdapter pagerAdapter;
    public static Bill bill_in_select_category;
    public static Transaction transaction_in_select_category;
    public static Budget budget_in_select_category;
    public static boolean from_edit_budget, from_add_budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.select_category));

        tl_category = findViewById(R.id.tl_category);
        vp_category = findViewById(R.id.vp_category);

        if ((Bill) getIntent().getSerializableExtra("currBill") != null){
            bill_in_select_category = (Bill) getIntent().getSerializableExtra("currBill");
            SelectCategoryActivity.transaction_in_select_category = null;
            budget_in_select_category = null;
            from_edit_budget=false;
            from_add_budget=false;
        }
        if ((Transaction) getIntent().getSerializableExtra("currTransaction") != null){
            transaction_in_select_category = (Transaction) getIntent().getSerializableExtra("currTransaction");
            bill_in_select_category = null;
            budget_in_select_category = null;
            from_edit_budget=false;
            from_add_budget=false;
        }
        if ((Budget) getIntent().getSerializableExtra("currBudget") != null){
            budget_in_select_category = (Budget) getIntent().getSerializableExtra("currBudget");
            bill_in_select_category = null;
            SelectCategoryActivity.transaction_in_select_category = null;
            if(getIntent().getExtras().getString("fromEditBudget")!=null){
                from_edit_budget=true;
                from_add_budget=false;
            }
            else{
                from_edit_budget=false;
                from_add_budget=true;
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        pagerAdapter = new CategoryPagerAdapter(fragmentManager, getLifecycle());
        vp_category.setAdapter(pagerAdapter);
        tl_category.addTab(tl_category.newTab().setText(getString(R.string.expense)));
        if ((Bill) getIntent().getSerializableExtra("currBill") == null && (Budget) getIntent().getSerializableExtra("currBudget") == null){
            tl_category.addTab(tl_category.newTab().setText(getString(R.string.income)));
        }


        tl_category.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_category.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp_category.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tl_category.selectTab(tl_category.getTabAt(position));
            }
        });

//        Transaction transaction = (Transaction) getIntent().getSerializableExtra("currTransaction");
//
//        rv_categories = findViewById(R.id.rv_categories);
//        categoryAdapter = new CategoryAdapter(SelectCategoryActivity.this, categoryList, transaction);
//
//        progressDialog = new ProgressDialog(SelectCategoryActivity.this);
//        progressDialog.setTitle("Loading");
//        progressDialog.setMessage("Fetching...");
//
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SelectCategoryActivity.this, LinearLayoutManager.VERTICAL, false);
//        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(SelectCategoryActivity.this, DividerItemDecoration.VERTICAL);
//        rv_categories.setLayoutManager(layoutManager);
//        rv_categories.addItemDecoration(decoration);
//        rv_categories.setAdapter(categoryAdapter);
//
//        getData();
    }

    private void getData(){
        progressDialog.show();
        db.collection("categories")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        categoryList.clear();
                        if(error != null){
                            Toast.makeText(SelectCategoryActivity.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshot : value){
                            if(snapshot.getString("categoryName") != null && snapshot.getString("categoryType") != null){
                                Category category = new Category(snapshot.getId(), snapshot.getString("categoryName"), snapshot.getString("categoryType"));
                                categoryList.add(category);
                            }
                            categoryAdapter.notifyDataSetChanged();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}