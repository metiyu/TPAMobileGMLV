package com.example.tpamobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private EditText et_email_login, et_pass_login;
    private TextInputLayout til_email_login, til_pass_login;
    private Button btn_signin;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

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
        Intent intent = new Intent(this, HomeActivity.class);
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(intent);
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
}