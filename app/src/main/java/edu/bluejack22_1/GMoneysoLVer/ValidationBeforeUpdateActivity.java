package edu.bluejack22_1.GMoneysoLVer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.tpamobile.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ValidationBeforeUpdateActivity extends AppCompatActivity {

    private FirebaseUser user;
    private Button btn_submit_old_password;
    private EditText old_password_tf;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation_before_update);
        user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
        btn_submit_old_password = findViewById(R.id.btn_submit_validation_old_password);
        old_password_tf = findViewById(R.id.old_password_tf);
        btn_submit_old_password.setOnClickListener(x->{
            navigateToEditProfile(old_password_tf.getText().toString().trim());
        });
    }

    private void navigateToEditProfile(String secretCode){
        Log.d("This is secretCode:", secretCode);
        Log.d("disini", "disini");
        Intent intent = new Intent(this, EditProfileActivity.class);
        AuthCredential credential = EmailAuthProvider.getCredential(email, secretCode);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    intent.putExtra("secretCode",secretCode);
                    startActivity(intent);
                    setContentView(R.layout.activity_edit_profile);
                }
                else{
                    Log.e("error","error");
                }
            }
        });
    }
}