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
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static ru.vvsu.helpcreator.utils.ProjectPreferences.PATTERN_PROJECT_NAME;
import static ru.vvsu.helpcreator.utils.ProjectPreferences.PROJECT_SETTING_NAME;

public class NewProject implements Initializable {

    @FXML private HBox hBox;
    @FXML private Button btnOk;
    @FXML private ImageView imageViewProjectIcon;
    @FXML private TextField textFieldProjectPath;
    @FXML private TextField textFieldProjectName;
    @FXML private TextField textFieldImagePath;

    private final Validator validator = new Validator();

    private Preferences preferences;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        preferences = Preferences.userNodeForPackage(Project.class);
        textFieldProjectName.setText("Новый проект");
        TooltipWrapper<Button> successWrapper = new TooltipWrapper<>(
                btnOk,
                validator.containsErrorsProperty(),
                Bindings.concat("Невозможно создать проект:\n", validator.createStringBinding())
        );
        validator.createCheck()
                .withMethod(c -> {
                    if (!PATTERN_PROJECT_NAME.matcher(c.get("projectName")).matches()) {
                        c.error("Имя проекта не может пустым, начинаться с цифры \n" +
                                "или превышать размер в 52 символа.");
                    }
                })
                .dependsOn("projectName", textFieldProjectName.textProperty())
                .decorates(textFieldProjectName)
                .decorates(btnOk)
                .immediate();
        validator.createCheck()
                .withMethod(c -> {
                    if (textFieldProjectPath.getText().isEmpty() ||
                            !Files.isDirectory(Paths.get(textFieldProjectPath.getText()))) {
                        c.error("Директория проекта не указана или неверна.");
                    }
                })
                .dependsOn("projectPath", textFieldProjectPath.textProperty())
                .decorates(textFieldProjectPath)
                .decorates(btnOk)
                .immediate();
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(successWrapper);
    }

    public void handleBtnOpenFolder(ActionEvent actionEvent) {
        Optional<String> directoryPathOptional = Optional.ofNullable(FileHelper.directoryChoicer(""));
        directoryPathOptional.ifPresent(path -> textFieldProjectPath.setText(path));
    }

    public void handleBtnOk(ActionEvent actionEvent) throws IOException {
        ProjectPreferences.putProjectPathIfAbsent(preferences, textFieldProjectPath.getText());
        final long time = Calendar.getInstance().getTimeInMillis();
        System.out.println("ProjectPath: "+textFieldProjectPath.getText());
        Project project = new Project(textFieldProjectName.getText(), time, textFieldProjectPath.getText(), textFieldImagePath.getText());
        final Path path = Paths.get(textFieldProjectPath.getText());
        if (!Files.exists(path)){
            Files.createDirectory(path);
        }
        FileHelper.serialize(project, textFieldProjectPath.getText()+File.separator+PROJECT_SETTING_NAME);

        ViewWindow.openMainWindow(project);
        ViewWindow.closeWindowWithOwner(actionEvent);
    }

    public void handleBtnCancel(ActionEvent actionEvent) {
        ViewWindow.closeWindow(actionEvent);
    }

    public void handleBtnSetImage(ActionEvent actionEvent) {
        Optional<String> imagePathOptional = Optional.ofNullable(FileHelper.imageChoicer());
        imagePathOptional.ifPresent(path -> {
            textFieldImagePath.setText(path);
            final Image image = new Image("file:/" + path, 128D, 80D, true, false);
            imageViewProjectIcon.setImage(image);
        });
    }
}
