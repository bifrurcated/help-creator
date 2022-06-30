package ru.vvsu.helpcreator.model;

import java.io.Serial;
import java.io.Serializable;

import static ru.vvsu.helpcreator.utils.ProjectPreferences.DIR_HTML;

public class Project implements Serializable {

    @Serial
    private static final long serialVersionUID = 8693707378041282986L;

    private String name;
    private long date;
    private String path;
    private final Settings settings;
    private final HtmlGenerateData htmlGenerateData;

    public Project(String name, long date, String path) {
        this(name, date, path, "");
    }

    public Project(String name, long date, String path, String imagePath) {
        this.name = name;
        this.date = date;
        this.path = path;
        this.settings = new Settings(imagePath);
        this.htmlGenerateData = new HtmlGenerateData(path+DIR_HTML, false, false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Settings getSettings() {
        return settings;
    }

    public HtmlGenerateData getHtmlGenerateData() {
        return htmlGenerateData;
    }

    @Override
    public String toString() {
        return this.name + "\r\r" + this.path + "\r" + this.date;
    }
}
