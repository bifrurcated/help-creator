package ru.vvsu.helpcreator.model;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ContextMenuTreeCell<T> extends TreeCell<T> {

    public static <T> Callback<TreeView<T>,TreeCell<T>> forTreeView(ContextMenu contextMenu) {
        return forTreeView(contextMenu, null);
    }

    public static <T> Callback<TreeView<T>,TreeCell<T>> forTreeView(final ContextMenu contextMenu, final Callback<TreeView<T>,TextFieldTreeCell<T>> cellFactory) {
        return listView -> {
            TextFieldTreeCell<T> cell = cellFactory == null ? new DefaultTreeCell<>() : cellFactory.call(listView);
            cell.setContextMenu(contextMenu);
            if (cell.getItem() instanceof Page) {
                final Page item = (Page) cell.getItem();
                StringConverter<Page> converter = new PageStringConverter(item);
                cell.setConverter((StringConverter<T>) converter);
            }

            return cell;
        };
    }

    public ContextMenuTreeCell(ContextMenu contextMenu) {
        setContextMenu(contextMenu);
    }
}
