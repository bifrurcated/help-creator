package ru.vvsu.helpcreator.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return date == project.date && Objects.equals(name, project.name) && Objects.equals(path, project.path) && Objects.equals(settings, project.settings) && Objects.equals(htmlGenerateData, project.htmlGenerateData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, path, settings, htmlGenerateData);
    }

    @Override
    public String toString() {
        return this.name + "\r\r" + this.path + "\r" + this.date;
    }
}
