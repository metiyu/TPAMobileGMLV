package com.example.tpamobile.model;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private String TransactionID, TransactionNote;
    private Integer TransactionAmount;
    private Category TransactionCategory;
    private Wallet TransactionWallet;
    private Date TransactionDate;

    public Transaction() {
    }

    public String getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(String transactionID) {
        TransactionID = transactionID;
    }

    public String getTransactionNote() {
        return TransactionNote;
    }

    public void setTransactionNote(String transactionNote) {
        TransactionNote = transactionNote;
    }

    public Integer getTransactionAmount() {
        return TransactionAmount;
    }

    public void setTransactionAmount(Integer transactionAmount) {
        TransactionAmount = transactionAmount;
    }

    public Category getTransactionCategory() {
        return TransactionCategory;
    }

    public void setTransactionCategory(Category transactionCategory) {
        TransactionCategory = transactionCategory;
    }

    public Wallet getTransactionWallet() {
        return TransactionWallet;
    }

    public void setTransactionWallet(Wallet transactionWallet) {
        TransactionWallet = transactionWallet;
    }

    public Date getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        TransactionDate = transactionDate;
    }
}
