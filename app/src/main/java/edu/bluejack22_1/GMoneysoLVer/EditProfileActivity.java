package edu.bluejack22_1.GMoneysoLVer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.bluejack22_1.GMoneysoLVer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileActivity extends AppCompatActivity {

    FirebaseUser user;
    private Button btn_update_profile;
    private EditText emailInput, newPasswordInput, confirmNewPasswordInput;
    private String email = "";
    private String secretCode = "";
    private String new_email = "";
    private String new_password = "";
    private String confirm_new_password = "";
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = new Intent(this, MainActivity.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        btn_update_profile = findViewById(R.id.btn_update_profile);
        Log.d("check log", "test log");
        user = FirebaseAuth.getInstance().getCurrentUser();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            secretCode = extras.getString("secretCode");
            //The key argument here must match that used in the other activity
        }
        if (user != null) {
            email = user.getEmail();
            Log.d("value of secretCode is ", String.valueOf(secretCode));
            TextView tv = (TextView) findViewById(R.id.email_tv);
            emailInput = (EditText) findViewById(R.id.email_et);
            emailInput.setText(email);
            newPasswordInput = (EditText) findViewById(R.id.new_password_et);
            confirmNewPasswordInput = (EditText) findViewById(R.id.confirm_new_password_et);
        }
        btn_update_profile.setOnClickListener(x->{
            new_email = emailInput.getText().toString();
            new_password = newPasswordInput.getText().toString().trim();
            confirm_new_password = confirmNewPasswordInput.getText().toString().trim();

            if(new_password.isEmpty()){
                onlyUpdateEmail();
                startActivity(intent);
                setContentView(R.layout.activity_main);
            }
            else{
                if(new_password.equals(confirm_new_password)){
                    updateEmail();
                    startActivity(intent);
                    setContentView(R.layout.activity_main);
                }
                else{

                }

            }

        });
    }
    protected void updateEmail(){
        new_email = emailInput.getText().toString();
        new_password = newPasswordInput.getText().toString().trim();
        confirm_new_password = confirmNewPasswordInput.getText().toString().trim();
        Log.d(TAG, new_password);
        Log.d(TAG, confirm_new_password);
        Intent intent = new Intent(this, SignInActivity.class);
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
                                            }
                                        }
                                    });
                        }
                        else{
                            Log.e("error","error");
                        }

                    }
                });

            }
        );

    }
    protected void onlyUpdateEmail(){
        new_email = emailInput.getText().toString();
        Intent intent = new Intent(this, SignInActivity.class);
        AuthCredential credential = EmailAuthProvider.getCredential(email, secretCode);
        user.reauthenticate(credential).addOnCompleteListener(x->{
                    user.updateEmail(new_email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("message:", "User email address updated.");
                                    }
                                    else{
                                        Log.e("error","error");
                                    }
                                    setContentView(R.layout.activity_edit_profile);
                                }
                            });
                }
        );

    }
}