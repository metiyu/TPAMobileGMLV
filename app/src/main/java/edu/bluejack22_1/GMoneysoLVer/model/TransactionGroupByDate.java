package edu.bluejack22_1.GMoneysoLVer.model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.List;

public class TransactionGroupByDate {

    private Date date;
    private List<Transaction> transactionList;
    private Integer subTotalAmount = 0;

    public TransactionGroupByDate(Date date, List<Transaction> transactionList) {
        this.date = date;
        this.transactionList = transactionList;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public Integer getSubTotalAmount(){
        return this.subTotalAmount;
    }

    public void setSubTotalAmount(Integer subTotalAmount) {
        this.subTotalAmount = subTotalAmount;
    }

    public String getSubTotalAmountFormatted(){
        subTotalAmount = 0;
        for(Transaction t : transactionList){
            if (t.getTransactionCategory().getType().equals("expense"))
                subTotalAmount -= t.getTransactionAmount();
            if (t.getTransactionCategory().getType().equals("income"))
                subTotalAmount += t.getTransactionAmount();
        }
        return formatRupiah(subTotalAmount);
    }

    public String formatRupiah(Integer amount){
        DecimalFormat IndExcRate = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');
        IndExcRate.setDecimalFormatSymbols(formatRp);
        return IndExcRate.format(amount);
    }
}
