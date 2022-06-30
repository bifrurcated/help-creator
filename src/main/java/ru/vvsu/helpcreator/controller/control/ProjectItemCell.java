package ru.vvsu.helpcreator.controller.control;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import ru.vvsu.helpcreator.Main;
import ru.vvsu.helpcreator.controller.ProjectItem;
import ru.vvsu.helpcreator.model.Project;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class ProjectItemCell extends ListCell<Project> {
    @Override
    protected void updateItem(Project project, boolean empty) {
        super.updateItem(project, empty);
        if (!empty && project != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/project-item.fxml"));
            Node load = null;
            try {
                load = fxmlLoader.load();
                load.setStyle("-fx-border-color: black; -fx-border-width: 0px 0px 1px 0px");
            } catch (IOException e) {
                e.printStackTrace();
            }
            final ProjectItem projectItem = fxmlLoader.getController();
            projectItem.setTextProjectName(project.getName());
            projectItem.setTextProjectPath(project.getPath());
            String projectCreateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(project.getDate());
            projectItem.setTextProjectDate(projectCreateTime);
            final String imagePath = project.getSettings().getImagePath();
            if (imagePath == null || imagePath.isEmpty()) {
                projectItem.defaultImageViewProjectIcon();
            } else {
                projectItem.setImageViewProjectIcon(imagePath);
            }
            setGraphic(load);
        } else {
            setGraphic(null);
        }
    }
}
