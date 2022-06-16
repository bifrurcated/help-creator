package ru.vvsu.helpcreator.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import ru.vvsu.helpcreator.Main;
import ru.vvsu.helpcreator.model.ContextMenuTreeCell;
import ru.vvsu.helpcreator.model.Page;
import ru.vvsu.helpcreator.model.Project;
import ru.vvsu.helpcreator.utils.DefaultValues;
import ru.vvsu.helpcreator.utils.FileHelper;
import ru.vvsu.helpcreator.utils.ViewWindow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static ru.vvsu.helpcreator.utils.ProjectSettings.DIR_SAVE;
import static ru.vvsu.helpcreator.utils.ProjectSettings.SAVE_SUFFIX;

public class MainWindow implements Initializable {

    @FXML
    private TreeView<Page> treeView;
    @FXML
    private HTMLEditor htmlEditor;

    private final List<Page> pages = new LinkedList<>();
    private ArrayList<Page> loadPages;

    private final FontIcon defaultIcon;
    private TreeItem<Page> previousPageSelected;
    private Project project;

    public MainWindow() {
        defaultIcon = new FontIcon("anto-file-text");
        defaultIcon.setIconSize(8);
    }

    public void setRootTreeView(Project project) throws IOException {
        this.project = project;

        final Page projectPage = new Page(project.getName(), "");
        TreeItem<Page> rootItem = new TreeItem<>(projectPage, defaultIcon);
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);

