package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.enums.TransactionType;
import com.example.quintorapplication.models.KasgeldModel;
import com.example.quintorapplication.util.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
public class MoneyDrawerController {
    private Stage stage;
    private final ModeController modeController;
    @FXML
    private ChoiceBox<String> modeChoiceBox;
    @FXML
    private TextField amountField;
    @FXML
    private TextField referentionField;
    @FXML
    private DatePicker dateField;
    @FXML
    private ChoiceBox<TransactionType> transactiontypeField;
    @FXML
    private TextField descriptionField;
    @FXML
    private Label feedbackLabel;

    @FXML
    public void initialize() {
        dateField.setEditable(false);

        TextFormatter<Double> textFormatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, c ->
                c.getControlNewText().matches("-?\\d*(\\.\\d*)?") ? c : null);
        amountField.setTextFormatter(textFormatter);

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

        ObservableList<TransactionType> transactionTypes = FXCollections.observableArrayList(TransactionType.C, TransactionType.D);
        transactiontypeField.setItems(transactionTypes);
        transactiontypeField.setValue(TransactionType.C);
    }

    public MoneyDrawerController() {
        this.modeController = new ModeController();
    }

    public void addMoney(ActionEvent event) {
        boolean incorrect = amountField.getText().isEmpty() || referentionField.getText().isEmpty() || dateField.getValue() == null || transactiontypeField.getValue() == null;

        if (!incorrect) {
            double amount = Double.parseDouble(amountField.getText());
            String referention = referentionField.getText();
            LocalDate date = dateField.getValue();
            TransactionType type = transactiontypeField.getValue();
            String description = descriptionField.getText();

            boolean output = false;

            try {
                KasgeldModel kasgeldModel = new KasgeldModel(amount, referention, date, type, description);
                DatabaseUtil DB = new DatabaseUtil();

                if (this.modeController.getMode().toLowerCase().equals("json")) {
                    HashMap<String, String> headerBody = new HashMap<>();
                    headerBody.put("transactionData", kasgeldModel.convertToJson());

                    String ApiOutput = DB.postApiRequest("transaction/" + this.modeController.getMode().toLowerCase(), headerBody);

                    output = Boolean.parseBoolean(ApiOutput);
                } else {
                    HashMap<String, String> headerBody = new HashMap<>();
                    headerBody.put("transactionData", kasgeldModel.convertToXml());

                    String ApiOutput = DB.postApiRequest("transaction/" + this.modeController.getMode().toLowerCase(), headerBody);

                    output = Boolean.parseBoolean(ApiOutput);
                }

                if (output) {
                    feedbackLabel.setText("Transactie toegevoegd.");
                    this.amountField.setText("0.0");
                    this.referentionField.setText("");
                    this.dateField.setValue(null);
                    this.transactiontypeField.setValue(TransactionType.C);
                    this.descriptionField.setText("");
                } else {
                    feedbackLabel.setText("Er is een fout opgetreden met het verwerken van de transactie.");
                }
            } catch (Exception e) {
                feedbackLabel.setText("Er is iets fout gegaan met het invoeren van de transactie.");
            }
        } else {
            feedbackLabel.setText("Een of meerdere velden zijn incorrect ingevoerd.");
        }
    }

    public void clearFields(ActionEvent event) {
        this.amountField.setText("0.0");
        this.referentionField.setText("");
        this.dateField.setValue(null);
        this.transactiontypeField.setValue(TransactionType.C);
        this.descriptionField.setText("");
        this.feedbackLabel.setText("");
    }

    private void setMode(ActionEvent actionEvent) {
        this.modeController.setMode(this.modeChoiceBox.getSelectionModel().getSelectedItem());
    }

    public void switchToDashboard(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
