package com.example.tpamobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private String email;
    private Button btn_edit_profile;
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