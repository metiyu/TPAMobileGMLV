package com.example.tpamobile.model;

import java.util.Calendar;
import java.util.Date;

public class Bill {
    private String id, description, repeatValue, paidStatus, dueDate;
    private Category category;
    private Integer billAmount;
    private Wallet wallet;

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    private Date billDate;

    public Bill(String id, String description, String repeatValue, String paidStatus, Integer billAmount, Integer year, Integer month, Integer day){
        this.id = id;
        this.description = description;
        this.repeatValue = repeatValue;
        this.paidStatus = paidStatus;
        this.billAmount = billAmount;
        int curr_year,curr_month,curr_day;
        curr_year = Calendar.getInstance().get(Calendar.YEAR);
        curr_month = Calendar.getInstance().get(Calendar.MONTH)+1;
        month++;
        curr_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if(year > curr_year || (year == curr_year && month > curr_month) ||(year == curr_year && month == curr_month && day > curr_day)){
            this.dueDate = "Due "+day+"/"+month+"/"+year;
        }
        else if (year==curr_year && month==curr_month && curr_day == day){
            this.dueDate = "Due today";
        }
        else{
            this.dueDate = "Was due on "+day+"/"+month+"/"+year;
        }
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRepeatValue() {
        return repeatValue;
    }

    public void setRepeatValue(String repeatValue) {
        this.repeatValue = repeatValue;
    }

    public String getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(String paidStatus) {
        this.paidStatus = paidStatus;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Integer billAmount) {
        this.billAmount = billAmount;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
