module ru.vvsu.helpcreator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens ru.vvsu.helpcreator to javafx.fxml;
    exports ru.vvsu.helpcreator;
    exports ru.vvsu.helpcreator.controller;
    opens ru.vvsu.helpcreator.controller to javafx.fxml;
}