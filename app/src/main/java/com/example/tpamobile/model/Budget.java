package com.example.tpamobile.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class Budget implements Serializable {
    private String id;
    private Category category;
    private Integer amount, month, year;
    private List<Transaction> transactionList = new ArrayList<>();

    public Budget() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public Integer getAllTransactionAmount(){
        int sum = 0;
        if (transactionList.size() > 0){
            for (Transaction t : transactionList){
                sum += t.getTransactionAmount();
            }
        }
        return sum;
    }

    public String getAllTransactionAmountFormatted(){
        int sum = 0;
        if (transactionList.size() > 0){
            for (Transaction t : transactionList){
                sum += t.getTransactionAmount();
            }
        }
        DecimalFormat IndExcRate = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        IndExcRate.setDecimalFormatSymbols(formatRp);
        return IndExcRate.format(sum);
    }

    public String formatRupiah(){
        DecimalFormat IndExcRate = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        IndExcRate.setDecimalFormatSymbols(formatRp);
        return IndExcRate.format(this.amount);
    }
}
