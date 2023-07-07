package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.enums.TransactionType;
import com.example.quintorapplication.models.TransactionModel;

import com.example.quintorapplication.util.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import javafx.util.Callback;

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
    public TextField transactionSearch;
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
        TransactionsData.setItems(getTransactions()); //Get all transactions on first startup

        //make a FilteredList from the TransactionModel
        FilteredList<TransactionModel> filteredData = new FilteredList<>(getTransactions(), b -> true);

        //check on the text property of the transactionSearch
        transactionSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(transactionModel -> {

                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }//small check for if the transactionSearch is empty

                String searchKeyWord = newValue.toLowerCase();

                if (transactionModel.getTransaction_reference().toLowerCase().indexOf(searchKeyWord) > -1) {
                    return true;
                } else if (transactionModel.getTransactionType().toString().toLowerCase().indexOf(searchKeyWord) > -1) {
                    return true;
                } else if (transactionModel.getValue_date().toString().toLowerCase().indexOf(searchKeyWord) > -1) {
                    return true;
                } else {
                    return false;
                } //check on different types of searchable data and check whether they are typed in or not
            });
        });

        //make a SortedList to check the filteredData
        SortedList<TransactionModel> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(TransactionsData.comparatorProperty());

        TransactionsData.setItems(sortedData); //then bind and compare the two lists and set the new items in the table

        // Create a custom cell factory for the column
        Callback<TableColumn<TransactionModel, Integer>, TableCell<TransactionModel, Integer>> cellFactory = column -> {
            final TableCell<TransactionModel, Integer> cell = new TableCell<TransactionModel, Integer>() {
                @Override
                protected void updateItem(Integer itemId, boolean empty) {
                    super.updateItem(itemId, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        // Create a button for the cell
                        Button button = new Button("Bekijk");
                        button.setOnAction(event -> {
                            // Handle button click event
                            TransactionModel transaction = getTableView().getItems().get(getIndex());
                            int transactionId = transaction.getId();

                            // Perform actions based on the transactionId
                            Stage stage;
                            SingleBillOverviewController.transactionId = transactionId;
                            FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("singlebilloverview/singlebilloverview-view.fxml"));
                            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                            Scene scene = null;
                            try {
                                scene = new Scene(fxmlLoader.load());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            stage.setScene(scene);
                            stage.show();
                        });
                        setGraphic(button);
                    }
                }
            };
            return cell;
        };

        // Set the cell factory for the column
        transactionId.setCellFactory(cellFactory);


        //transactionSearch.setDisable(true);//Set search box disabled, as it's not working
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
        if (this.modeController.getMode().toLowerCase().equals("json")) { //JSON
            String receivedData = DB.getApiRequest(this.modeController.getAllTransactionsEndpoint());
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
            String receivedData =  DB.getApiRequest(this.modeController.getAllTransactionsEndpoint());

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
