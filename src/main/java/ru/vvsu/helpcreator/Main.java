package ru.vvsu.helpcreator;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;
import ru.vvsu.helpcreator.utils.ProjectPreferences;
import ru.vvsu.helpcreator.utils.ViewWindow;

import java.io.IOException;

public class Main extends Application {

    public static HostServices hostServices;

    @Override
    public void start(Stage stage) throws IOException {
        hostServices = getHostServices();
        ProjectPreferences.initializePaths();
        ViewWindow.openProjectCreate(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}