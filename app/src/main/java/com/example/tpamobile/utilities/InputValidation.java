package com.example.tpamobile.utilities;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

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

    public boolean passwordValidation(EditText et, TextInputLayout til, String msg){
        String text = et.getText().toString().trim();
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");
        if (text.length() <= 8 || !UpperCasePatten.matcher(text).find() || !lowerCasePatten.matcher(text).find() || !digitCasePatten.matcher(text).find()){
            til.setError(msg);
            return false;
        }
        else {
            til.setErrorEnabled(false);
        }
        return true;
    }
}
