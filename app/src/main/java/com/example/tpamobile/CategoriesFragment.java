package com.example.tpamobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tpamobile.databinding.FragmentCategoriesBinding;
import com.example.tpamobile.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
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
    String s1[];
    int images[] = {R.drawable.ic_baseline_home_24,R.drawable.ic_baseline_person_24,R.drawable.ic_baseline_list_alt_24};
    Button btn_add_category;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCategoriesBinding.inflate(
                inflater,  container, false);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        s1 = getResources().getStringArray(R.array.list_name);

        btn_add_category = binding.btnAddCategory;
        btn_add_category.setOnClickListener(x -> {
            startActivity(new Intent(CategoriesFragment.this.getActivity(), AddCategoryActivity.class));
        });

        db.collection("categories")
                .whereEqualTo("state", "CA")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("GET DATA FROM CATEGORIES", "Listen failed.", error);
                            return;
                        }

                        List<String> categories = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Log.d("GET DATA FROM CATEGORIES", "doc: " + doc.getId());
                            if (doc.get("categoryName") != null) {
                                categories.add(doc.getString("categoryName"));
                            }
                        }
                        Log.d("GET DATA FROM CATEGORIES", "Current cites in CA: " + categories);

                    }
                });

        rv_categories = binding.rvCategories;
        CategoryAdapter categoryAdapter = new CategoryAdapter(this.getContext(), s1, images);
        rv_categories.setAdapter(categoryAdapter);
        rv_categories.setLayoutManager(new LinearLayoutManager(this.getContext()));

        return binding.getRoot();
    }
}