package com.example.quintorapplication.models;

import com.example.quintorapplication.enums.TransactionType;

import java.time.LocalDate;

import com.example.quintorapplication.util.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class KasgeldModel {
    public double amountInEuro;
    public String transactionReference;
    public LocalDate date;
    public TransactionType transactionType;
    public String description;

    public KasgeldModel(double amountInEuro, String transactionReference, LocalDate date, TransactionType transactionType, String description) {
        this.amountInEuro = amountInEuro;
        this.transactionReference = transactionReference;
        this.date = date;
        this.transactionType = transactionType;
        this.description = description;
    }

    public String convertToJson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        KasgeldModel model = new KasgeldModel(amountInEuro, transactionReference, date, transactionType, description);
        return gson.toJson(model);
    }

    public String convertToXml() {
        return
                "<transaction>" +
                        "<amount_in_euro>" + amountInEuro + "</amount_in_euro>" +
                        "<transaction_reference>" + transactionReference + "</transaction_reference>" +
                        "<value_date>" + date + "</value_date>" +
                        "<transaction_type>" + transactionType + "</transaction_type>" +
                        "<description>" + description + "</description>" +
                "</transaction>";
    }


    public double getAmountInEuro() {
        return amountInEuro;
    }

    public void setAmountInEuro(double amountInEuro) {
        this.amountInEuro = amountInEuro;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
