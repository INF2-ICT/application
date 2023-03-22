package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.enums.Balance;
import com.example.quintorapplication.functions.Accounting;
//import com.example.quintorapplication.functions.ReadTransactionJsonXml;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;

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

    private Stage stage;
    public void switchToDashboard(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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

//        try {
//            tableView.setItems(getAllTransactionsJson());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }
}
