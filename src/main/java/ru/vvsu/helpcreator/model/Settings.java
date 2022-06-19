package ru.vvsu.helpcreator.model;

import java.io.Serializable;
import java.util.Objects;

public class Settings implements Serializable {

    private static final long serialVersionUID = -2271176427530950929L;

    private String imagePath;
    private String productName;
    private String productVersion;
    private String typeDoc;
    private String companyName;
    private String year;

    public Settings (String imagePath) {
        this(imagePath, "", "", "", "", "");
    }

    public Settings(String imagePath, String productName, String productVersion, String typeDoc, String companyName, String year) {
        this.imagePath = imagePath;
        this.productName = productName;
        this.productVersion = productVersion;
        this.typeDoc = typeDoc;
        this.companyName = companyName;
        this.year = year;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return Objects.equals(imagePath, settings.imagePath) && Objects.equals(productName, settings.productName) && Objects.equals(productVersion, settings.productVersion) && Objects.equals(typeDoc, settings.typeDoc) && Objects.equals(companyName, settings.companyName) && Objects.equals(year, settings.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imagePath, productName, productVersion, typeDoc, companyName, year);
    }
}
