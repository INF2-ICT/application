package com.example.quintorapplication.models;

import com.example.quintorapplication.enums.TransactionType;

import java.time.LocalDate;

public class SingleTransactionModel {
    private double amount_in_euro;
    private String description;
    private LocalDate value_date;
    private TransactionType transactionType;
    private String identificationCode;
    private String ownerReferential;

    public SingleTransactionModel(double amount_in_euro, String description, LocalDate value_date, TransactionType transactionType, String identificationCode, String ownerReferential) {
        this.amount_in_euro = amount_in_euro;
        this.description = description;
        this.value_date = value_date;
        this.transactionType = transactionType;
        this.identificationCode = identificationCode;
        this.ownerReferential = ownerReferential;
    }

    public double getAmount_in_euro() {
        return amount_in_euro;
    }

    public void setAmount_in_euro(double amount_in_euro) {
        this.amount_in_euro = amount_in_euro;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getValue_date() {
        return value_date;
    }

    public void setValue_date(LocalDate value_date) {
        this.value_date = value_date;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getOwnerReferential() {
        return ownerReferential;
    }

    public void setOwnerReferential(String ownerReferential) {
        this.ownerReferential = ownerReferential;
    }
}
