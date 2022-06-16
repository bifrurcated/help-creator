package ru.vvsu.helpcreator.model;

import java.io.Serializable;

public class Project implements Serializable {

    private static final long serialVersionUID = 8693707378041282986L;

    private String name;
    private String date;
    private String path;
    private String imagePath;

    public Project() {}

    public Project(String name, String date, String path) {
        this(name, date, path, "");
    }

    public Project(String name, String date, String path, String imagePath) {
        this.name = name;
        this.date = date;
        this.path = path;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return this.name + "\t\t" + this.path + "\t\t" + this.date;
    }
}
