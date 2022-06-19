package ru.vvsu.helpcreator.model;

import java.io.Serializable;

public class Project implements Serializable {

    private static final long serialVersionUID = 8693707378041282986L;

    private String name;
    private String date;
    private String path;
    private String imagePath;
    private String productName;
    private String productVersion;
    private String typeDoc;
    private String companyName;
    private String year;


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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public String getTypeDoc() {
        return typeDoc;
    }

    public void setTypeDoc(String typeDoc) {
        this.typeDoc = typeDoc;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return this.name + "\r\r" + this.path + "\r" + this.date;
    }
}
