package com.example.quintorapplication.functions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.github.underscore.U;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ReadTransactionJsonXml {


    public ObservableList<Accounting> getAllTransactionJson() throws IOException {
        URL url = new URL("http://localhost:8081/get-all-transactions-json");

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("apikey", "test123");
        con.setRequestProperty("Content-Type", "application/xml");
        con.connect();

        if (con != null) {

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;

                ObservableList<Accounting> list = FXCollections.observableArrayList();

                while ((inputLine = in.readLine()) != null) {
                    JSONArray array = new JSONArray(inputLine.trim());

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = new JSONObject(array.get(i).toString());
                        list.add(new Accounting(
                                (String) obj.get("transaction_reference"),
                                (String) obj.get("value_date"),
                                (String) obj.get("transactionType"),
                                obj.get("amount_in_euro")+".00",
                                "bekijk"));
                    }
                }

                in.close();
                return list;
            }
        }

        con.disconnect();
        return null;
    }

    public List<String> getAllTransactionXml() throws IOException, ParserConfigurationException, SAXException {
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
                    JSONObject obj = new JSONObject(inputLine.substring(1, inputLine.length() - 1));
                    xml = U.jsonToXml(String.valueOf(obj));
                    response.add(xml);
                }

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(response.get(0))));
                Element rootElement = document.getDocumentElement();

                System.out.println(getXmlNodeValue("transactionType", rootElement));
                System.out.println(response);

                in.close();
                return response;
            }
        }

        con.disconnect();
        return null;
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

}
