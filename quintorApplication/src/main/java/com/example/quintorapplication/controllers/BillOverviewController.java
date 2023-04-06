package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.enums.TransactionType;
import com.example.quintorapplication.models.TransactionModel;

import com.example.quintorapplication.util.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class BillOverviewController {
    private final ModeController modeController;
    @FXML
    public TableView<TransactionModel> TransactionsData;
    @FXML
    public TableColumn<TransactionModel, String> transactionReference;
    @FXML
    public TableColumn<TransactionModel, LocalDate> valueDate;
    @FXML
    public TableColumn<TransactionModel, TransactionType> transactionType;
    @FXML
    public TableColumn<TransactionModel, Double> amountInEuro;
    @FXML
    public TableColumn<TransactionModel, Integer> transactionId;
    @FXML
    private ChoiceBox<String> modeChoiceBox;
    @FXML
    private void initialize() throws Exception {
        ObservableList<String> modeList = FXCollections.observableArrayList("JSON", "XML");
        modeChoiceBox.setItems(modeList);
        modeChoiceBox.setValue("JSON");
        modeChoiceBox.setOnAction(this::setMode);

        transactionReference.setCellValueFactory(new PropertyValueFactory<>("transaction_reference"));
        valueDate.setCellValueFactory(new PropertyValueFactory<>("value_date"));
        transactionType.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        amountInEuro.setCellValueFactory(new PropertyValueFactory<>("amount_in_euro"));
        transactionId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TransactionsData.setItems(getTransactions());
    }

    private void setMode(ActionEvent actionEvent) {
        this.modeController.setMode(this.modeChoiceBox.getSelectionModel().getSelectedItem());
    }

    public BillOverviewController() throws Exception {
        this.modeController = new ModeController();
    }

    public ObservableList<TransactionModel> getTransactions() throws Exception {
        DatabaseUtil DB = new DatabaseUtil(); //Create new database util object
        ObservableList<TransactionModel> transactions = FXCollections.observableArrayList(); //List with all transaction models that will be sent back

        //Send received XML/JSON to API to validate it in schemas and mariaDB
        HashMap<String, String> headerBody = new HashMap<>(); //Header - Body
        headerBody.put("mode", this.modeController.getMode().toLowerCase());

        if (this.modeController.getMode().toLowerCase().equals("json")) {
            String receivedData = DB.getApiRequest("get-all-transactions-json", headerBody);
            JSONArray jsonArray = new JSONArray(receivedData);
            ArrayList<JSONObject> jsonList = new ArrayList<>();

//            System.out.println("Recieved data = " + receivedData);

            //Loop over JSRONArray to add objects to ArrayList
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                jsonList.add(jsonObj);
            }

            //Loop over arraylist, get each individual jsonObject and make TransactionModel out of this
            for (JSONObject jsonObject : jsonList) {
//                System.out.println(jsonObject.toString());
                int id = jsonObject.getInt("id");
                String transactionReference = jsonObject.getString("transaction_reference");
                LocalDate valueDate = LocalDate.parse(jsonObject.getString("value_date"));
                TransactionType transactionType = TransactionType.valueOf(jsonObject.getString("transaction_type"));
                double amountInEuro = jsonObject.getDouble("amount_in_euro");

                transactions.add(new TransactionModel(id, transactionReference, valueDate, transactionType, amountInEuro));
            }
        } else {
            String recievedData =  DB.getApiRequest("get-all-transactions-xml", headerBody);
            System.out.println("data recieved in xml");
        }

        return transactions;
    }

    public void switchToDashboard(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
