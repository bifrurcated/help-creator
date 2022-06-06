package ru.vvsu.helpcreator.model;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import ru.vvsu.helpcreator.utils.DefaultValues;

public class ContextMenuTreeCell<T> extends TreeCell<T> {

    public static <T> Callback<TreeView<T>,TreeCell<T>> forTreeView(ContextMenu contextMenu) {
        return forTreeView(contextMenu, null);
    }

    public static <T> Callback<TreeView<T>,TreeCell<T>> forTreeView(final ContextMenu contextMenu, final Callback<TreeView<T>,TextFieldTreeCell<T>> cellFactory) {
        return listView -> {
            TextFieldTreeCell<T> cell = cellFactory == null ? new DefaultTreeCell<>() : cellFactory.call(listView);
            cell.setContextMenu(contextMenu);

            StringConverter<Page> converter = new StringConverter<>() {
                @Override
                public String toString(Page page) {
                    return page.getName();
                }
                @Override
                public Page fromString(String string) {
                    Page city = (Page) cell.getItem();
                    if (city == null) {
                        Page newPage = new Page();
                        newPage.setName(string);
                        newPage.setHtml(String.format(DefaultValues.HTMLPAGE, string));
                        return newPage;
                    } else {
                        city.setName(string);
                        return city;
                    }
                }
            };
            cell.setConverter((StringConverter<T>) converter);
            return cell;
        };
    }

    public ContextMenuTreeCell(ContextMenu contextMenu) {
        setContextMenu(contextMenu);
    }
}
