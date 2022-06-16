package ru.vvsu.helpcreator.utils;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import ru.vvsu.helpcreator.Main;
import ru.vvsu.helpcreator.controller.MainWindow;
import ru.vvsu.helpcreator.model.Project;

import java.io.IOException;

public class ViewWindow {

    public static void openProjectCreate(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/project-create.fxml"));
        final Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 800, 600);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setTitle("Help Creator");
        stage.setScene(scene);
        stage.show();
    }

    public static void openMainWindow(Project project) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/main-window.fxml"));
        final Parent parent = fxmlLoader.load();
        final MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setRootTreeView(project);
        Scene scene = new Scene(parent, 800, 600);
        stage.setTitle("Help Creator");
        stage.setScene(scene);
        stage.show();
    }

    public static void openNewProject(Stage projectStage) throws IOException {
        Stage newProject = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/new-project.fxml"));
        final Parent parent = fxmlLoader.load();
        Scene scene = new Scene(parent, 600, 275);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        newProject.setTitle("Новый проект");
        newProject.setScene(scene);
        newProject.initModality(Modality.WINDOW_MODAL);
        newProject.initOwner(projectStage);
        newProject.show();
    }

    public static void closeWindow(Event event) {
        Node node = (Node) event.getSource();
        Stage thisStage = (Stage) node.getScene().getWindow();
        thisStage.close();
    }

    public static void closeWindow(Node node) {
        Stage thisStage = (Stage) node.getScene().getWindow();
        thisStage.close();
    }

    public static void closeWindowWithOwner(Event event) {
        Node node = (Node) event.getSource();
        Stage thisStage = (Stage) node.getScene().getWindow();
        Stage owner = (Stage) thisStage.getOwner().getScene().getWindow();
        thisStage.close();
        owner.close();
    }
}