        Path path = Paths.get(project.getPath() + DIR_SAVE);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        Runnable defaultPages = () -> {
            final Page page2 = new Page("page 1", String.format(DefaultValues.HTMLPAGE, "page 1"));
            TreeItem<Page> treeItem1 = new TreeItem<>(page2, defaultIcon);
            rootItem.getChildren().add(treeItem1);
            htmlEditor.setHtmlText(treeItem1.getValue().getHtml());
            treeView.getSelectionModel().select(1);
            previousPageSelected = treeView.getSelectionModel().getSelectedItem();
            try {
                handleMenuItemSave();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        Optional.ofNullable((Page[]) FileHelper.deserialize(path + File.separator + project.getName() + SAVE_SUFFIX))
                .ifPresentOrElse(deserializeArray -> {
                    loadPages = new ArrayList<>(Arrays.asList(deserializeArray));
                    loadPages.forEach(page -> System.out.println(page.getName()));
                    final ObservableList<TreeItem<Page>> treeItems = treeView.getRoot().getChildren();
                    if (!loadPages.isEmpty()) {
                        setTreeViewElement(loadPages.get(0), treeItems);
                        htmlEditor.setHtmlText(loadPages.get(0).getHtml());
                        treeView.getSelectionModel().select(1);
                        previousPageSelected = treeView.getSelectionModel().getSelectedItem();
                    } else {
                        defaultPages.run();
                    }
                }, defaultPages);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        treeView.setEditable(true);

        MenuItem addPage = new MenuItem("Add page");
        MenuItem addSubPage = new MenuItem("Add subpage");
        MenuItem deletePage = new MenuItem("Delete");
        ContextMenu contextMenu = new ContextMenu(addPage, addSubPage, deletePage);
        treeView.setCellFactory(ContextMenuTreeCell.forTreeView(contextMenu));

        addPage.setOnAction(actionEvent -> {
            Page newPage = new Page("new Page", String.format(DefaultValues.HTMLPAGE, "new Page"));
            TreeItem<Page> newTreeItem = new TreeItem<>(newPage, defaultIcon);
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
            TreeItem<Page> newTreeItem = new TreeItem<>(newPage, defaultIcon);
            final TreeItem<Page> selectedItem = treeView.getSelectionModel().getSelectedItem();
            selectedItem.getChildren().add(newTreeItem);
            selectedItem.setExpanded(true);
        });
        deletePage.setOnAction(actionEvent -> {
            final TreeItem<Page> selectedItem = treeView.getSelectionModel().getSelectedItem();
            selectedItem.getParent().getChildren().remove(selectedItem);
        });
    }

    @FXML
    protected void onConvertToHTML(ActionEvent actionEvent) {
        System.out.println(htmlEditor.getHtmlText());
    }

    public void handleMouseClicked(MouseEvent mouseEvent) {
        final TreeItem<Page> value = treeView.getSelectionModel().getSelectedItem();
        if (value != null && value != treeView.getRoot()) {
            final Page page = value.getValue();
            previousPageSelected.getValue().setHtml(htmlEditor.getHtmlText());
            previousPageSelected = value;
            htmlEditor.setHtmlText(page.getHtml());
        }
    }

    public void handleMenuItemNew(ActionEvent actionEvent) throws IOException {
        Stage projectStage = (Stage) treeView.getParent().getScene().getWindow();
        ViewWindow.openNewProject(projectStage);
    }

    public void handleMenuItemOpen(ActionEvent actionEvent) {
        System.out.println("deserialize");
        Optional.ofNullable(saveChoicer())
                .ifPresent(pathToSave -> Optional.ofNullable((Page[]) FileHelper.deserialize(pathToSave))
                        .ifPresent(deserializeArray -> {
                            loadPages = new ArrayList<>(Arrays.asList(deserializeArray));
                            loadPages.forEach(page -> System.out.println(page.getName()));
                            final ObservableList<TreeItem<Page>> treeItems = treeView.getRoot().getChildren();
                            treeItems.clear();
                            if (!loadPages.isEmpty()) {
                                setTreeViewElement(loadPages.get(0), treeItems);
                            }
                        }));
    }

    private void setTreeViewElement(Page page, ObservableList<TreeItem<Page>> treeItems) {
        final TreeItem<Page> treeItem = new TreeItem<>(page, defaultIcon);
        treeItem.setExpanded(page.isOpen());
        treeItems.add(treeItem);
        if (page.getChildId() != 0) {
            setTreeViewElement(loadPages.get(page.getChildId()-1), treeItem.getChildren());
        }
        if(page.getNextId() != 0) {
            setTreeViewElement(loadPages.get(page.getNextId()-1), treeItems);
        }
    }

    public void handleMenuItemSave() throws IOException {
        final ObservableList<TreeItem<Page>> treeItems = treeView.getRoot().getChildren();
        if (treeItems.isEmpty()) {
            return;
        }
        if (!pages.isEmpty()) {
            pages.clear();
        }
        if (previousPageSelected != null) {
            previousPageSelected.getValue().setHtml(htmlEditor.getHtmlText());
        }
        traversalTreeView(treeItems.get(0), 1);
        System.out.println("pages size: "+pages.size());
        pages.forEach(page -> {
            System.out.println("page: "+page.getName() + ", id: " + page.getId());
        });
        pages.sort(Comparator.comparingInt(Page::getId));
        Path path = Paths.get(project.getPath() + DIR_SAVE);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        FileHelper.serialize(pages.toArray(new Page[0]), path + File.separator + project.getName() + SAVE_SUFFIX);
        System.out.println("serialize");
    }

    public void handleMenuItemClose(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        ViewWindow.openProjectCreate(stage);
        ViewWindow.closeWindow(treeView.getParent());
    }

    public void handleMenuItemAbout(ActionEvent actionEvent) {
        Main.hostServices.showDocument("https://github.com/bifrurcated/help-creator");
    }

    private void traversalTreeView(TreeItem<Page> treeItem, int countPage) {
        final Page page = treeItem.getValue();
        System.out.println("page: "+page.getName());
        page.setOpen(treeItem.isExpanded());
        page.setId(countPage);
        final ObservableList<TreeItem<Page>> itemChildren = treeItem.getChildren();
        if (!itemChildren.isEmpty()) {
            page.setChildId(++countPage);
            traversalTreeView(itemChildren.get(0), countPage);
        }
        final TreeItem<Page> nextSibling = treeItem.nextSibling();
        if (nextSibling != null) {
            page.setNextId(++countPage);
            traversalTreeView(nextSibling, countPage);
        }
        pages.add(page);
    }

    private String saveChoicer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить сохранение");
        fileChooser.setInitialDirectory(new File(project.getPath()));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SAVE", "*.save"),
                new FileChooser.ExtensionFilter("All file", "*.*")
        );
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
}