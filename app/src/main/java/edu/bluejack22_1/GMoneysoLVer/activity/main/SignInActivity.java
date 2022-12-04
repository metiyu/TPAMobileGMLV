package edu.bluejack22_1.GMoneysoLVer.activity.main;

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
import android.widget.TextView;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.R;

import edu.bluejack22_1.GMoneysoLVer.model.Wallet;
import edu.bluejack22_1.GMoneysoLVer.utilities.InputValidation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private EditText et_email_login, et_pass_login;
    SignInButton btnSignInGoogle;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;
    private TextInputLayout til_email_login, til_pass_login;
    private Button btn_signin;
    private FirebaseAuth mAuth;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    private InputValidation inputValidation = new InputValidation(SignInActivity.this);

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
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.checking));

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btnSignInGoogle = findViewById(R.id.btn_sign_in_google);
        TextView textView = (TextView) btnSignInGoogle.getChildAt(0);
        textView.setText(getString(R.string.sign_in));
        btnSignInGoogle.setOnClickListener(x -> {
            Log.d("TAG", "onClick: begin Google Sign in");
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
        });

        btn_signin.setOnClickListener(x -> {

            String emailInp = et_email_login.getText().toString().trim();
            String passInp = et_pass_login.getText().toString().trim();
            if(!inputValidation.isEmpty(et_email_login, til_email_login, "Email must be filled!")){
                return;
            }
            if(!inputValidation.emailValidation(et_email_login, til_email_login, "Email invalid")){
                return;
            }
            if(!inputValidation.isEmpty(et_pass_login, til_pass_login, "Password must be filled!")){
                return;
            }
            signInUser(emailInp, passInp);



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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Log.d("TAG", "onActivityResult: begin Google Sign in intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
                Log.d("TAG", account.getIdToken());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("TAG", "onActivityResult: " + e.getMessage());
            }
        }
    }
    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogleAccount: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d("TAG", "onSuccess: Logged in");

                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        String uid = user.getUid();
                        String email = user.getEmail();

                        Log.d("TAG", "onSuccess: UID -> " + uid);
                        Log.d("TAG", "onSuccess: Email -> " + email);

                        //check if user new or existing
                        if(authResult.getAdditionalUserInfo().isNewUser()){
                            Log.d("TAG", "onSuccess: Account Created");
                            Toast.makeText(SignInActivity.this, "Account Created...\n"+email, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("TAG", "onSuccess: Existing User");
                            Toast.makeText(SignInActivity.this, "Existing User...\n"+email, Toast.LENGTH_SHORT).show();
                        }

                        checkUser(user, SignInActivity.this);

                        startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "onFailure: Loggin failed");
                        Log.d("TAG", "onFailure: " + e.getMessage());
                    }
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
                            Log.d("SignInActivity", "onComplete: task error, " + task.getException().getMessage());
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(),
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