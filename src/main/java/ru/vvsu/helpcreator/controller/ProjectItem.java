package ru.vvsu.helpcreator.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ProjectItem {

    @FXML
    private BorderPane borderPaneImage;
    @FXML
    private ImageView imageViewProjectIcon;
    @FXML
    private Text textProjectName;
    @FXML
    private Text textProjectPath;
    @FXML
    private Text textProjectDate;

    public void setTextProjectName(String projectName) {
        textProjectName.setText(projectName);
    }

    public void setTextProjectPath(String projectPath) {
        textProjectPath.setText(projectPath);
    }

    public void setTextProjectDate(String projectDate) {
        textProjectDate.setText(projectDate);
    }

    public void setImageViewProjectIcon(String imagePath) {
        final Image image = new Image("file:/" + imagePath, 48D, 30D, true, false);
        imageViewProjectIcon.setImage(image);
    }

    public void defaultImageViewProjectIcon(){
        Canvas canvas = new Canvas(48, 30);
        GraphicsContext context2D = canvas.getGraphicsContext2D();
        context2D.setLineWidth(1.0);
        context2D.setFill(Color.gray(0.5D,0.5D));
        context2D.fillRoundRect(2,0,48,30,5,5);
        borderPaneImage.setCenter(canvas);
    }
}
