package ru.vvsu.helpcreator.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import ru.vvsu.helpcreator.Main;
import ru.vvsu.helpcreator.model.Project;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

public class ProjectCreate implements Initializable {

    @FXML
    private TextField textFieldSearch;
    @FXML
    private ListView<Project> listViewProjects;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final Date time = Calendar.getInstance().getTime();
        time.setTime(10000);
        String timeStamp = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(time);
        String path = new File("").getAbsolutePath();
        Project project = new Project("My Project 1", timeStamp, path);
        listViewProjects.getItems().add(project);
    }

    public void handleBtnNewProject(ActionEvent actionEvent) throws IOException {
        Node node = (Node) actionEvent.getSource();
        Stage projectStage = (Stage) node.getScene().getWindow();
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

    public void handleBtnOpen(ActionEvent actionEvent) {
    }


    public void handleListViewMouseClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() != 2) return;
        final Project selectedItem = listViewProjects.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/main-window.fxml"));
            final Parent parent = fxmlLoader.load();
            final MainWindow mainWindow = fxmlLoader.getController();
            mainWindow.setRootTreeView(selectedItem.getName());
            Scene scene = new Scene(parent, 800, 600);
            stage.setTitle("Help Creator");
            stage.setScene(scene);
            stage.show();
            final Stage projectStage = (Stage) listViewProjects.getScene().getWindow();
            projectStage.close();
        }
    }
}
