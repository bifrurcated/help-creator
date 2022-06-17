package ru.vvsu.helpcreator.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
import ru.vvsu.helpcreator.utils.Navigation;
import ru.vvsu.helpcreator.utils.ViewWindow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.vvsu.helpcreator.utils.ProjectSettings.*;

public class MainWindow implements Initializable {

    @FXML
    private MenuItem menuItemDelete;
    @FXML
    private MenuItem menuItemNew;
    @FXML
    private MenuItem menuItemOpen;
    @FXML
    private MenuItem menuItemSave;
    @FXML
    private TreeView<Page> treeView;
    @FXML
    private HTMLEditor htmlEditor;

    private final AtomicInteger countPage = new AtomicInteger(1);
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

        MenuItem addPageItem = new MenuItem("Add page");
        MenuItem addSubpageItem = new MenuItem("Add subpage");
        MenuItem deletePageItem = new MenuItem("Delete");
        ContextMenu contextMenu = new ContextMenu(addPageItem, addSubpageItem, deletePageItem);
        treeView.setCellFactory(ContextMenuTreeCell.forTreeView(contextMenu));

        addPageItem.setOnAction(this::handleMenuItemAddPage);
        addSubpageItem.setOnAction(this::handleMenuItemAddSubpage);
        deletePageItem.setOnAction(this::handleMenuItemDeletePage);

        KeyCombination newShortcut = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        menuItemNew.setAccelerator(newShortcut);
        KeyCombination openShortcut = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        menuItemOpen.setAccelerator(openShortcut);
        KeyCombination saveShortcut = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        menuItemSave.setAccelerator(saveShortcut);
        KeyCombination deleteShortcut = new KeyCodeCombination(KeyCode.DELETE);
        menuItemDelete.setAccelerator(deleteShortcut);
    }

    @FXML
    protected void onConvertToHTML(ActionEvent actionEvent) throws IOException, URISyntaxException {
        handleMenuItemSave();
        Path path = Paths.get(project.getPath() + DIR_HTML);
        FileHelper.deleteDirectory(path.toFile());
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        URI uri = ClassLoader.getSystemResource(PATH_TO_TEMPLATE).toURI();
        String mainPath = Paths.get(uri).toString();
        FileHelper.copyDirectory(mainPath, path.toString());
        Navigation navigation = new Navigation(pages);
        final String indexPath = Paths.get(uri) + File.separator + MAIN_PAGE_NAME + HTML_SUFFIX;
        final String indexWritePath = path + File.separator + MAIN_PAGE_NAME + HTML_SUFFIX;
        File indexFile = new File(indexWritePath);
        try (
                final BufferedReader bufferedReader
                        = Files.newBufferedReader(Paths.get(indexPath), StandardCharsets.UTF_8);
                final BufferedWriter bufferedWriter
                        = Files.newBufferedWriter(indexFile.toPath(), StandardCharsets.UTF_8)
        ) {
            String readLine;
            while ((readLine = bufferedReader.readLine()) != null) {
                bufferedWriter.write(readLine+"\n");
                if (readLine.strip().equals("<div onclick=\"tree_toggle(arguments[0])\">")) {
                    bufferedWriter.write(navigation.generateNavigation(0));
                }
                if (readLine.strip().equals("<section>")) {
                    bufferedWriter.write(removeContented(pages.get(0).getHtml()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        uri = ClassLoader.getSystemResource(PATH_TO_TEMPLATE_PAGE).toURI();
        for (int i = 1; i < pages.size(); i++) {
            final Page page = pages.get(i);
            final String pageFileName = ((page.getId()-1) + "_" + page.getName() + HTML_SUFFIX)
                    .replace(" ", "_");
            final String pagePath = Paths.get(uri) + File.separator + PAGE_NAME + HTML_SUFFIX;
            final String pageWritePath = path + File.separator + DIR_PAGES + pageFileName;
            File pageFile = new File(pageWritePath);
            try (
                    final BufferedReader bufferedReader
                            = Files.newBufferedReader(Paths.get(pagePath), StandardCharsets.UTF_8);
                    final BufferedWriter bufferedWriter
                            = Files.newBufferedWriter(pageFile.toPath(), StandardCharsets.UTF_8)
            ) {
                String readLine;
                while ((readLine = bufferedReader.readLine()) != null) {
                    bufferedWriter.write(readLine);
                    if (readLine.strip().equals("<div onclick=\"tree_toggle(arguments[0])\">")) {
                        bufferedWriter.newLine();
                        bufferedWriter.write(navigation.generateNavigation(i));
                    }
                    if (readLine.strip().equals("<section>")) {
                        bufferedWriter.newLine();
                        bufferedWriter.write(removeContented(pages.get(i).getHtml()));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String removeContented(String html) {
        return html.replaceFirst("contenteditable=\"true\"", "");
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
        if (countPage.get() > 1) {
            countPage.set(1);
        }
        traversalTreeView(treeItems.get(0));
        System.out.println("pages size: "+pages.size());
        pages.sort(Comparator.comparingInt(Page::getId));
        pages.forEach(page -> {
            System.out.println("page: "+page.getName() + ", id: " + page.getId());
        });
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

    private void traversalTreeView(TreeItem<Page> treeItem) {
        final Page page = treeItem.getValue();
        System.out.println("page: " + page.getName());
        page.setOpen(treeItem.isExpanded());
        page.setId(countPage.get());
        if (treeItem.getParent() != null && treeItem.getParent() != treeView.getRoot()) {
            page.setParentId(treeItem.getParent().getValue().getId());
        }
        final ObservableList<TreeItem<Page>> itemChildren = treeItem.getChildren();
        if (!itemChildren.isEmpty()) {
            page.setChildId(countPage.incrementAndGet());
            traversalTreeView(itemChildren.get(0));
        }
        final TreeItem<Page> nextSibling = treeItem.nextSibling();
        if (nextSibling != null) {
            page.setNextId(countPage.incrementAndGet());
            traversalTreeView(nextSibling);
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

    public void handleMenuItemAddPage(ActionEvent actionEvent) {
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
    }

    public void handleMenuItemAddSubpage(ActionEvent actionEvent) {
        Page newPage = new Page("new Subpage", String.format(DefaultValues.HTMLPAGE, "new Subpage"));
        TreeItem<Page> newTreeItem = new TreeItem<>(newPage, defaultIcon);
        final TreeItem<Page> selectedItem = treeView.getSelectionModel().getSelectedItem();
        selectedItem.getChildren().add(newTreeItem);
        selectedItem.setExpanded(true);
    }

    public void handleMenuItemDeletePage(ActionEvent actionEvent) {
        final TreeItem<Page> selectedItem = treeView.getSelectionModel().getSelectedItem();
        selectedItem.getParent().getChildren().remove(selectedItem);
    }
}