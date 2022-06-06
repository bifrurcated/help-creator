package ru.vvsu.helpcreator.model;

import javafx.scene.Node;
import javafx.scene.control.cell.TextFieldTreeCell;
import org.kordamp.ikonli.javafx.FontIcon;

public class DefaultTreeCell<T> extends TextFieldTreeCell<T> {
    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty && item instanceof Page) {
            Page page = (Page) item;
            setText(page.getName());
            FontIcon icon = new FontIcon("anto-file-text");
            icon.setIconSize(8);
            setGraphic(icon);
        } else if (empty) {
            setText(null);
            setGraphic(null);
        } else if (item instanceof Node) {
            setText(null);
            Node currentNode = getGraphic();
            Node newNode = (Node) item;
            if (currentNode == null || !currentNode.equals(newNode)) {
                setGraphic(newNode);
            }
        } else {
            setText(item == null ? "null" : item.toString());
            setGraphic(null);
        }

//        if (empty) {
//            setText(null);
//            setGraphic(null);
//        } else if (item instanceof Node) {
//            setText(null);
//            Node currentNode = getGraphic();
//            Node newNode = (Node) item;
//            if (currentNode == null || !currentNode.equals(newNode)) {
//                setGraphic(newNode);
//            }
//        } else {
//            setText(item == null ? "null" : item.toString());
//            setGraphic(null);
//        }
    }
}
