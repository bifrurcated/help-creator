package ru.vvsu.helpcreator.controller;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;
import ru.vvsu.helpcreator.model.Project;
import ru.vvsu.helpcreator.utils.FileHelper;
import ru.vvsu.helpcreator.utils.ProjectPreferences;
import ru.vvsu.helpcreator.utils.ViewWindow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static ru.vvsu.helpcreator.utils.ProjectPreferences.PATTERN_PROJECT_NAME;
import static ru.vvsu.helpcreator.utils.ProjectPreferences.PROJECT_SETTING_NAME;

public class SettingsProject implements Initializable {

    @FXML
    private TextField textFieldProductName;
    @FXML
    private TextField textFieldProductVersion;
    @FXML
    private TextField textFieldTypeDocumentation;
    @FXML
    private TextField textFieldCompanyName;
    @FXML
    private TextField textFieldProjectName;
    @FXML
    private HBox hBoxOk;
    @FXML
    private TextField textFieldYear;
    @FXML
    private Button btnOk;
    @FXML
    private ImageView imageViewProjectIcon;
    @FXML
    private TextField textFieldImagePath;
    @FXML
    private TextField textFieldProjectPath;

    private Project project;
    private Preferences preferences;
    private MainWindow mainWindow;

    private final Validator validator = new Validator();

    public void setProject(Project project) {
        this.project = project;
        if (!project.getImagePath().isEmpty()) {
            textFieldImagePath.setText(project.getImagePath());
            final Image image = new Image("file:/" + project.getImagePath(), 128D, 80D, true, false);
            imageViewProjectIcon.setImage(image);
        }
        if (project.getName() != null && !project.getName().isEmpty()) {
            textFieldProjectName.setText(project.getName());
        }
        if (project.getPath() != null && !project.getPath().isEmpty()) {
            textFieldProjectPath.setText(project.getPath());
        }
        if (project.getProductName() != null && !project.getProductName().isEmpty()) {
            textFieldProductName.setText(project.getProductName());
        }
        if (project.getProductVersion() != null && !project.getProductVersion().isEmpty()) {
            textFieldProductVersion.setText(project.getProductVersion());
        }
        if (project.getTypeDoc() != null && !project.getTypeDoc().isEmpty()) {
            textFieldTypeDocumentation.setText(project.getTypeDoc());
        }
        if (project.getCompanyName() != null && !project.getCompanyName().isEmpty()) {
            textFieldCompanyName.setText(project.getCompanyName());
        }
        if (project.getYear() != null && !project.getYear().isEmpty()) {
            textFieldYear.setText(project.getYear());
        }
    }

    public Project getProject() {
        return project;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        preferences = Preferences.userNodeForPackage(Project.class);
        TooltipWrapper<Button> successWrapper = new TooltipWrapper<>(
                btnOk,
                validator.containsErrorsProperty(),
                Bindings.concat("Невозможно сохранить проект:\n", validator.createStringBinding())
        );

        validator.createCheck()
                .withMethod(c -> {
                    if (!PATTERN_PROJECT_NAME.matcher(c.get("projectName")).matches()) {
                        c.error("Имя проекта не может пустым или начинаться с цифры.");
                    }
                })
                .dependsOn("projectName", textFieldProjectName.textProperty())
                .decorates(textFieldProjectName)
                .decorates(btnOk)
                .immediate();
        validator.createCheck()
                .withMethod(c -> {
                    if (textFieldProjectPath.getText().isEmpty() || !Files.isDirectory(Paths.get(textFieldProjectPath.getText()))) {
                        c.error("Директория проекта не указана или неверна.");
                    }
                })
                .dependsOn("projectPath", textFieldProjectPath.textProperty())
                .decorates(textFieldProjectPath)
                .decorates(btnOk)
                .immediate();
        hBoxOk.setAlignment(Pos.BOTTOM_RIGHT);
        hBoxOk.getChildren().add(successWrapper);
    }

    public void handleBtnSetImage(ActionEvent actionEvent) {
        Optional<String> imagePathOptional = Optional.ofNullable(FileHelper.imageChoicer());
        imagePathOptional.ifPresent(path -> {
            textFieldImagePath.setText(path);
            final Image image = new Image("file:/" + path, 128D, 80D, true, false);
            imageViewProjectIcon.setImage(image);
        });
    }

    public void handleBtnOpenFolder(ActionEvent actionEvent) {
        Optional<String> directoryPathOptional = Optional.ofNullable(FileHelper.directoryChoicer());
        directoryPathOptional.ifPresent(path -> textFieldProjectPath.setText(path));
    }

    public void handleBtnOk(ActionEvent actionEvent) throws IOException {
        ProjectPreferences.putProjectPathIfAbsent(preferences, textFieldProjectPath.getText());
        final Date time = Calendar.getInstance().getTime();
        String projectCreateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(time);
        project.setDate(projectCreateTime);
        project.setImagePath(textFieldImagePath.getText());
        project.setName(textFieldProjectName.getText());
        project.setPath(textFieldProjectPath.getText());
        project.setProductName(textFieldProductName.getText());
        project.setProductVersion(textFieldProductVersion.getText());
        project.setTypeDoc(textFieldTypeDocumentation.getText());
        project.setCompanyName(textFieldCompanyName.getText());
        project.setYear(textFieldYear.getText());
        final Path path = Paths.get(textFieldProjectPath.getText());
        if (!Files.exists(path)){
            Files.createDirectory(path);
        }
        FileHelper.serialize(project, textFieldProjectPath.getText()+ File.separator+PROJECT_SETTING_NAME);
        mainWindow.getRootItem().getValue().setName(project.getName());
        mainWindow.refreshTreeView();
        mainWindow.handleMenuItemSave();
        ViewWindow.closeWindow(actionEvent);
    }

    public void handleBtnCancel(ActionEvent actionEvent) {
        ViewWindow.closeWindow(actionEvent);
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }
}
