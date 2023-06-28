package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.enums.TransactionType;
import com.example.quintorapplication.models.SingleTransactionModel;
import com.example.quintorapplication.models.TransactionModel;
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

public class SingleBillOverviewController {
    private Stage stage;
    public static int transactionId;
    private final ModeController modeController;
    @FXML
    public TableView<SingleTransactionModel> transactionData;
    @FXML
    public TableColumn<SingleTransactionModel, Double> amountInEuro;
    @FXML
    public TableColumn<SingleTransactionModel, String> description;
    @FXML
    public TableColumn<SingleTransactionModel, LocalDate> valueDate;
    @FXML
    public TableColumn<SingleTransactionModel, TransactionType> creditDebit;
    @FXML
    public TableColumn<SingleTransactionModel, String> identificationCode;
    @FXML
    public TableColumn<SingleTransactionModel, String> referentialOwner;

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
                //TransactionsData.setItems(this.getTransactions()); //Get all transactions based on mode
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });

        amountInEuro.setCellValueFactory(new PropertyValueFactory<>("amount_in_euro"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        valueDate.setCellValueFactory(new PropertyValueFactory<>("value_date"));
        creditDebit.setCellValueFactory(new PropertyValueFactory<>("credit_debit"));
        identificationCode.setCellValueFactory(new PropertyValueFactory<>("identification_code"));
        referentialOwner.setCellValueFactory(new PropertyValueFactory<>("referential_owner"));

        System.out.println(transactionId);
    }

    public SingleBillOverviewController() {
        this.modeController = new ModeController();
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
