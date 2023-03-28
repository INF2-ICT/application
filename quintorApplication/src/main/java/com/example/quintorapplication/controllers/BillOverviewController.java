package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.enums.Balance;
import com.example.quintorapplication.functions.Accounting;
import com.github.underscore.U;
import com.github.underscore.Xml;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
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

    public BillOverviewController() throws IOException, ParserConfigurationException, SAXException {
        getAllTransactionXml();
    }

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

    public void getAllTransactionXml() throws IOException, ParserConfigurationException, SAXException {
        URL url = new URL("http://localhost:8081/get-all-transactions-xml");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("apikey", "test123");
        con.setRequestProperty("Content-Type", "application/xml");
        con.connect();

        if (con != null) {

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                List<String> response = new ArrayList<>();
                String inputLine;

                String xml;

//                ObservableList<Accounting> list = FXCollections.observableArrayList();

                while ((inputLine = in.readLine()) != null) {
                    for (String document : inputLine.split(",")) {
//                        response.add(document);
                        System.out.println(Arrays.toString(document.split("\"")));
                    }
//                    xml = U.toXml(inputLine);
//                    System.out.println(xml);
//                    System.out.println(inputLine.trim());
//                    xml = U.xmlOrJsonToXml(inputLine.substring(1, inputLine.length() - 1));
//                    System.out.println(xml);
//                    response.add(convertStringToXml(inputLine.substring(1, inputLine.length() - 1)));
                }

//                System.out.println(response);
//
//                String test = response.get(0).substring(1, response.get(0).length() - 1);
//
//                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                DocumentBuilder builder = factory.newDocumentBuilder();
//                Document document = builder.parse(response.get(0));
//                System.out.println(document);
//                Element rootElement = document.getDocumentElement();
//                System.out.println(getXmlNodeValue("transactionType", rootElement));
//                    response.add(xml);

                in.close();
//                return response;
            }
        }

        con.disconnect();
//        return null;
    }

    private static Document convertStringToXml(String xmlString) {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder builder = dbf.newDocumentBuilder();

            return builder.parse(new InputSource(new StringReader(xmlString)));

        } catch (IOException | SAXException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

    }

    private String getXmlNodeValue(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }

//    public ObservableList<Accounting> getAllTransactionJson() throws IOException, ParserConfigurationException, SAXException {
//        URL url = new URL("http://localhost:8081/get-all-transactions-json");
//
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
//        con.setRequestProperty("apikey", "test123");
//        con.setRequestProperty("Content-Type", "application/json");
//        con.connect();
//
//        if (con != null) {
//
//            int responseCode = con.getResponseCode();
//
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//
//                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//                List<String> response = new ArrayList<>();
//                String inputLine;
//                String xml;
//
//                ObservableList<Accounting> list = FXCollections.observableArrayList();
//
//                while ((inputLine = in.readLine()) != null) {
////                    JSONObject obj = new JSONObject(inputLine.substring(1, inputLine.length() - 1));
////                    xml = U.jsonToXml(String.valueOf(obj));
//                    JSONArray array = new JSONArray(inputLine.trim());
//
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject obj = new JSONObject(array.get(i).toString());
//                        xml = U.jsonToXml(String.valueOf(obj));
//                        response.add(xml);
//                    }
//
//                }
//
//                System.out.println(response);
//
//                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                DocumentBuilder builder = factory.newDocumentBuilder();
//                Document document = builder.parse(new InputSource(new StringReader(response.get(0))));
//                System.out.println(document);
//                Element rootElement = document.getDocumentElement();
//
//                in.close();
//                return list;
//            }
//        }
//
//        con.disconnect();
//        return null;
//    }

}
