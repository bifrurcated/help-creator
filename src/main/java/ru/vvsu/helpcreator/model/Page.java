package ru.vvsu.helpcreator.model;

import java.io.Serializable;

public class Page implements Serializable {
    private String name;
    private String html;

    public Page() {}

    public Page(String name, String html) {
        this.name = name;
        this.html = html;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    @Override
    public String toString()  {
        return this.name;
    }
}
