package ru.vvsu.helpcreator.model;

public class Page {
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
