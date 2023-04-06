module com.example.quintorapplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.prowidesoftware.core;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires json.schema.validator;
    requires java.sql;
    requires org.json;

    opens com.example.quintorapplication.models to javafx.base;
    opens com.example.quintorapplication to javafx.fxml;

    exports com.example.quintorapplication;
    exports com.example.quintorapplication.controllers;
    opens com.example.quintorapplication.controllers to javafx.fxml;
}
