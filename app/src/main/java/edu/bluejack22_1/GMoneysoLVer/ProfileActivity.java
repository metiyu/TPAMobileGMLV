package edu.bluejack22_1.GMoneysoLVer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.bluejack22_1.GMoneysoLVer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private String email;
    private LinearLayout btn_edit_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = new Intent(this, ValidationBeforeUpdateActivity.class);
        if (user != null) {
            email = user.getEmail();
            Log.d("value of email is ", String.valueOf(email));
            TextView tv = (TextView) findViewById(R.id.current_user_email_tv);
            tv.setText(email);
        }
        btn_edit_profile = findViewById(R.id.btn_edit_profile);
        btn_edit_profile.setOnClickListener(x->{
            startActivity(intent);
        });
    }
}