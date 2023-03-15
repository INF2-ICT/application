package com.example.quintorapplication.functions;

import com.example.quintorapplication.enums.Balance;

import java.time.LocalDate;
import java.util.Date;

public class Accounting {

    private String accountNumberId;
    private LocalDate billingDate;
    private Balance balance;
    private Double moneyAmount;

    private String singleAccountData;


    public Accounting(String accountNumberId, LocalDate billingDate, Balance balance, Double moneyAmount, String singleAccountData) {
        this.accountNumberId = accountNumberId;
        this.billingDate = billingDate;
        this.balance = balance;
        this.moneyAmount = moneyAmount;
        this.singleAccountData = singleAccountData;
    }

    public String getAccountNumberId() {
        return this.accountNumberId;
    }

    public LocalDate getBillingDate() {
        return this.billingDate;
    }

    public Balance getBalance() {
        return this.balance;
    }

    public Double getMoneyAmount() {
        return this.moneyAmount;
    }

    public String getSingleAccountData() {
        return this.singleAccountData;
    }

}
