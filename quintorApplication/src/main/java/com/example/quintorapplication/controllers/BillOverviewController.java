package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.enums.Balance;
import com.example.quintorapplication.functions.Accounting;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class BillOverviewController implements Initializable {

    @FXML
    private TableView<Accounting> tableView;
    @FXML
    private TableColumn<Accounting, String> accountNumberId;
    @FXML
    private TableColumn<Accounting, LocalDate> billingDate;
    @FXML
    private TableColumn<Accounting, Balance> balance;
    @FXML
    private TableColumn<Accounting, Double> moneyAmount;
    @FXML
    private TableColumn<Accounting, String> singleAccountData;

    ObservableList<Accounting> list = FXCollections.observableArrayList(
            new Accounting("NL69INGB0123456789EUR", LocalDate.of(2023, 7, 7), Balance.CREDIT, 35.00, "bekijk")
    );

    private Stage stage;

    public void switchToDashboard(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountNumberId.setCellValueFactory(new PropertyValueFactory<Accounting, String>("accountNumberId"));
        billingDate.setCellValueFactory(new PropertyValueFactory<Accounting, LocalDate>("billingDate"));
        balance.setCellValueFactory(new PropertyValueFactory<Accounting, Balance>("balance"));
        moneyAmount.setCellValueFactory(new PropertyValueFactory<Accounting, Double>("moneyAmount"));
        singleAccountData.setCellValueFactory(new PropertyValueFactory<Accounting, String>("singleAccountData"));

        tableView.setItems(list);
    }
}
