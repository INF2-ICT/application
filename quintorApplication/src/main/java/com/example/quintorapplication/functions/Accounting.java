package com.example.quintorapplication.functions;

public class Accounting {

    private String accountNumberId;
    private String billingDate;
    private String balance;
    private String moneyAmount;
    private String singleAccountData;

    public Accounting(String accountNumberId, String billingDate, String balance, String moneyAmount, String singleAccountData) {
        this.accountNumberId = accountNumberId;
        this.billingDate = billingDate;
        this.balance = balance;
        this.moneyAmount = moneyAmount;
        this.singleAccountData = singleAccountData;
    }

    public String getAccountNumberId() {
        return this.accountNumberId;
    }

    public String getBillingDate() {
        return this.billingDate;
    }

    public String getBalance() {
        return this.balance;
    }

    public String getMoneyAmount() {
        return this.moneyAmount;
    }

    public String getSingleAccountData() {
        return this.singleAccountData;
    }

}
