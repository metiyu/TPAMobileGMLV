package com.example.tpamobile.activity.category;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tpamobile.BillsFragment;
import com.example.tpamobile.HomeActivity;
import com.example.tpamobile.MainActivity;
import com.example.tpamobile.PlanningFragment;
import com.example.tpamobile.ProfileFragment;
import com.example.tpamobile.R;
import com.example.tpamobile.adapter.CategoryAdapter;
import com.example.tpamobile.databinding.FragmentCategoriesBinding;
import com.example.tpamobile.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentCategoriesBinding binding;
    private RecyclerView rv_categories;
    private List<Category> categoryList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private Button btn_add_category;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
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
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                replaceFragment(new ProfileFragment());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(
                inflater,  container, false);
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Category");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        btn_add_category = binding.btnAddCategory;
        btn_add_category.setOnClickListener(x -> {
            startActivity(new Intent(CategoriesFragment.this.getActivity(), AddCategoryActivity.class));
        });

        rv_categories = binding.rvCategories;
        categoryAdapter = new CategoryAdapter(CategoriesFragment.this.getContext(), categoryList);
//        categoryAdapter.setDialog(new CategoryAdapter.Dialog() {
//            @Override
//            public void onClick(int pos) {
//                final CharSequence[] dialogItem = {"Edit", "Delete"};
//                AlertDialog.Builder dialog = new AlertDialog.Builder(CategoriesFragment.this.getContext());
//                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        switch (i){
//                            case 0:
//
//                                break;
//                            case 1:
//                                deleteData(categoryList.get(pos).getId());
//                                break;
//                        }
//                    }
//                });
//                dialog.show();
//            }
//        });

        progressDialog = new ProgressDialog(CategoriesFragment.this.getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching...");

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CategoriesFragment.this.getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(CategoriesFragment.this.getContext(), DividerItemDecoration.VERTICAL);
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
                            Toast.makeText(CategoriesFragment.this.getContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
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

    private void deleteData(String id){
        progressDialog.show();
        db.collection("categories")
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(CategoriesFragment.this.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CategoriesFragment.this.getContext(), "Failed to fetch", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        replaceFragment(new PlanningFragment());
//        return super.onContextItemSelected(item);
//    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}