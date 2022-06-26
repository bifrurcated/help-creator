package ru.vvsu.helpcreator.controller.control;

import javafx.scene.control.cell.TextFieldTreeCell;
import org.kordamp.ikonli.javafx.FontIcon;
import ru.vvsu.helpcreator.model.Page;

public class PageTreeCell extends TextFieldTreeCell<Page> {
    @Override
    public void updateItem(Page page, boolean empty) {
        super.updateItem(page, empty);
        if (!empty && page != null) {
            setText(page.getName());
            FontIcon icon = new FontIcon("anto-file-text");
            icon.setIconSize(8);
            setGraphic(icon);
            if (ContextMenuTreeCell.getRootValue() != null &&
                    page.equals(ContextMenuTreeCell.getRootValue())) {
                setEditable(false);
            }
        } else {
            setText(null);
            setGraphic(null);
        }
    }
}
