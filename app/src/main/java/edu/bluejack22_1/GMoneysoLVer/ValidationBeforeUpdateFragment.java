package edu.bluejack22_1.GMoneysoLVer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.databinding.FragmentValidationBeforeUpdateBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ValidationBeforeUpdateFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ValidationBeforeUpdateFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseUser user;
    private Button btn_submit_old_password;
    private EditText old_password_tf;
    private String email;
    private FragmentValidationBeforeUpdateBinding binding;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ValidationBeforeUpdateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ValidationBeforeUpdateFragment newInstance(String param1, String param2) {
        ValidationBeforeUpdateFragment fragment = new ValidationBeforeUpdateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ValidationBeforeUpdateFragment() {
        // Required empty public constructor
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
        binding = FragmentValidationBeforeUpdateBinding.inflate(
                inflater,  container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.validation_before_edit));
        email = user.getEmail();
        btn_submit_old_password = binding.btnSubmitValidationOldPassword;
        old_password_tf = binding.oldPasswordTf;
        btn_submit_old_password.setOnClickListener(x->{
            navigateToEditProfile(old_password_tf.getText().toString());
        });
        return binding.getRoot();

    }

    private void navigateToEditProfile(String secretCode){
        Log.d("This is secretCode:", secretCode);
        Log.d("disini", "disini");
        AuthCredential credential = EmailAuthProvider.getCredential(email, secretCode);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("secretCode",secretCode);
                    EditProfileFragment editFrag = new EditProfileFragment();
                    editFrag.setArguments(bundle);
                    replaceFragment(editFrag);
                    Log.d("asd", "onComplete: addProfile");
                }
                else{
                    Log.e("error","error");
                }
            }
        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}