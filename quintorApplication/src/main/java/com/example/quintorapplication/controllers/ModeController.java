package com.example.quintorapplication.controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ModeController {
    private String mode;
    private String parserEndpoint;
    private String MT940Endpoint;
    private String allTransactionsEndpoint;

    public ModeController() {
        this.mode = "JSON";
        this.parserEndpoint = "MT940toJSON";
        this.MT940Endpoint = "post-json";
        this.allTransactionsEndpoint = "get-all-transactions-json";
    }

    /**
     * Function to set mode of application. JSON or XML. Standard = JSON
     * @param mode JSON or XML
     * @return ActionEvent
     */
    public EventHandler<ActionEvent> setMode(String mode) {
        //Set variables based on application mode
        this.mode = mode;

        switch (this.getMode()) {
            case "JSON" -> {
                this.parserEndpoint = "MT940toJSON";
                this.MT940Endpoint = "post-json";
                this.allTransactionsEndpoint = "get-all-transactions-json";
            }
            case "XML" -> {
                this.parserEndpoint = "MT940toXML";
                this.MT940Endpoint = "post-xml";
                this.allTransactionsEndpoint = "get-all-transactions-xml";
            }
        }

        return null;
    }

    public String getMode() {
        return mode;
    }

    public String getParserEndpoint() {
        return parserEndpoint;
    }

    public String getMT940Endpoint() {
        return MT940Endpoint;
    }

    public String getAllTransactionsEndpoint() {
        return allTransactionsEndpoint;
    }
}
