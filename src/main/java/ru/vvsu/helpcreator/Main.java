package ru.vvsu.helpcreator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.vvsu.helpcreator.controller.MainWindowController;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/main-window.fxml"));
        final Parent parent = fxmlLoader.load();
        final MainWindowController mainWindow = fxmlLoader.getController();
        mainWindow.setRootTreeView("new project 1");
        Scene scene = new Scene(parent, 800, 600);
        stage.setTitle("Help Creator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}