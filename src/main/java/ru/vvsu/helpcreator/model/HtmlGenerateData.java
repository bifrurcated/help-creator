package ru.vvsu.helpcreator.model;

import java.io.Serializable;
import java.util.Objects;

public class HtmlGenerateData implements Serializable {

    private static final long serialVersionUID = -7757665205212112374L;

    private String htmlPath;
    private boolean openDoc;
    private boolean openFolder;

    public HtmlGenerateData(String htmlPath, boolean openDoc, boolean openFolder) {
        this.htmlPath = htmlPath;
        this.openDoc = openDoc;
        this.openFolder = openFolder;
    }

    public String getHtmlPath() {
        return htmlPath;
    }

    public void setHtmlPath(String htmlPath) {
        this.htmlPath = htmlPath;
    }

    public boolean isOpenDoc() {
        return openDoc;
    }

    public void setOpenDoc(boolean openDoc) {
        this.openDoc = openDoc;
    }

    public boolean isOpenFolder() {
        return openFolder;
    }

    public void setOpenFolder(boolean openFolder) {
        this.openFolder = openFolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HtmlGenerateData htmlData = (HtmlGenerateData) o;
        return openDoc == htmlData.openDoc && openFolder == htmlData.openFolder && Objects.equals(htmlPath, htmlData.htmlPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(htmlPath, openDoc, openFolder);
    }
}
