package ru.vvsu.helpcreator.model;

import java.io.Serializable;

public class Project implements Serializable {
    private String name;
    private String date;
    private String path;

    public Project() {}

    public Project(String name, String date, String path) {
        this.name = name;
        this.date = date;
        this.path = path;
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

    @Override
    public String toString() {
        return this.name + "\t" + this.path + "\t" + this.date;
    }
}
