package edu.bluejack22_1.GMoneysoLVer;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.bluejack22_1.GMoneysoLVer.R;
import edu.bluejack22_1.GMoneysoLVer.databinding.FragmentEditProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseUser user;
    private Button btn_update_profile;
    private EditText emailInput, newPasswordInput, confirmNewPasswordInput;
    private String email = "";
    private String secretCode = "";
    private String new_email = "";
    private String new_password = "";
    private String confirm_new_password = "";
    private FragmentEditProfileBinding binding;
    private FirebaseAuth mAuth;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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
        binding = FragmentEditProfileBinding.inflate(
                inflater,  container, false);
        btn_update_profile = binding.btnUpdateProfile;
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.edit_profile));
        Log.d("check log", "test log");
        user = FirebaseAuth.getInstance().getCurrentUser();
        Bundle extras = getArguments();
        secretCode = extras.getString("secretCode");
        if (secretCode!=null) {
            Log.d(TAG, secretCode);
            //The key argument here must match that used in the other activity
        }
        if (user != null) {
            email = user.getEmail();
            Log.d("value of secretCode is ", String.valueOf(secretCode));
            TextView tv = binding.emailTv;
            emailInput = binding.emailEt;
            emailInput.setText(email);
            newPasswordInput = binding.newPasswordEt;
            confirmNewPasswordInput = binding.confirmNewPasswordEt;
        }
        btn_update_profile.setOnClickListener(x->{
            new_email = emailInput.getText().toString();
            new_password = newPasswordInput.getText().toString();
            confirm_new_password = confirmNewPasswordInput.getText().toString();

            if(new_password.isEmpty()){
                onlyUpdateEmail();
//                replaceFragment(new ProfileFragment());
            }
            else{
                if(new_password.equals(confirm_new_password)){
                    updateEmail();
//                    replaceFragment(new ProfileFragment());
                }
                else{

                }

            }

        });
        return binding.getRoot();
    }
    protected void updateEmail(){
        new_email = emailInput.getText().toString();
        new_password = newPasswordInput.getText().toString().trim();
        confirm_new_password = confirmNewPasswordInput.getText().toString().trim();
        Log.d(TAG, new_password);
        Log.d(TAG, confirm_new_password);
        mAuth = FirebaseAuth.getInstance();
        Intent intent = new Intent(getContext(), HomeActivity.class);
        AuthCredential credential = EmailAuthProvider.getCredential(email, secretCode);
        user.reauthenticate(credential).addOnCompleteListener(x->{
                    user.updateEmail(new_email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("message:", "User email address updated.");
                                        user.updatePassword(new_password)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("message", "User password updated.");
                                                            FirebaseAuth.getInstance().signOut();
                                                            Log.d(TAG, "onComplete: "+new_email);
                                                            Log.d(TAG, "onComplete: "+new_password);
                                                            mAuth.signInWithEmailAndPassword(new_email, new_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    intent.putExtra("fragmentToGo", "profile");
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                        }
                                                    }
                                                });


                                    }
                                    else{
                                        Log.e("error","error");
                                        startActivity(intent);
                                    }

                                }
                            });

                }
        );

    }
    protected void onlyUpdateEmail(){
        new_email = emailInput.getText().toString();
        AuthCredential credential = EmailAuthProvider.getCredential(email, secretCode);
        Intent intent = new Intent(getContext(), HomeActivity.class);
        user.reauthenticate(credential).addOnCompleteListener(x->{
                    user.updateEmail(new_email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("message:", "User email address updated.");
                                        mAuth.signOut();
                                        mAuth.signInWithEmailAndPassword(new_email, new_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                intent.putExtra("fragmentToGo", "profile");
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                    else{
                                        Log.e("error","error");
                                        startActivity(intent);
                                    }
//                                    replaceFragment(new ProfileFragment());
                                }
                            });
                }
        );

    }
//    private void replaceFragment(Fragment fragment){
//        FragmentManager fragmentManager = getParentFragment().getActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout,fragment);
//        fragmentTransaction.commit();
//    }
}