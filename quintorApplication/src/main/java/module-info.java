module com.example.quintorapplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.prowidesoftware.core;
    requires java.net.http;
    requires java.sql;
    requires org.json;
    requires com.google.gson;

    opens com.example.quintorapplication.models to javafx.base;
    opens com.example.quintorapplication to javafx.fxml;
    exports com.example.quintorapplication;
    exports com.example.quintorapplication.controllers;
    opens com.example.quintorapplication.controllers to javafx.fxml;
    exports com.example.quintorapplication.models;
    exports com.example.quintorapplication.enums;
}
