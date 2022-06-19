module helpcreator {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.antdesignicons;
    requires org.kordamp.bootstrapfx.core;
    requires java.prefs;

    opens ru.vvsu.helpcreator to javafx.fxml;
    exports ru.vvsu.helpcreator;
    exports ru.vvsu.helpcreator.controller;
    exports ru.vvsu.helpcreator.model;
    opens ru.vvsu.helpcreator.controller to javafx.fxml;
}