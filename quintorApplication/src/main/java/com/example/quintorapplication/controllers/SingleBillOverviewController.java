package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.enums.TransactionType;
import com.example.quintorapplication.models.SingleTransactionModel;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class SingleBillOverviewController {
    private Stage stage;
    public static int transactionId;
    private final ModeController modeController;
    private SingleTransactionModel singleTransactionModel;
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
    private Label transactionReference;
    @FXML
    private void initialize() throws Exception {
        ObservableList<String> modeList = FXCollections.observableArrayList("JSON", "XML");
        modeChoiceBox.setItems(modeList);
        modeChoiceBox.setValue("JSON");
        modeChoiceBox.setOnAction(e -> {
            try {
                this.setMode(e); //Give event, set different mode
                transactionData.setItems(getTransaction(transactionId)); //Get all transactions based on mode
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });

        amountInEuro.setCellValueFactory(new PropertyValueFactory<>("amount_in_euro"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        valueDate.setCellValueFactory(new PropertyValueFactory<>("value_date"));
        creditDebit.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        identificationCode.setCellValueFactory(new PropertyValueFactory<>("identificationCode"));
        referentialOwner.setCellValueFactory(new PropertyValueFactory<>("ownerReferential"));
        transactionData.setItems(getTransaction(transactionId)); //Get all transactions on first startup
    }

    public SingleBillOverviewController() {
        this.modeController = new ModeController();
    }

    /**
     * Function to get all transactions based on set application mode, JSON or XML
     * @return ObservableList with TransactionModels
     * @throws Exception
     */
    public ObservableList<SingleTransactionModel> getTransaction(int transactionId) throws Exception {
        transactionData.setItems(null);

        DatabaseUtil DB = new DatabaseUtil(); //Create new database util object
        ObservableList<SingleTransactionModel> transactions = FXCollections.observableArrayList(); //List with transaction model that will be sent back

        //Send received XML/JSON to API to validate it in schemas and mariaDB
        if (this.modeController.getMode().toLowerCase().equals("json")) { //JSON
            String receivedData = DB.getApiRequest("transaction/" + this.modeController.getMode().toLowerCase() + "/" + transactionId);

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
                    //Get id - for deleting transaction
                    String identification_code = jsonObject.getString("identification_code");
                    String transactionReference = jsonObject.getString("transaction_reference"); //put this to label
                    TransactionType transactionType = TransactionType.valueOf(jsonObject.getString("transaction_type"));
                    double amountInEuro = jsonObject.getDouble("amount_in_euro");
                    String owner_referential = jsonObject.getString("owner_referential");
                    LocalDate valueDate = LocalDate.parse(jsonObject.getString("value_date"));
                    String description = jsonObject.getString("description");

                    setTransactionReference(transactionReference);

                    SingleTransactionModel transactionModel = new SingleTransactionModel(amountInEuro, description, valueDate, transactionType, identification_code, owner_referential);
                    this.singleTransactionModel = transactionModel;
                    transactions.add(transactionModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            String receivedData = DB.getApiRequest("transaction/" + this.modeController.getMode().toLowerCase() + "/" + transactionId);

            //Get XML objects
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + receivedData; //Set XML mode to XML string

            //Read XML object
            try {
                //Create document builder of XML string
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
                doc.getDocumentElement().normalize();

                //Get contents of XML object
                //Get id - for deleting transaction
                String transaction_reference = doc.getElementsByTagName("transaction_reference").item(0).getTextContent();
                String description = doc.getElementsByTagName("description").item(0).getTextContent();
                LocalDate valueDate = LocalDate.parse(doc.getElementsByTagName("value_date").item(0).getTextContent());
                TransactionType transactionType = TransactionType.valueOf(doc.getElementsByTagName("transaction_type").item(0).getTextContent());
                double amountInEuro = Double.parseDouble(doc.getElementsByTagName("amount_in_euro").item(0).getTextContent());
                String identification_code = doc.getElementsByTagName("identification_code").item(0).getTextContent();
                String owner_referential = doc.getElementsByTagName("owner_referential").item(0).getTextContent();

                setTransactionReference(transaction_reference);

                SingleTransactionModel transactionModel = new SingleTransactionModel(amountInEuro, description, valueDate, transactionType, identification_code, owner_referential);
                this.singleTransactionModel = transactionModel;
                transactions.add(transactionModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Return list of all transactions
        return transactions;
    }

    private void setMode(ActionEvent actionEvent) {
        this.modeController.setMode(this.modeChoiceBox.getSelectionModel().getSelectedItem());
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference.setText(transactionReference);
    }

    public void switchToDashboard(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public void deleteTransaction(MouseEvent event) throws IOException {
        DatabaseUtil DB = new DatabaseUtil();

        HashMap<String, String> headerBody = new HashMap<>();
        String ApiOutput = DB.deleteApiRequest("transaction/" + transactionId, headerBody);

        if (ApiOutput.equals("true")) {
            FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();
        }
    }

    public void addDescription(MouseEvent event) throws IOException {
        TransactionDescriptionAddController.transactionId = transactionId;
        TransactionDescriptionAddController.description = singleTransactionModel.getDescription();

        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("transactionDescriptionAdd/transactionDescriptionAdd-view.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
