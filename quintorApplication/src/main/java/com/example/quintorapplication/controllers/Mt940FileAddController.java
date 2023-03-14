package com.example.quintorapplication.controllers;

import com.example.quintorapplication.StarterApplication;
import com.prowidesoftware.swift.model.SwiftTagListBlock;
import com.prowidesoftware.swift.model.Tag;
import com.prowidesoftware.swift.model.field.Field20;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.prowidesoftware.swift.model.mt.mt9xx.MT940;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Mt940FileAddController {

    @FXML
    public Button uploadFile;
    private Stage stage;
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

        if (response != null) {
            Scanner scanner = new Scanner(response);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
//                if (!validateMT940(line)) {
//                    System.out.println(validateMT940(line));
                System.out.println(validateMT940(line));
//                    return null;
//                } else {
//                    return response;
//                }
            }

            scanner.close();
        }

        return null;
    }

    private boolean validateMT940 (String file) throws IOException {

//        System.out.println("hier");
//
//        System.out.println(file);

        MT940 mt = new MT940(file);

        System.out.println(mt);

        if (mt.getSwiftMessage().getBlock4() != null) {
            Tag start = mt.getSwiftMessage().getBlock4().getTagByNumber(20);
            Tag end = mt.getSwiftMessage().getBlock4().getTagByNumber(62);

            SwiftTagListBlock loop = mt.getSwiftMessage().getBlock4().getSubBlock(start, end);

            for (int i = 0; i < loop.size(); i++) {
                Tag t = loop.getTag(i);

                if (t.getName().equals("20")) {
                    Field20 tx = (Field20) t.asField();

                    System.out.println(tx);
                }
            }
        }

        return false;
    }

}
