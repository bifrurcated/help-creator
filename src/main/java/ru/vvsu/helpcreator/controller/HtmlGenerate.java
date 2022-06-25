package ru.vvsu.helpcreator.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;
import ru.vvsu.helpcreator.Main;
import ru.vvsu.helpcreator.model.HtmlGenerateData;
import ru.vvsu.helpcreator.model.Page;
import ru.vvsu.helpcreator.model.Project;
import ru.vvsu.helpcreator.service.HtmlGenerateTask;
import ru.vvsu.helpcreator.service.TaskService;
import ru.vvsu.helpcreator.utils.FileHelper;
import ru.vvsu.helpcreator.utils.ViewWindow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static ru.vvsu.helpcreator.utils.ProjectPreferences.*;

public class HtmlGenerate implements Initializable{

    @FXML
    private  HBox hBoxGenerate;
    @FXML
    private TextField textFieldHtmlPath;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Button btnGenerate;
    @FXML
    private Button btnCancel;
    @FXML
    private CheckBox checkBoxOpenDoc;
    @FXML
    private CheckBox checkBoxOpenFolder;

    private Project project;
    private List<Page> pages;
    private MainWindow mainWindow;

    private final Validator validator = new Validator();
    private final TaskService service = new TaskService();

    public void setProject(Project project) {
        this.project = project;
        final HtmlGenerateData htmlGenerateData = project.getHtmlGenerateData();
        textFieldHtmlPath.setText(htmlGenerateData.getHtmlPath());
        checkBoxOpenDoc.setSelected(htmlGenerateData.isOpenDoc());
        checkBoxOpenFolder.setSelected(htmlGenerateData.isOpenFolder());
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void handleBtnGenerate(ActionEvent actionEvent) throws IOException {
        hBoxGenerate.setDisable(true);
        final ObservableList<TreeItem<Page>> treeItems = mainWindow.getRootItem().getChildren();
        if (treeItems.isEmpty()) {
            return;
        }
        mainWindow.generatePages(treeItems);
        Path path = Paths.get(textFieldHtmlPath.getText());
        //TODO: Как правильно удалять внутренние данные, поствить галку гля этого?
        //FileHelper.deleteDirectory(path.toFile());
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }

        String mainPath = Paths.get(PATH_TO_TEMPLATE).toString();
        FileHelper.copyDirectory(mainPath, path.toString());

        final String imagePath = project.getSettings().getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            FileHelper.copyFile(Paths.get(imagePath), Paths.get(path.toString(), PATH_TO_LOGO));
        }

        HtmlGenerateTask htmlGenerateTask = new HtmlGenerateTask(project, pages, path.toString());
        service.setTask(htmlGenerateTask);
        service.restart();
    }

    public void handleBtnCancel(ActionEvent actionEvent) {
        service.cancel();
        ViewWindow.closeWindow(actionEvent);
    }

    public void handleBtnOpenFolder(ActionEvent actionEvent) {
        Optional<String> directoryPathOptional = Optional.ofNullable(FileHelper.directoryChoicer(project.getPath()));
        directoryPathOptional.ifPresent(path -> textFieldHtmlPath.setText(path));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TooltipWrapper<Button> btnGenerateWrapper = new TooltipWrapper<>(
                btnGenerate,
                validator.containsErrorsProperty(),
                Bindings.concat("Невозможно начать генерацию:\n", validator.createStringBinding())
        );
        validator.createCheck()
                .withMethod(c -> {
                    if (textFieldHtmlPath.getText().isEmpty() || !Files.isDirectory(Paths.get(textFieldHtmlPath.getText()).getParent())) {
                        c.error("Директория не указана или неверна.");
                    }
                })
                .dependsOn("htmlPath", textFieldHtmlPath.textProperty())
                .decorates(textFieldHtmlPath)
                .decorates(btnGenerate)
                .immediate();
        hBoxGenerate.setAlignment(Pos.BOTTOM_RIGHT);
        hBoxGenerate.getChildren().add(btnGenerateWrapper);

        service.setOnSucceeded(event -> {
            final HtmlGenerateData htmlGenerateData = project.getHtmlGenerateData();
            if (!(textFieldHtmlPath.getText().equals(htmlGenerateData.getHtmlPath()) &&
                    checkBoxOpenDoc.isSelected() == htmlGenerateData.isOpenDoc() &&
                    checkBoxOpenFolder.isSelected() == htmlGenerateData.isOpenFolder()))
            {
                htmlGenerateData.setHtmlPath(textFieldHtmlPath.getText());
                htmlGenerateData.setOpenDoc(checkBoxOpenDoc.isSelected());
                htmlGenerateData.setOpenFolder(checkBoxOpenFolder.isSelected());
                FileHelper.serialize(project, project.getPath() + File.separator + PROJECT_SETTING_NAME);
            }
            hBoxGenerate.setDisable(false);
            if (checkBoxOpenDoc.isSelected()) {
                Main.hostServices.showDocument(project.getHtmlGenerateData().getHtmlPath() + File.separator + MAIN_PAGE_NAME + HTML_SUFFIX);
            }
            if (checkBoxOpenFolder.isSelected()) {
                Main.hostServices.showDocument(project.getHtmlGenerateData().getHtmlPath());
            }
        });

        progressBar.progressProperty().bind(service.progressProperty().add(1L));
        progressIndicator.progressProperty().bind(service.progressProperty().add(1L));
    }
}
