package ru.vvsu.helpcreator.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.HTMLEditor;
import javafx.util.StringConverter;
import ru.vvsu.helpcreator.model.ContextMenuTreeCell;
import ru.vvsu.helpcreator.model.Page;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    private TreeView<Page> treeView;
    @FXML
    private HTMLEditor htmlEditor;

    private final ImageView defaultImageView;
    private final String defaultHtmlPage;
    private TreeItem<Page> previousPageSelected;

    public MainWindowController() {
        final String pathToImg = Objects.requireNonNull(getClass().getResource("/ru/vvsu/helpcreator/images/ListView32.png")).toExternalForm();
        final Image image = new Image(pathToImg, 16D, 16D, true, false);
        defaultImageView = new ImageView(image);
        defaultHtmlPage = "<html dir=\"ltr\"><head></head><body contenteditable=\"true\"><p style=\"text-align: center;\"><span style=\"font-size: x-large;\">%s</span></p><p style=\"text-align: left;\"><span style=\"font-size: x-large;\"><br></span></p></body></html>";
    }

    public void setRootTreeView(String projectName) {
        final Page page1 = new Page(projectName, String.format(defaultHtmlPage, projectName));
        final Page page2 = new Page("page 1", String.format(defaultHtmlPage, "page 1"));
        final Page page3 = new Page("page 2", String.format(defaultHtmlPage, "page 2"));
        TreeItem<Page> rootItem = new TreeItem<>(page1, defaultImageView);
        rootItem.setExpanded(true);
        TreeItem<Page> treeItem1 = new TreeItem<>(page2, defaultImageView);
        TreeItem<Page> treeItem2 = new TreeItem<>(page3, defaultImageView);
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
        treeView.setCellFactory(lv -> {
            TextFieldTreeCell<Page> cell = new TextFieldTreeCell<>();
            StringConverter<Page> converter = new StringConverter<>() {
                @Override
                public String toString(Page page) {
                    return page.getName();
                }
                @Override
                public Page fromString(String string) {
                    Page city = cell.getItem();
                    if (city == null) {
                        Page newPage = new Page();
                        newPage.setName(string);
                        newPage.setHtml(String.format(defaultHtmlPage, string));
                        return newPage;
                    } else {
                        city.setName(string);
                        return city;
                    }
                }

            };
            cell.setConverter(converter);
            return cell;
        });

        MenuItem addPage = new MenuItem("Add page");
        MenuItem addSubPage = new MenuItem("Add subpage");
        ContextMenu contextMenu2 = new ContextMenu(addPage, addSubPage);
        treeView.setCellFactory(ContextMenuTreeCell.forTreeView(contextMenu2));

        addPage.setOnAction(actionEvent -> {
            Page newPage = new Page("new Page", String.format(defaultHtmlPage, "new Page"));
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
            Page newPage = new Page("new Subpage", String.format(defaultHtmlPage, "new Subpage"));
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