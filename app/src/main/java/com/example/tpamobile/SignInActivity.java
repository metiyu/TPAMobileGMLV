package com.example.tpamobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tpamobile.activity.wallet.AddWalletActivity;
import com.example.tpamobile.model.Wallet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private EditText et_email_login, et_pass_login;
    private TextInputLayout til_email_login, til_pass_login;
    private Button btn_signin;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        et_email_login = findViewById(R.id.et_email_login);
        til_email_login = findViewById(R.id.til_email_login);
        et_pass_login = findViewById(R.id.et_pass_login);
        til_pass_login = findViewById(R.id.til_pass_login);
        btn_signin = findViewById(R.id.btn_signin);

        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Checking...");

        btn_signin.setOnClickListener(x -> {

            String emailInp = et_email_login.getText().toString().trim();
            String passInp = et_pass_login.getText().toString().trim();
            signInUser(emailInp, passInp);
//            if(!inputValidation.isEmpty(et_email_login, til_email_login, "Email must be filled!")){
//                return;
//            }
//            if(!inputValidation.isEmpty(et_pass_login, til_pass_login, "Password must be filled!")){
//                return;
//            }
//            if(!inputValidation.emailValidation(et_email_login, til_email_login, "Email invalid")){
//                return;
//            }



//                SharedPreferences sharedPreferences = this.getSharedPreferences("USERS", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putInt("id", Integer.parseInt(dbOpenHelper.getUserattribut("ID", "EMAIL", emailInp, "id")));
//                editor.putString("name", dbOpenHelper.getUserattribut("NAME", "EMAIL", emailInp, "name"));
//                editor.putString("email", dbOpenHelper.getUserattribut("EMAIL", "EMAIL", emailInp, "email"));
//                editor.putString("password", dbOpenHelper.getUserattribut("PASSWORD", "EMAIL", emailInp, "password"));
//                editor.putString("phone", dbOpenHelper.getUserattribut("PHONE", "EMAIL", emailInp, "phone"));
//                editor.putInt("money", Integer.parseInt(dbOpenHelper.getUserattribut("MONEY", "EMAIL", emailInp, "money")));
//                editor.apply();
//                startActivity(intent);
        });
    }

    private void signInUser(String email, String password){
        mAuth = FirebaseAuth.getInstance();
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkUser(user, SignInActivity.this);
                            progressDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    public void checkUser(FirebaseUser currUser, Context c){
        db.collection("users")
                .document(currUser.getUid())
                .collection("wallets")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot doc = task.getResult();
                            Log.d("SignInActivity", "onComplete: isEmpty, " + doc.isEmpty());
                            if (!doc.isEmpty()){
                                Log.d("SignInActivity", "onComplete: ada user, " + doc.size());
                                checkWallet(currUser, c);
                            } else {
                                Log.d("SignInActivity", "onComplete: gaada user, " + doc.size());
                                saveCurrUser(currUser, c);
                            }
                        } else {
                            Toast.makeText(c, "Error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }

    private void checkWallet(FirebaseUser currUser, Context c){
        db.collection("users")
                .document(currUser.getUid())
                .collection("wallets")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            Log.d("SignInActivity", "onEvent: " + error.toString());
                            Toast.makeText(SignInActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (QueryDocumentSnapshot snapshot : value){
                            if (snapshot.getLong("walletAmount") != null && snapshot.getString("walletName") != null){
                                Wallet wallet = new Wallet(snapshot.getId(), snapshot.getString("walletName"), snapshot.getLong("walletAmount").intValue());
                                Gson gson = new Gson();
                                String walletJson = gson.toJson(wallet);
                                String userJson = gson.toJson(currUser);

                                SharedPreferences sharedPreferences =  c.getSharedPreferences("app", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("wallet", walletJson);
                                editor.putString("user", userJson);
                                editor.commit();

                                Log.d("SignInActivity", "onEvent: wallet id, " + snapshot.getId());
                                Log.d("SignInActivity", "onEvent: wallet name, " + snapshot.getString("walletName"));
                                Log.d("SignInActivity", "onEvent: wallet amount, " + snapshot.getLong("walletAmount").intValue());

                                Log.d("SignInActivity", "onEvent: context, " + c.getClass());
                                Log.d("SignInActivity", "onEvent: context, " + c.getApplicationContext());

                                startActivity(new Intent(c, HomeActivity.class));
                            }
                        }
                    }
                });
    }

    private void saveCurrUser(FirebaseUser currUser, Context c){
        String walletName = "My Wallet";
        int walletAmount = 0;

        Map<String, Object> wallet = new HashMap<>();
        wallet.put("walletName", walletName);
        wallet.put("walletAmount", walletAmount);

        db.collection("users")
                .document(currUser.getUid())
                .collection("wallets")
                .add(wallet)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Wallet walletObj = new Wallet(documentReference.getId(), walletName, walletAmount);
                        Gson gson = new Gson();
                        String walletJson = gson.toJson(walletObj);
                        String userJson = gson.toJson(currUser);

                        SharedPreferences sharedPreferences = c.getSharedPreferences("app", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("wallet", walletJson);
                        editor.putString("user", userJson);
                        editor.commit();

                        startActivity(new Intent(c, HomeActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}