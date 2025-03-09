module com.hit.italkclientfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;

    opens com.hit.italkclientfx to javafx.fxml;
    opens com.hit.italkclientfx.controllers to javafx.fxml;
    opens com.hit.client to com.google.gson;
    opens com.hit.dm to com.google.gson;
    exports com.hit.italkclientfx;
    exports com.hit.italkclientfx.controllers;
    exports com.hit.client;
    exports com.hit.dm;
}