package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.util.DatabaseUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class MoneyDrawerController {
    private Stage stage;

    public void addMoney(Double amount, String description) {
        DatabaseUtil DB = new DatabaseUtil();

        HashMap<String, String> map = new HashMap<>();
        map.put("amount", amount.toString());
        map.put("description", description);

        try {
            DB.postApiRequest("post-cash", map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void switchToDashboard(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
