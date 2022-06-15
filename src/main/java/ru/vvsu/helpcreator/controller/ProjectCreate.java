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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;
import ru.vvsu.helpcreator.Main;
import ru.vvsu.helpcreator.model.Project;
import ru.vvsu.helpcreator.utils.FileHelper;
import ru.vvsu.helpcreator.utils.ProjectSettings;
import ru.vvsu.helpcreator.utils.ViewWindow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.prefs.Preferences;

import static ru.vvsu.helpcreator.utils.ProjectSettings.ARTIFACT_ID;
import static ru.vvsu.helpcreator.utils.ProjectSettings.PROJECT_SETTING_NAME;

public class ProjectCreate implements Initializable {

    @FXML
    private TextField textFieldSearch;
    @FXML
    private ListView<Project> listViewProjects;

    private Preferences preferences;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listViewProjects.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("css/list-cell.css")).toExternalForm());
        preferences = Preferences.userNodeForPackage(Project.class);
        final int projectCount = preferences.getInt(ARTIFACT_ID, 0);
        if (projectCount > 0) {
            System.out.println("projectCount: "+projectCount);
            for (int i = 1; i <= projectCount; i++) {
                final String projectPath = preferences.get(ARTIFACT_ID + i, "");
                System.out.println("projectPath: "+projectPath);
                if (projectPath.isEmpty()) {
                    preferences.remove(ARTIFACT_ID + i);
                    return;
                }
                if (Files.isDirectory(Paths.get(projectPath))) {
                    System.out.println("isDirectory: true");
                    final File file = new File(projectPath + File.separator + PROJECT_SETTING_NAME);
                    if (file.isFile()) {
                        Optional.ofNullable((Project) FileHelper.deserialize(file.getAbsolutePath()))
                                .ifPresent(project -> listViewProjects.getItems().add(project));
                    }
                }
            }
        }
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
        Optional.ofNullable(projectChoicer())
                .ifPresent(projectPath -> Optional.ofNullable((Project) FileHelper.deserialize(projectPath))
                        .ifPresent(project -> {
                            ProjectSettings.putProjectPathIfAbsent(preferences, Paths.get(projectPath).getParent().toString());
                            saveEditTimeProject(project, projectPath);
                            try {
                                ViewWindow.openMainWindow(project.getName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ViewWindow.closeWindow(actionEvent);
                        }));
    }


    public void handleListViewMouseClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() != 2) return;
        final Project selectedItem = listViewProjects.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            saveEditTimeProject(selectedItem, selectedItem.getPath() + File.separator + PROJECT_SETTING_NAME);
            ViewWindow.openMainWindow(selectedItem.getName());
            ViewWindow.closeWindow(mouseEvent);
        }
    }

    private String projectChoicer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбрать проект");
        final String path = new File("").getAbsolutePath();
        fileChooser.setInitialDirectory(new File(path));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PROJECT", "*.settings"),
                new FileChooser.ExtensionFilter("All file", "*.*")
        );
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }
        return null;
    }

    private void saveEditTimeProject(Project project, String projectPath) {
        final Date time = Calendar.getInstance().getTime();
        String projectCreateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(time);
        project.setDate(projectCreateTime);
        FileHelper.serialize(project, projectPath);
    }
}
