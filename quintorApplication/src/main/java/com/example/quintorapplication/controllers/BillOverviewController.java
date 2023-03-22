package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.enums.Balance;
import com.example.quintorapplication.functions.Accounting;
import com.prowidesoftware.JsonSerializable;
import org.json.JSONObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

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

        try {
            tableView.setItems(getAllTransactions());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ObservableList<Accounting> getAllTransactions() throws IOException {
        URL url = new URL("http://localhost:8081/get-all-transactions");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("apikey", "test123");
        con.setRequestProperty("Content-Type", "application/json");
        con.connect();

        if (con != null) {

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                List<JSONObject> response = new ArrayList<>();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    JSONObject obj = new JSONObject(inputLine.substring(1, inputLine.length()-1));
                    response.add(obj);
                }

                ObservableList<Accounting> list = FXCollections.observableArrayList(
                        new Accounting(
                                (String) response.get(0).get("transaction_reference"),
                                (String) response.get(0).get("value_date"),
                                (String) response.get(0).get("transactionType"),
                                (BigDecimal) response.get(0).get("amount_in_euro"),
                                "bekijk")
                );

                in.close();

                return list;
            }
        }

        con.disconnect();



        return null;
    }
}
