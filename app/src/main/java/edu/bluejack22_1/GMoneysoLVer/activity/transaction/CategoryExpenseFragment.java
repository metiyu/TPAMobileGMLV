package edu.bluejack22_1.GMoneysoLVer.activity.transaction;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.adapter.CategoryAdapter;
import edu.bluejack22_1.GMoneysoLVer.model.Category;
import edu.bluejack22_1.GMoneysoLVer.model.Transaction;

import edu.bluejack22_1.GMoneysoLVer.databinding.FragmentCategoryExpenseBinding;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryExpenseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentCategoryExpenseBinding binding;
    private RecyclerView rv_categories;
    private List<Category> categoryList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategoryExpenseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryExpenseFragment newInstance(String param1, String param2) {
        CategoryExpenseFragment fragment = new CategoryExpenseFragment();
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
        binding = FragmentCategoryExpenseBinding.inflate(
                inflater,  container, false);

        Transaction transaction = (Transaction) getActivity().getIntent().getSerializableExtra("currTransaction");

        rv_categories = binding.rvCategories;
        categoryAdapter = new CategoryAdapter(CategoryExpenseFragment.this.getActivity(), categoryList, transaction);

        progressDialog = new ProgressDialog(CategoryExpenseFragment.this.getActivity());
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.fetching));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CategoryExpenseFragment.this.getActivity(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(CategoryExpenseFragment.this.getActivity(), DividerItemDecoration.VERTICAL);
        rv_categories.setLayoutManager(layoutManager);
        rv_categories.addItemDecoration(decoration);
        rv_categories.setAdapter(categoryAdapter);

        getData();

        return binding.getRoot();
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
                            Toast.makeText(CategoryExpenseFragment.this.getActivity(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshot : value){
                            if(snapshot.getString("categoryName") != null && snapshot.getString("categoryType") != null){
                                if (snapshot.getString("categoryType").equalsIgnoreCase("expense")){
                                    Category category = new Category(snapshot.getId(), snapshot.getString("categoryName"), snapshot.getString("categoryType"));
                                    categoryList.add(category);
                                }
                            }
                            categoryAdapter.notifyDataSetChanged();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}