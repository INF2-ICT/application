package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField text;
    @FXML
    private TextField text1;
    private Stage stage;

    @FXML
    protected void onLoginButtonClick(ActionEvent event) throws IOException {
        welcomeText.setText("Succesvol ingelogd!");
//        System.out.println("test");
        System.out.println(text.getText());
        System.out.println(text1.getText());
        switchToDashboard(event);
    }


    public void switchToDashboard(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}