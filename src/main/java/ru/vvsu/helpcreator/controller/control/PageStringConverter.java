package ru.vvsu.helpcreator.controller.control;

import javafx.util.StringConverter;
import ru.vvsu.helpcreator.model.Page;
import ru.vvsu.helpcreator.utils.DefaultValues;

public class PageStringConverter extends StringConverter<Page> {

    private final Page page;

    public PageStringConverter(Page page) {
        this.page = page;
    }

    @Override
    public String toString(Page page) {
        return page.getName();
    }

    @Override
    public Page fromString(String val) {
        if (page == null) {
            Page newPage = new Page();
            newPage.setName(val);
            newPage.setHtml(String.format(DefaultValues.HTML_PAGE, val));
            return newPage;
        } else {
            page.setName(val);
            return page;
        }
    }
}
