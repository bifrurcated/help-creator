package ru.vvsu.helpcreator.model;

import javafx.util.StringConverter;

public class PageStringConverter extends StringConverter<Page> {

    private String name;
    private String html;

    public PageStringConverter(String name, String html) {
        this.name = name;
        this.html = html;
    }

    @Override
    public String toString(Page page) {
        return page.getName();
    }

    @Override
    public Page fromString(String s) {
        return new Page(s,html);
    }
}
