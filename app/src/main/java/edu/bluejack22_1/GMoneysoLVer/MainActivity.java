package edu.bluejack22_1.GMoneysoLVer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.bluejack22_1.GMoneysoLVer.R;

import edu.bluejack22_1.GMoneysoLVer.model.Wallet;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnSignup, btnChange;
    SignInButton btnSignInGoogle;
    private EditText et_fcm_token;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = findViewById(R.id.btn_gotosignin);
        btnSignIn.setOnClickListener(x -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        });

        btnSignup = findViewById(R.id.btn_gotosignup);
        btnSignup.setOnClickListener(x -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        //google sign in
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        btnSignInGoogle = findViewById(R.id.btn_sign_in_google);
        btnSignInGoogle.setOnClickListener(x -> {
            Log.d(TAG, "onClick: begin Google Sign in");
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
        });

        et_fcm_token = findViewById(R.id.et_fcm_token);
        et_fcm_token.setVisibility(View.GONE);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
//                        et_fcm_token.setText(token);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Log.d(TAG, "onActivityResult: begin Google Sign in intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
                Log.d(TAG, account.getIdToken());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "onActivityResult: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "onSuccess: Logged in");

                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        String uid = user.getUid();
                        String email = user.getEmail();

                        Log.d(TAG, "onSuccess: UID -> " + uid);
                        Log.d(TAG, "onSuccess: Email -> " + email);

                        //check if user new or existing
                        if(authResult.getAdditionalUserInfo().isNewUser()){
                            Log.d(TAG, "onSuccess: Account Created");
                            Toast.makeText(MainActivity.this, "Account Created...\n"+email, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "onSuccess: Existing User");
                            Toast.makeText(MainActivity.this, "Existing User...\n"+email, Toast.LENGTH_SHORT).show();
                        }

                        checkUser(user, MainActivity.this);

                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Loggin failed");
                        Log.d(TAG, "onFailure: " + e.getMessage());
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
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
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