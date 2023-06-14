package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.example.quintorapplication.util.DatabaseUtil;

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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;


public class Mt940FileAddController {
    private File file;
    @FXML
    private Text feedbackText;
    @FXML
    private Button fileAddButton;
    private final ModeController modeController;
    @FXML
    private ChoiceBox<String> modeChoiceBox;

    @FXML
    private void initialize() {
        ObservableList<String> modeList = FXCollections.observableArrayList("JSON", "XML");
        modeChoiceBox.setItems(modeList);
        modeChoiceBox.setValue("JSON");
        modeChoiceBox.setOnAction(this::setMode);
    }

    private void setMode(ActionEvent actionEvent) {
        this.modeController.setMode(this.modeChoiceBox.getSelectionModel().getSelectedItem());
    }

    public Mt940FileAddController() {
        this.file = null;
        this.modeController = new ModeController();
    }

    /**
     * Function to set file to null and change text back to regular(cancel the upload)
     */
    public void cancelUpload() {
        this.file = null;
        this.fileAddButton.setText("MT940 bestand toevoegen");
    }

    /**
     * Event handler for uploading a file.
     * @param event the ActionEvent representing the upload event
     */
    public void uploadTheFile(ActionEvent event) {
        // Create a FileChooser object to allow the user to select a file
        FileChooser fileChooser = new FileChooser();

        // Show the file chooser dialog and wait for the user to select a file
        this.file = fileChooser.showOpenDialog((Stage) ((Node) event.getSource()).getScene().getWindow());

        if (this.file != null) {
            // Update the text of the fileAddButton to display the selected file name
            this.fileAddButton.setText(this.file.getName());
        }
    }

    /**
     * Function that uploads the uploaded file to databases and runs file through validator
     *
     * @throws IOException
     */
    public void upload() throws Exception {
        if (this.file != null) {
            if (checkIfFileHasContent()) {
                System.out.println("I read the data and it is correct.");

                //Create new database util object
                DatabaseUtil DB = new DatabaseUtil();
                String parserOutput = DB.uploadMT940FileToParser(this.file, this.modeController.getParserEndpoint());

                System.out.println(parserOutput);
            }
        }

//        boolean fileChecked = false;
//        if (response != null) {
//            //Scanner to read lines from file
//            Scanner sc = new Scanner(response);
//            while (sc.hasNextLine()) {
//                if (sc.nextLine().length() > 0) {
//                    fileChecked = true;
//                }
//            }
//
//            //If there is content and read out all lines
//            if (fileChecked) {
//                String mt940Text = Files.readString(Paths.get(response.toURI()));
//
//                //Create new database util object
//                DatabaseUtil DB = new DatabaseUtil();
//
//                //Send file to Parser based on set mode XML/JSON
//                String parserOutput = DB.uploadMT940FileToParser(this.file, this.modeController.getParserEndpoint());
//
//                //Send received XML/JSON to API to validate it in schemas and mariaDB
//                HashMap<String, String> headerBody = new HashMap<>(); //Header - Body
//                headerBody.put(this.modeController.getMode().toLowerCase(), parserOutput); //For example "json", "{test: "test"}"
//                String ApiOutput = DB.postApiRequest(this.modeController.getMT940Endpoint(), headerBody);
//
//                if (!ApiOutput.equals("Success")) {
//                    this.feedbackText.setText("Het bestand is geen valide MT940 bestand!");
//                } else {
//                    HashMap<String, String> raw = new HashMap<>();
//                    raw.put("MT940File", mt940Text);
//                    String rawOutput = DB.postApiRequest("post-raw", raw);
//
//                    if (!ApiOutput.equals("Success") && !rawOutput.equals("Success")) {
//                        this.feedbackText.setText("Het bestand is geen valide MT940 bestand!");
//                    } else {
//                        this.feedbackText.setText("Bestand succesvol toegevoegd!");
//                    }
//                }
//            } else {
//                this.feedbackText.setText("Bestand is geen valide MT940 bestand!");
//            }
//            cancelUpload(); //Set uploaded file to null
//        } else {
//            this.feedbackText.setText("Geen bestand geupload!");
//        }
    }

    /**
     * Checks if the file has any content
     * @return true if the file has content, false otherwise
     * @throws FileNotFoundException if the file does not exist
     */
    public boolean checkIfFileHasContent() throws FileNotFoundException {
        try {
            // Scanner to read lines from file
            Scanner sc = new Scanner(this.file);
            while (sc.hasNextLine()) {
                if (sc.nextLine().length() > 0) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }

        return false;
    }


    /**
     * Function to switch scene to dashboard
     * @param event
     * @throws IOException
     */
    public void switchToDashboard(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }
}
