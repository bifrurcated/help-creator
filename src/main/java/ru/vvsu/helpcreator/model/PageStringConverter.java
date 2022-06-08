package ru.vvsu.helpcreator.model;

import javafx.util.StringConverter;
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
            newPage.setHtml(String.format(DefaultValues.HTMLPAGE, val));
            return newPage;
        } else {
            page.setName(val);
            return page;
        }
    }
}
