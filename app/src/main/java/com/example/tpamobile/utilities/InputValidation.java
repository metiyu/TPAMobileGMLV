package com.example.tpamobile.utilities;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class InputValidation {
    private Context context;

    public InputValidation(Context context) {
        this.context = context;
    }

    public boolean isEmpty(EditText et, TextInputLayout til, String msg){
        String text = et.getText().toString().trim();
        if(text.isEmpty()){
            til.setError(msg);
            return false;
        }
        else {
            til.setErrorEnabled(false);
        }
        return true;
    }

    public boolean emailValidation(EditText et,TextInputLayout til, String msg){
        String text = et.getText().toString().trim();
        if(text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(text).matches()){
            til.setError(msg);
            return false;
        }
        else {
            til.setErrorEnabled(false);
        }
        return true;
    }
}
