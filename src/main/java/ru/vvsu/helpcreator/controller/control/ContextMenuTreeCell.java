package ru.vvsu.helpcreator.controller.control;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import ru.vvsu.helpcreator.model.Page;

public class ContextMenuTreeCell extends TreeCell<Page> {

    private static TreeItem<Page> root;

    public static Callback<TreeView<Page>,TreeCell<Page>> forTreeView(ContextMenu contextMenu) {
        return forTreeView(contextMenu, null);
    }

    public static Callback<TreeView<Page>,TreeCell<Page>> forTreeView(final ContextMenu contextMenu, final Callback<TreeView<Page>,TextFieldTreeCell<Page>> cellFactory) {
        return listView -> {
            TextFieldTreeCell<Page> cell = cellFactory == null ? new DefaultTreeCell<>() : cellFactory.call(listView);
            cell.setContextMenu(contextMenu);
            final Page item = cell.getItem();
            StringConverter<Page> converter = new PageStringConverter(item);
            cell.setConverter(converter);
            return cell;
        };
    }

    public ContextMenuTreeCell(ContextMenu contextMenu) {
        setContextMenu(contextMenu);
    }

    public static void setRoot(TreeItem<Page> root) {
        ContextMenuTreeCell.root = root;
    }
    public static Page getRootValue() {
        return root.getValue();
    }
}
