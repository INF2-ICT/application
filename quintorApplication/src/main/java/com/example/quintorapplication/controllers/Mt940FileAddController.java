package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.util.DatabaseUtil;

import com.prowidesoftware.swift.model.SwiftTagListBlock;
import com.prowidesoftware.swift.model.Tag;
import com.prowidesoftware.swift.model.field.Field20;
import com.prowidesoftware.swift.model.field.Field25;
import com.prowidesoftware.swift.model.field.Field60F;
import com.prowidesoftware.swift.model.field.Field62F;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Mt940FileAddController {
    private File file;
    private Stage stage;
    private ArrayList<Boolean> checkList;
    String mode; //Temp for now, can be XML/JSON must be gotten from application
    String parserEndpoint;
    String apiEndpoint;
    @FXML
    private Text feedbackText;
    @FXML
    private Button fileAddButton;
    @FXML
    private ChoiceBox<String> modeChoiceBox;
    @FXML
    private void initialize() {
        ObservableList<String> modeList = FXCollections.observableArrayList("JSON", "XML");
        modeChoiceBox.setItems(modeList);
        modeChoiceBox.setValue("JSON");
        modeChoiceBox.setOnAction(this::setMode);
    }

    /**
     * Constructor
     */
    public Mt940FileAddController() {
        this.file = null;
        this.checkList = new ArrayList<>();
        this.mode = "JSON";
        this.parserEndpoint = "MT940toJSON";
        this.apiEndpoint = "post-json";
    }

    /**
     * Function to set file to null and change text back to regular(cancel the upload)
     */
    public void cancelUpload() {
        this.file = null;
        this.fileAddButton.setText("MT940 bestand toevoegen");
    }

    /**
     * Sets uploaded file to field `file`
     * Also changes upload button name to uploaded file name
     * @param event
     */
    public void uploadTheFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        this.file = fileChooser.showOpenDialog(null);

        if (this.file != null) {
            this.fileAddButton.setText(this.file.getName());
        }
    }

    /**
     * Function that uploads the uploaded file to databases and runs file through validator
     * @throws IOException
     */
    public void upload() throws Exception {
        File response = this.file;
        boolean fileChecked = false;
        emptyCheckList();

        if (response != null) {
            //Scanner to read lines from file
            Scanner sc = new Scanner(response);
            while (sc.hasNextLine()) {
                if (sc.nextLine().length() > 0) {
                    fileChecked = true;
                }
            }

            //If there is content and read out all lines
            if (fileChecked) {
                String mt940Text = Files.readString(Paths.get(response.toURI()));

                //Validate MT940
                if (validateMT940(mt940Text)) {
                    //Create new database util object
                    DatabaseUtil DB = new DatabaseUtil();

                    //Send file to Parser based on set mode XML/JSON
                    String parserOutput = DB.uploadMT940FileToParser(this.file, this.parserEndpoint);

                    //Send received XML/JSON to API to validate it
                    HashMap<String, String> headerBody = new HashMap<>(); //Header - Body
                    headerBody.put(this.mode.toLowerCase(), parserOutput); //For example "json", "{test: "test"}"
                    String ApiOutput = DB.postApiRequest(this.apiEndpoint, headerBody);

                    //Send file to mariaDB
                    if (ApiOutput == "Success") {
                        // . . .
                    }

                    //Give successful feedback message
                    this.feedbackText.setText("Bestand succesvol toegevoegd!");
                }

            } else {
                this.feedbackText.setText("Bestand is geen valide MT940 bestand!");
            }

            cancelUpload(); //Set uploaded file to null
        } else {
            this.feedbackText.setText("Geen bestand geupload!");
        }

    }

    /**
     * Function to validate MT940 if it contains field 20, 25, 60F and 62F
     * @param file
     * @return
     */
    private boolean validateMT940 (String file) {
        MT940 mt = new MT940(file); //Needs extra validation to check if filetype is ok with MT940(json for example is not, and it gives an error)

        if (mt.getSwiftMessage().getBlock4() != null) {
            Tag start = mt.getSwiftMessage().getBlock4().getTagByNumber(20);
            Tag end = mt.getSwiftMessage().getBlock4().getTagByNumber(62);

            SwiftTagListBlock loop = mt.getSwiftMessage().getBlock4().getSubBlock(start, end);

            for (int i = 0; i < loop.size(); i++) {
                Tag t = loop.getTag(i);
                CheckField20(t);
                CheckField25(t);
                CheckField60F(t);
                CheckField62F(t);
            }
        }

        if (!getCheckList().isEmpty()) {
            return !getCheckList().contains(false);
        }

        return false;
    }

    /**
     *
     * @param correct
     */
    private void addCheckList(boolean correct) {
        this.checkList.add(correct);
    }

    /**
     *
     */
    private void emptyCheckList() {
        this.checkList.clear();
    }

    /**
     *
     * @return
     */
    private ArrayList<Boolean> getCheckList() {
        return this.checkList;
    }

    /**
     *
     * @param t
     */
    private void CheckField20(Tag t) {
        if (t.getName().equals("20")) {
            Field20 tx = (Field20) t.asField();
            if (tx.getComponent1().length() == 16) {
                addCheckList(true);
            } else {
                addCheckList(false);
            }
        }
    }

    /**
     *
     * @param t
     */
    private void CheckField25(Tag t) {
        if (t.getName().equals("25")) {
            Field25 tx = (Field25) t.asField();
            addCheckList(true);
        }
    }

    /**
     *
     * @param t
     */
    private void CheckField60F(Tag t) {
        if (t.getName().equals("60F")) {
            Field60F tx = (Field60F) t.asField();
            addCheckList(true);
        }
    }

    /**
     *
     * @param t
     */
    private void CheckField62F(Tag t) {
        if (t.getName().equals("62F")) {
            Field62F tx = (Field62F) t.asField();
            addCheckList(true);
        }
    }

    /**
     * Function to set mode of application. JSON or XML. Standard = JSON
     * @param actionEvent
     */
    private void setMode(ActionEvent actionEvent) {
        //Set variables based on application mode

        switch (this.mode) {
            case "JSON" -> {
                this.parserEndpoint = "MT940toJSON";
                this.apiEndpoint = "post-json";
            }
            case "XML" -> {
                this.parserEndpoint = "MT940toXML";
                this.apiEndpoint = "post-xml";
            }
        }
    }

    /**
     * Function to switch scene to dashboard (navbar)
     * @param event
     * @throws IOException
     */
    public void switchToDashboard(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
