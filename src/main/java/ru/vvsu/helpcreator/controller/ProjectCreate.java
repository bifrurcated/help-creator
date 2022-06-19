package ru.vvsu.helpcreator.controller;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.vvsu.helpcreator.Main;
import ru.vvsu.helpcreator.model.Project;
import ru.vvsu.helpcreator.utils.FileHelper;
import ru.vvsu.helpcreator.utils.ProjectPreferences;
import ru.vvsu.helpcreator.utils.ViewWindow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static ru.vvsu.helpcreator.utils.ProjectPreferences.ARTIFACT_ID;
import static ru.vvsu.helpcreator.utils.ProjectPreferences.PROJECT_SETTING_NAME;

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

        listViewProjects.setCellFactory(lv -> {
            TextFieldListCell<Project> cell = new TextFieldListCell<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteMenu = new MenuItem("Удалить проект");
            deleteMenu.setOnAction((event) -> {
                final Project project = cell.getItem();
                listViewProjects.getItems().remove(project);
                try {
                    for (var key: preferences.keys()) {
                        final String val = preferences.get(key, "");
                        if (!val.isEmpty()) {
                            if (val.equals(project.getPath())) {
                                preferences.remove(key);
                                final int anInt = preferences.getInt(ARTIFACT_ID, 0);
                                preferences.putInt(ARTIFACT_ID, anInt-1);
                                break;
                            }
                        }
                    }
                } catch (BackingStoreException e) {
                    e.printStackTrace();
                }
            });
            contextMenu.getItems().addAll(deleteMenu);
            cell.setContextMenu(contextMenu);
            return cell;
        });


        FilteredList<Project> filteredData = new FilteredList<>(listViewProjects.getItems(), project -> true);

        textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(project ->{
            if(newValue == null || newValue.isEmpty()){
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            return project.getName().toLowerCase().contains(lowerCaseFilter);
        }));

        SortedList<Project> sortedData = new SortedList<>(filteredData);
        sortedData.setComparator((o1, o2) -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            try {
                final Date date1 = dateFormat.parse(o1.getDate());
                final Date date2 = dateFormat.parse(o2.getDate());
                return Long.compare(date2.getTime(), date1.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 1;
        });
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


    public void handleListViewMouseClicked(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() != 2) return;
        final Project selectedItem = listViewProjects.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            saveEditTimeProject(selectedItem, selectedItem.getPath() + File.separator + PROJECT_SETTING_NAME);
            ViewWindow.openMainWindow(selectedItem);
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
