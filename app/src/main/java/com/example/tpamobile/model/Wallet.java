package com.example.tpamobile.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class Wallet implements Serializable {
    String id, name;
    Integer amount;
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
//    private final String TAG = "WALLET CLASS";

    public Wallet(String id, String name, Integer amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String formatRupiah(){
        Locale localeID = new Locale("IND", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        String rupiahFormatted = formatRupiah.format(Double.parseDouble(this.amount.toString()));
        String[] split = rupiahFormatted.split(",");
        int length = split[0].length();
        return split[0].substring(0,2)+". "+split[0].substring(2,length);
    }

//    public void getCurrentAmount(){
//        db.collection("users")
//                .document(currUser.getUid())
//                .collection("wallets")
//                .document(this.getId())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()){
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()){
//                                Integer walletAmount = document.getLong("walletAmount").intValue();
//                                Log.d(TAG, "onComplete: " + walletAmount);
//                                List<String> transactionList = (List<String>) document.get("transactions");
//                                for (String t : transactionList){
//                                    Log.d(TAG, "onComplete: " + getTransaction(t));
//                                    walletAmount -= getTransaction(t);
//                                }
//                                amount = walletAmount;
//                            }
//                        }
//                    }
//                });
//    }
//
//    public Integer getTransaction(String transactionID){
//        final Integer[] transactionAmount = new Integer[1];
//        db.collection("users")
//                .document(currUser.getUid())
//                .collection("transactions")
//                .document(transactionID)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()){
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()){
//                                Log.d(TAG, "onComplete: " + document.getLong("transactionAmount").intValue());
//                                transactionAmount[0] = document.getLong("transactionAmount").intValue();
//                            }
//                        }
//                    }
//                });
//        return transactionAmount[0];
//    }
}
