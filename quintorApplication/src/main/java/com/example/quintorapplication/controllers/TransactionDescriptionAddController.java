package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.models.SingleTransactionModel;
import com.example.quintorapplication.util.DatabaseUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.HashMap;

public class TransactionDescriptionAddController {
    public static int transactionId;
    public static String description;
    private Stage stage;
    private final ModeController modeController;
    @FXML
    public TextArea descriptionField;
    @FXML
    public Label feedbackLabel;
    @FXML
    private ChoiceBox<String> modeChoiceBox;
    @FXML
    private void initialize() throws Exception {
        ObservableList<String> modeList = FXCollections.observableArrayList("JSON", "XML");
        modeChoiceBox.setItems(modeList);
        modeChoiceBox.setValue("JSON");
        modeChoiceBox.setOnAction(e -> {
            try {
                this.setMode(e); //Give event, set different mode
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });

        if (!description.equals("Geen beschrijving")) {
            this.descriptionField.setText(description);
        }
    }

    public TransactionDescriptionAddController() {
        this.modeController = new ModeController();
    }

    public void addDescription(MouseEvent event) throws Exception {
        if (!descriptionField.getText().isEmpty()) {
            DatabaseUtil DB = new DatabaseUtil();

            try {
                HashMap<String, String> queryParams = new HashMap<>();

                if (this.modeController.getMode().toLowerCase().equals("json")) {
                    JsonObject jsonParams = new JsonObject();
                    jsonParams.addProperty("description", this.descriptionField.getText());

                    queryParams.put("description", String.valueOf(jsonParams));
                } else {
                    String xmlParams = "<singletransaction>" + "<description>" + this.descriptionField.getText() +"</description>" + "</singletransaction>";

                    queryParams.put("description", xmlParams);
                }

                String response = DB.putApiRequest("transaction/" + this.modeController.getMode().toLowerCase() + "/" + transactionId, queryParams);

                if (response.equals("true")) {
                    SingleBillOverviewController.transactionId = transactionId;

                    FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("singlebilloverview/singlebilloverview-view.fxml"));
                    stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(fxmlLoader.load());
                    stage.setScene(scene);
                    stage.show();
                } else {
                    this.feedbackLabel.setText("Er is iets fout gegaan.");
                }
            } catch (Exception e) {
                this.feedbackLabel.setText("Controleer of het veld goed is ingevuld.");
            }
        } else {
            this.feedbackLabel.setText("Veld mag niet leeg zijn.");
        }
    }

    private void setMode(ActionEvent actionEvent) {
        this.modeController.setMode(this.modeChoiceBox.getSelectionModel().getSelectedItem());
    }

    public void switchToDashboard(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
