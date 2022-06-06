package ru.vvsu.helpcreator.model;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class ContextMenuTreeCell<T> extends TreeCell<T> {

    public static <T> Callback<TreeView<T>,TreeCell<T>> forTreeView(ContextMenu contextMenu) {
        return forTreeView(contextMenu, null);
    }

    public static <T> Callback<TreeView<T>,TreeCell<T>> forTreeView(final ContextMenu contextMenu, final Callback<TreeView<T>,TreeCell<T>> cellFactory) {
        return listView -> {
            TreeCell<T> cell = cellFactory == null ? new DefaultTreeCell<>() : cellFactory.call(listView);
            cell.setContextMenu(contextMenu);
            return cell;
        };
    }

    public ContextMenuTreeCell(ContextMenu contextMenu) {
        setContextMenu(contextMenu);
    }
}
