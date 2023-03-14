package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.prowidesoftware.swift.model.SwiftTagListBlock;
import com.prowidesoftware.swift.model.Tag;
import com.prowidesoftware.swift.model.field.Field20;
import com.prowidesoftware.swift.model.field.Field25;
import com.prowidesoftware.swift.model.field.Field60F;
import com.prowidesoftware.swift.model.field.Field62F;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Mt940FileAddController {

    @FXML
    public Button uploadFile;
    private Stage stage;
    private ArrayList<Boolean> checkList;
    public Mt940FileAddController() {
        this.checkList = new ArrayList<>();
    }
    private void addCheckList(boolean correct) {
        this.checkList.add(correct);
    }

    private void emptyCheckList() {
        this.checkList.clear();
    }

    private ArrayList<Boolean> getCheckList() {
        return this.checkList;
    }
    public void switchToDashboard(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StarterApplication.class.getResource("dashboard/dashboard-view.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public File uploadTheFile(ActionEvent event) throws IOException {

        FileChooser fileChooser = new FileChooser();
        File response = fileChooser.showOpenDialog(null);

        boolean fileChecked = false;


        if (response != null) {

            Scanner sc = new Scanner(response);

            while (sc.hasNextLine()) {
                if (sc.nextLine().length() > 0) {
                    fileChecked = true;
                }
            }

            if (fileChecked) {
                String text = Files.readString(Paths.get(response.toURI()));
                System.out.println(validateMT940(text));
            }
        }

        return null;
    }

    private boolean validateMT940 (String file) {

        MT940 mt = new MT940(file);
        System.out.println("test");

        if (mt.getSwiftMessage().getBlock4() != null) {
            System.out.println("got through?");
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

        System.out.println(getCheckList());

        return true;
    }
    private void CheckField20(Tag t) {
        if (t.getName().equals("20")) {
            Field20 tx = (Field20) t.asField();
            System.out.println(tx);
            addCheckList(true);
        }
    }
    private void CheckField25(Tag t) {
        if (t.getName().equals("25")) {
            Field25 tx = (Field25) t.asField();
            System.out.println(tx);
            addCheckList(true);
        }
    }
    private void CheckField60F(Tag t) {
        if (t.getName().equals("60F")) {
            Field60F tx = (Field60F) t.asField();
            System.out.println(tx);
            addCheckList(true);
        }
    }
    private void CheckField62F(Tag t) {
        if (t.getName().equals("62F")) {
            Field62F tx = (Field62F) t.asField();
            System.out.println(tx);
            addCheckList(true);
        }
    }

}
