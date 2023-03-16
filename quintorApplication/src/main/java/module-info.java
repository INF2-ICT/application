module com.example.quintorapplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.prowidesoftware.core;

    opens com.example.quintorapplication to javafx.fxml;
    exports com.example.quintorapplication;
    exports com.example.quintorapplication.controllers;
    opens com.example.quintorapplication.controllers to javafx.fxml;
    exports com.example.quintorapplication.functions;
    opens com.example.quintorapplication.functions to javafx.base;
}
