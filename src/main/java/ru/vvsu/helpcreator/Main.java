package ru.vvsu.helpcreator;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.vvsu.helpcreator.model.Project;
import ru.vvsu.helpcreator.utils.ViewWindow;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        ViewWindow.openProjectCreate(stage);
    }

    public static void main(String[] args) {
        Preferences preferences = Preferences.userNodeForPackage(Project.class);
        try {
            preferences.clear();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
        launch();
    }
}