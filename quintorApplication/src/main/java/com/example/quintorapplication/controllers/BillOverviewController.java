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
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
        modeChoiceBox.setOnAction(e -> {
            try {
                this.setMode(e); //Give event, set different mode
                TransactionsData.setItems(this.getTransactions()); //Get all transactions based on mode
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });

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

    public BillOverviewController() {
        this.modeController = new ModeController();
    }

    /**
     * Function to get all transactions based on set application mode, JSON or XML
     * @return ObservableList with TransactionModels
     * @throws Exception
     */
    public ObservableList<TransactionModel> getTransactions() throws Exception {
        TransactionsData.setItems(null);

        DatabaseUtil DB = new DatabaseUtil(); //Create new database util object
        ObservableList<TransactionModel> transactions = FXCollections.observableArrayList(); //List with all transaction models that will be sent back

        //Send received XML/JSON to API to validate it in schemas and mariaDB
        HashMap<String, String> headerBody = new HashMap<>(); //Header - Body
        headerBody.put("mode", this.modeController.getMode().toLowerCase());

        if (this.modeController.getMode().toLowerCase().equals("json")) { //JSON
            String receivedData = DB.getApiRequest(this.modeController.getAllTransactionsEndpoint(), headerBody);
            JSONArray jsonArray = new JSONArray(receivedData);
            ArrayList<JSONObject> jsonList = new ArrayList<>();

            //Loop over JSRONArray to add objects to ArrayList
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                jsonList.add(jsonObj);
            }

            //Get json objects
            for (JSONObject jsonObject : jsonList) {
                //Read json object
                try {
                    int id = jsonObject.getInt("id");
                    String transactionReference = jsonObject.getString("transaction_reference");
                    LocalDate valueDate = LocalDate.parse(jsonObject.getString("value_date"));
                    TransactionType transactionType = TransactionType.valueOf(jsonObject.getString("transaction_type"));
                    double amountInEuro = jsonObject.getDouble("amount_in_euro");

                    //Add contents to transaction array
                    transactions.add(new TransactionModel(id, transactionReference, valueDate, transactionType, amountInEuro));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else { //XML
            String receivedData =  DB.getApiRequest(this.modeController.getAllTransactionsEndpoint(), headerBody);

            //Edit received string
            receivedData = receivedData.substring(1, receivedData.length() - 1); //Remove characters [ & ]
            String[] xmlStrings = receivedData.split(","); //Split string on ,

            //Get XML objects
            for (String xml : xmlStrings) {
                xml = xml.substring(1, xml.length() - 1); //Remove characters " & "
                xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml; //Set XML mode to XML string

                //Read XML object
                try {
                    //Create document builder of XML string
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
                    doc.getDocumentElement().normalize();

                    //Get contents of XML object
                    int id = Integer.parseInt(doc.getElementsByTagName("id").item(0).getTextContent());
                    String transactionReference = doc.getElementsByTagName("transaction_reference").item(0).getTextContent();
                    LocalDate valueDate = LocalDate.parse(doc.getElementsByTagName("value_date").item(0).getTextContent());
                    TransactionType transactionType = TransactionType.valueOf(doc.getElementsByTagName("transaction_type").item(0).getTextContent());
                    double amountInEuro = Double.parseDouble(doc.getElementsByTagName("amount_in_euro").item(0).getTextContent());

                    //Add contents to transaction array
                    transactions.add(new TransactionModel(id, transactionReference, valueDate, transactionType, amountInEuro));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //Return list of all transactions
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
