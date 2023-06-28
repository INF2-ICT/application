package com.example.quintorapplication.models;

import com.example.quintorapplication.enums.TransactionType;

import java.time.LocalDate;

public class SingleTransactionModel {
    private int id;
    private double amountInEuro;
    private String description;
    private LocalDate value_date;
    private TransactionType transactionType;
    private String identificationCode;
    private String ownerReferential;

    public SingleTransactionModel(int id, double amountInEuro, String description, LocalDate value_date, TransactionType transactionType, String identificationCode, String ownerReferential) {
        this.id = id;
        this.amountInEuro = amountInEuro;
        this.description = description;
        this.value_date = value_date;
        this.transactionType = transactionType;
        this.identificationCode = identificationCode;
        this.ownerReferential = ownerReferential;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmountInEuro() {
        return amountInEuro;
    }

    public void setAmountInEuro(double amountInEuro) {
        this.amountInEuro = amountInEuro;
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
