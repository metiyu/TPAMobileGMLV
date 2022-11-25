package com.example.tpamobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tpamobile.utilities.InputValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    private EditText et_email, et_pass;
    private TextInputLayout til_email, til_pass;
    private Button btn_signup;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private InputValidation inputValidation = new InputValidation(SignUpActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_email = findViewById(R.id.et_email);
        til_email = findViewById(R.id.til_email);
        et_pass = findViewById(R.id.et_pass);
        til_pass = findViewById(R.id.til_pass);
        btn_signup = findViewById(R.id.btn_signup);

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Saving...");

        btn_signup.setOnClickListener(x -> {
            String userEmail = et_email.getText().toString().trim();
            String userPass = et_pass.getText().toString().trim();
            if(!inputValidation.isEmpty(et_email, til_email, "Email must be filled!")){
                return;
            }
            if(!inputValidation.emailValidation(et_email, til_email, "Email invalid")){
                return;
            }
            if(!inputValidation.isEmpty(et_pass, til_pass, "Password must be filled!")){
                return;
            }
            if (!inputValidation.passwordValidation(et_pass, til_pass, "Password length must more than 8, contains uppercase, lowercase and number!")){
                return;
            }
            regisUser(userEmail, userPass);
        });
    }

    private void regisUser(String email, String password){
        mAuth = FirebaseAuth.getInstance();
        Intent intent = new Intent(this, SignInActivity.class);

        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveData(String email, String password){
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);

        progressDialog.show();

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}