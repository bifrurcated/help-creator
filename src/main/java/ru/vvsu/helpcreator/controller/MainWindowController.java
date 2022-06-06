package ru.vvsu.helpcreator.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.HTMLEditor;
import org.kordamp.ikonli.javafx.FontIcon;
import ru.vvsu.helpcreator.model.ContextMenuTreeCell;
import ru.vvsu.helpcreator.model.Page;
import ru.vvsu.helpcreator.utils.DefaultValues;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    private TreeView<Page> treeView;
    @FXML
    private HTMLEditor htmlEditor;

    private final ImageView defaultImageView;
    private TreeItem<Page> previousPageSelected;

    public MainWindowController() {
        final String pathToImg = Objects.requireNonNull(getClass().getResource("/ru/vvsu/helpcreator/images/ListView32.png")).toExternalForm();
        final Image image = new Image(pathToImg, 16D, 16D, true, false);
        defaultImageView = new ImageView(image);
    }

    public void setRootTreeView(String projectName) {
        FontIcon icon = new FontIcon("anto-file-text");
        icon.setIconSize(8);
        final Page page1 = new Page(projectName, String.format(DefaultValues.HTMLPAGE, projectName));
        final Page page2 = new Page("page 1", String.format(DefaultValues.HTMLPAGE, "page 1"));
        final Page page3 = new Page("page 2", String.format(DefaultValues.HTMLPAGE, "page 2"));
        TreeItem<Page> rootItem = new TreeItem<>(page1, icon);
        rootItem.setExpanded(true);
        TreeItem<Page> treeItem1 = new TreeItem<>(page2, icon);
        TreeItem<Page> treeItem2 = new TreeItem<>(page3, icon);
        rootItem.getChildren().add(treeItem1);
        rootItem.getChildren().add(treeItem2);

        treeView.setRoot(rootItem);
        htmlEditor.setHtmlText(rootItem.getValue().getHtml());
        treeView.getSelectionModel().selectFirst();
        previousPageSelected = treeView.getSelectionModel().getSelectedItem();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        treeView.setEditable(true);

        MenuItem addPage = new MenuItem("Add page");
        MenuItem addSubPage = new MenuItem("Add subpage");
        ContextMenu contextMenu2 = new ContextMenu(addPage, addSubPage);
        treeView.setCellFactory(ContextMenuTreeCell.forTreeView(contextMenu2));

        addPage.setOnAction(actionEvent -> {
            Page newPage = new Page("new Page", String.format(DefaultValues.HTMLPAGE, "new Page"));
            TreeItem<Page> newTreeItem = new TreeItem<>(newPage, defaultImageView);
            final TreeItem<Page> parent = treeView.getSelectionModel().getSelectedItem().getParent();
            if (parent != null) {
                int index = parent
                        .getChildren()
                        .indexOf(treeView.getSelectionModel().getSelectedItem()) + 1;
                parent.getChildren().add(index, newTreeItem);
            } else {
                final TreeItem<Page> root = treeView.getRoot();
                int index = root
                        .getChildren()
                        .indexOf(treeView.getSelectionModel().getSelectedItem()) + 1;
                root.getChildren().add(index, newTreeItem);
            }
        });
        addSubPage.setOnAction(actionEvent -> {
            Page newPage = new Page("new Subpage", String.format(DefaultValues.HTMLPAGE, "new Subpage"));
            TreeItem<Page> newTreeItem = new TreeItem<>(newPage, defaultImageView);
            final TreeItem<Page> selectedItem = treeView.getSelectionModel().getSelectedItem();
            selectedItem.getChildren().add(newTreeItem);
            selectedItem.setExpanded(true);
        });
    }

    @FXML
    protected void onConvertToHTML(ActionEvent actionEvent) {
        System.out.println(htmlEditor.getHtmlText());
    }

    public void handleTreeViewEditCommit(TreeView.EditEvent<String> stringEditEvent) {

    }

    public void handleMouseClicked(MouseEvent mouseEvent) {
        final TreeItem<Page> value = treeView.getSelectionModel().getSelectedItem();
        if (value != null) {
            final Page page = value.getValue();
            previousPageSelected.getValue().setHtml(htmlEditor.getHtmlText());
            previousPageSelected = value;
            htmlEditor.setHtmlText(page.getHtml());
        }
    }
}