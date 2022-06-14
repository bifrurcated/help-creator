package ru.vvsu.helpcreator.controller;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;
import ru.vvsu.helpcreator.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class NewProject implements Initializable {

    @FXML private HBox hBox;
    @FXML private Button btnOk;
    @FXML private ImageView imageViewProjectIcon;
    @FXML private TextField textFieldProjectPath;
    @FXML private TextField textFieldProjectName;
    @FXML private TextField textFieldImagePath;

    private final Validator validator = new Validator();
    private final Pattern pattern = Pattern.compile("[a-zA-Zа-яА-Я][a-zA-Zа-яА-Я0-9\\s]{0,31}$");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textFieldProjectName.setText("Новый проект");
        TooltipWrapper<Button> signUpWrapper = new TooltipWrapper<>(
                btnOk,
                validator.containsErrorsProperty(),
                Bindings.concat("Нельзя:\n", validator.createStringBinding())
        );
        validator.createCheck()
                .withMethod(c -> {
                    if (!pattern.matcher(c.get("projectName")).matches()) {
                        c.error("Имя проекта не может пустым или начинаться с цифры.");
                    }
                })
                .dependsOn("projectName", textFieldProjectName.textProperty())
                .decorates(textFieldProjectName)
                .decorates(btnOk)
                .immediate();
        validator.createCheck()
                .withMethod(c -> {
                    if (textFieldProjectPath.getText().isEmpty() || !(new File(textFieldProjectPath.getText()).isDirectory())) {
                        c.error("Директория проекта не указана или неверна.");
                    }
                })
                .dependsOn("projectPath", textFieldProjectPath.textProperty())
                .decorates(textFieldProjectPath)
                .decorates(btnOk)
                .immediate();
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(signUpWrapper);
    }

    public void handleBtnOpenFolder(ActionEvent actionEvent) {
        Optional<String> directoryPathOptional = Optional.ofNullable(directoryChoicer());
        directoryPathOptional.ifPresent(path -> textFieldProjectPath.setText(path));
    }

    public void handleBtnOk(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/main-window.fxml"));
        final Parent parent = fxmlLoader.load();
        final MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setRootTreeView(textFieldProjectName.getText());
        Scene scene = new Scene(parent, 800, 600);
        stage.setTitle("Help Creator");
        stage.setScene(scene);
        stage.show();
        Node node = (Node) actionEvent.getSource();
        Stage thisStage = (Stage) node.getScene().getWindow();
        Stage owner = (Stage) thisStage.getOwner().getScene().getWindow();
        thisStage.close();
        owner.close();
    }

    public void handleBtnCancel(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        Stage thisStage = (Stage) node.getScene().getWindow();
        thisStage.close();
    }

    public void handleBtnSetImage(ActionEvent actionEvent) {
        Optional<String> imagePathOptional = Optional.ofNullable(imageChoicer());
        imagePathOptional.ifPresent(path -> {
            textFieldImagePath.setText(path);
            final Image image = new Image("file:/" + path, 128D, 80D, true, false);
            imageViewProjectIcon.setImage(image);
        });
    }

    private String directoryChoicer() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выбрать директорию проекта");
        final String path = new File("").getAbsolutePath();

        directoryChooser.setInitialDirectory(new File(path));
        File selectedDirectory = directoryChooser.showDialog(null);
        if(selectedDirectory != null){
            return selectedDirectory.getAbsolutePath();
        }
        return null;
    }

    private String imageChoicer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбрать изображение проекта");
        final String path = new File("").getAbsolutePath();
        fileChooser.setInitialDirectory(new File(path));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("IMAGE", "*.png", "*.jpg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("All file", "*.*")
        );
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
}
