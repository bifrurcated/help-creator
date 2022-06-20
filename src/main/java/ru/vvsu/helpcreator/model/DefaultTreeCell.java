package ru.vvsu.helpcreator.model;

import javafx.scene.Node;
import javafx.scene.control.cell.TextFieldTreeCell;
import org.kordamp.ikonli.javafx.FontIcon;

public class DefaultTreeCell<T> extends TextFieldTreeCell<T> {
    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item instanceof Page page) {
            setText(page.getName());
            FontIcon icon = new FontIcon("anto-file-text");
            icon.setIconSize(8);
            setGraphic(icon);
            if (ContextMenuTreeCell.getRootValue() != null && page.equals(ContextMenuTreeCell.getRootValue())) {
                setEditable(false);
            }
        } else if (empty) {
            setText(null);
            setGraphic(null);
        } else if (item instanceof Node newNode) {
            setText(null);
            Node currentNode = getGraphic();
            if (currentNode == null || !currentNode.equals(newNode)) {
                setGraphic(newNode);
            }
        } else {
            setText(item == null ? "null" : item.toString());
            setGraphic(null);
        }
    }
}
