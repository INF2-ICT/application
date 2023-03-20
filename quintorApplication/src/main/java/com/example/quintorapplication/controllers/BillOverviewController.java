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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
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
            new Accounting("NL69INGB0123456789EUR", LocalDate.of(2023, 7, 7), Balance.CREDIT, 35.00, "bekijk"),
            new Accounting("NL69INGB0123456789EUR", LocalDate.of(2023, 7, 7), Balance.CREDIT, 35.00, "bekijk")
    );

    private Stage stage;

    public BillOverviewController() throws IOException {
            getAllTransactions();
    }

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

    public void getAllTransactions() throws IOException {
        URL url = new URL("http://localhost:8080/get-all-transactions");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty ("apikey", "test123");
        con.setRequestProperty("Content-Type", "application/json");
        con.connect();

        if (con != null) {

            System.out.println("hallo");
            int responseCode = con.getResponseCode();

            System.out.println("responsecode is " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("hallo");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                StringBuffer response = new StringBuffer();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println(response);

//                return response.toString();
            }
        }

        con.disconnect();
    }
}
