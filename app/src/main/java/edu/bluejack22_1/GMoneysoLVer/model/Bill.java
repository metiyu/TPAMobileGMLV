package edu.bluejack22_1.GMoneysoLVer.model;

import edu.bluejack22_1.GMoneysoLVer.R;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Bill implements Serializable {
    private String id, description, repeatValue, paidStatus, dueDate;
    private Integer billAmount, occurences;
    private Wallet wallet;



    private Category category;

    public Bill() {

    }
    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    private Date billDate;

    public Bill(String id, String description, String repeatValue, String paidStatus, Integer billAmount, Integer year, Integer month, Integer day, Date date, String due, String due_today, String was_due_on){
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
            this.dueDate =  due+" "+day+"/"+month+"/"+year;
        }
        else if (year==curr_year && month==curr_month && curr_day == day){
            this.dueDate = due_today+"";
        }
        else{
            this.dueDate = was_due_on+" "+day+"/"+month+"/"+year;
        }
    }
    public Bill(String id, String catId, String description, String repeatValue, String paidStatus, Integer billAmount, Integer year, Integer month, Integer day, Date date, String due, String due_today, String was_due_on){
        this.id = id;
        this.description = description;
        this.repeatValue = repeatValue;
        this.paidStatus = paidStatus;
        this.billAmount = billAmount;
        int curr_year,curr_month,curr_day;
        Category billCat= new Category();
        billCat.setId(catId);
        this.category = billCat;
        curr_year = Calendar.getInstance().get(Calendar.YEAR);
        curr_month = Calendar.getInstance().get(Calendar.MONTH)+1;
        month++;
        curr_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if(year > curr_year || (year == curr_year && month > curr_month) ||(year == curr_year && month == curr_month && day > curr_day)){
            this.dueDate =  due+" "+day+"/"+month+"/"+year;
        }
        else if (year==curr_year && month==curr_month && curr_day == day){
            this.dueDate = due_today+"";
        }
        else{
            this.dueDate = was_due_on+" "+day+"/"+month+"/"+year;
        }
    }

    public Integer getOccurences() {
        return occurences;
    }

    public void setOccurences(Integer occurences) {
        this.occurences = occurences;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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
    public String formatRupiah(){
        Locale localeID = new Locale("IND", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        String rupiahFormatted = formatRupiah.format(Double.parseDouble(this.billAmount.toString()));
        String[] split = rupiahFormatted.split(",");
        int length = split[0].length();
        return split[0].substring(0,2)+". "+split[0].substring(2,length);
    }
}
