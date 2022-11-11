package com.example.tpamobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnSignin, btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignin = findViewById(R.id.btn_gotosignin);
        btnSignin.setOnClickListener(x -> {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        });

        btnSignup = findViewById(R.id.btn_gotosignup);
        btnSignup.setOnClickListener(x -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }
}