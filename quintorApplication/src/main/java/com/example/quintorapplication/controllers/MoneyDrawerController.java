package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.util.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class MoneyDrawerController {
    private Stage stage;
    @FXML
    private TextField amountField;

    @FXML
    private TextField descriptionField;

    @FXML
    private Button AddButton;

    public void addMoney(ActionEvent event) {
        Double amount = Double.parseDouble(amountField.getText());
        String description = descriptionField.getText();
        DatabaseUtil DB = new DatabaseUtil();

        String params = "amount="+amount+"&description="+description;

        try {
            DB.postMultiParam("post-cash", params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void switchToDashboard(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        // Initialize your fields here
        amountField.setText("");
        descriptionField.setText("");
    }
}
