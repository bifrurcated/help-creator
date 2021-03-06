package ru.vvsu.helpcreator.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.vvsu.helpcreator.Main;
import ru.vvsu.helpcreator.controller.control.ProjectItemCell;
import ru.vvsu.helpcreator.model.Project;
import ru.vvsu.helpcreator.utils.FileHelper;
import ru.vvsu.helpcreator.utils.ProjectPreferences;
import ru.vvsu.helpcreator.utils.ViewWindow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static ru.vvsu.helpcreator.utils.ProjectPreferences.ARTIFACT_ID;
import static ru.vvsu.helpcreator.utils.ProjectPreferences.PROJECT_SETTING_NAME;

public class ProjectCreate implements Initializable {

    @FXML
    private TextField textFieldSearch;
    @FXML
    private ListView<Project> listViewProjects;

    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private Preferences preferences;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listViewProjects.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("css/list-cell.css")).toExternalForm());
        preferences = Preferences.userNodeForPackage(Project.class);
        int index = 0;
        final int projectCount = preferences.getInt(ARTIFACT_ID, 0);
        if (projectCount > 0) {
            for (int i = 1; i <= projectCount; i++) {
                final String projectPath = preferences.get(ARTIFACT_ID + i, "");
                if (index > 0) {
                    preferences.put(ARTIFACT_ID + (i - index), projectPath);
                    preferences.remove(ARTIFACT_ID + i);
                }
                if (projectPath.isEmpty()) {
                    preferences.remove(ARTIFACT_ID + i);
                    index++;
                    preferences.putInt(ARTIFACT_ID, projectCount-index);
                    continue;
                }
                if (Files.isDirectory(Paths.get(projectPath))) {
                    final File file = new File(projectPath + File.separator + PROJECT_SETTING_NAME);
                    if (file.isFile()) {
                        Optional.ofNullable((Project) FileHelper.deserialize(file.getAbsolutePath()))
                                .ifPresent(projects::add);
                    }
                }
            }
        }

        listViewProjects.setCellFactory(lv -> {
            ListCell<Project> cell = new ProjectItemCell();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem openProject = new MenuItem("??????????????");
            MenuItem deleteMenu = new MenuItem("?????????????? ????????????");
            openProject.setOnAction(actionEvent -> openSelectProject());
            deleteMenu.setOnAction((event) -> {
                final Project project = cell.getItem();
                projects.remove(project);
                try {
                    boolean changeNext = false;
                    final String[] keys = preferences.keys();
                    for (int i = 1; i < keys.length; i++) {
                        final String val = preferences.get(keys[i], "");
                        if (!val.isEmpty()) {
                            if (changeNext) {
                                preferences.put(ARTIFACT_ID + (i-1), val);
                                preferences.remove(keys[i]);
                            }
                            if (val.equals(project.getPath())) {
                                preferences.remove(keys[i]);
                                changeNext = true;
                                final int anInt = preferences.getInt(ARTIFACT_ID, 0);
                                preferences.putInt(ARTIFACT_ID, anInt-1);
                            }
                        }
                    }
                } catch (BackingStoreException e) {
                    e.printStackTrace();
                }
            });
            contextMenu.getItems().addAll(openProject, deleteMenu);
            cell.setContextMenu(contextMenu);
            return cell;
        });


        FilteredList<Project> filteredData = new FilteredList<>(projects, project -> true);

        textFieldSearch.textProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(project -> {
                    if(newValue == null || newValue.isEmpty()){
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return project.getName().toLowerCase().contains(lowerCaseFilter);
                }));

        SortedList<Project> sortedData = new SortedList<>(filteredData);
        sortedData.setComparator((o1, o2) -> Long.compare(o2.getDate(), o1.getDate()));
        listViewProjects.setItems(sortedData);
    }

    public void handleBtnNewProject(ActionEvent actionEvent) throws IOException {
        Node node = (Node) actionEvent.getSource();
        Stage projectStage = (Stage) node.getScene().getWindow();
        ViewWindow.openNewProject(projectStage);
    }

    public void handleBtnOpen(ActionEvent actionEvent) {
        Optional.ofNullable(projectChoicer())
                .ifPresent(projectPath -> Optional.ofNullable((Project) FileHelper.deserialize(projectPath))
                        .ifPresent(project -> {
                            ProjectPreferences.putProjectPathIfAbsent(preferences, Paths.get(projectPath).getParent().toString());
                            saveEditTimeProject(project, projectPath);
                            try {
                                ViewWindow.openMainWindow(project);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ViewWindow.closeWindow(actionEvent);
                        }));
    }


    public void handleListViewMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() != 2) return;
        openSelectProject();
    }

    private void openSelectProject() {
        final Project selectedItem = listViewProjects.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            saveEditTimeProject(selectedItem, selectedItem.getPath() + File.separator + PROJECT_SETTING_NAME);
            try {
                ViewWindow.openMainWindow(selectedItem);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            ViewWindow.closeWindow(listViewProjects.getParent());
        }
    }

    private String projectChoicer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("?????????????? ????????????");
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
        final long time = Calendar.getInstance().getTimeInMillis();
        project.setDate(time);
        FileHelper.serialize(project, projectPath);
    }
}
