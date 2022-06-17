package ru.vvsu.helpcreator.model;

import java.io.Serializable;
import java.util.Objects;

public class Page implements Serializable {

    private static final long serialVersionUID = -522212596571546003L;

    private String name;
    private String html;
    private boolean isOpen;
    private int id;
    private int parentId;
    private int childId;
    private int nextId;

    private transient boolean isRoot;

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

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public int getNextId() {
        return nextId;
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    @Override
    public String toString()  {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return isOpen == page.isOpen && id == page.id && parentId == page.parentId && childId == page.childId && nextId == page.nextId && Objects.equals(name, page.name) && Objects.equals(html, page.html);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, html, isOpen, id, parentId, childId, nextId);
    }
}
