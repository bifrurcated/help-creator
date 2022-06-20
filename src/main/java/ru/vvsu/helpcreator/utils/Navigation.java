package ru.vvsu.helpcreator.utils;

import ru.vvsu.helpcreator.model.Page;

import java.util.LinkedList;
import java.util.List;

import static ru.vvsu.helpcreator.utils.ProjectPreferences.*;

public class Navigation {
    private static final String isRoot = "IsRoot";
    private static final String isLast = "IsLast";
    private static final String expandClose = "ExpandClosed";
    private static final String expandOpen = "ExpandOpen";
    private static final String expandLeaf = "ExpandLeaf";
    private static final String expand = "<div class=\"Expand\"></div>\n";
    private static final String content = "<div class=\"Content\">\n";
    private static final String contentEnd = "</div>\n";
    private static final String defaultImage = "<img src=\"img/ListView32.png\" alt=\"icon-doc\">\n";
    private static final String defaultImagePage = "<img src=\"../img/ListView32.png\" alt=\"icon-doc\">\n";
    private static final String linkPage = "<a href=\"%s\" class=\"nav-link\">%s</a>\n";
    private static final String ul = "<ul class=\"Container\">\n";
    private static final String ulEnd = "</ul>\n";
    private static final String li = "<li class=\"Node";
    private static final String liMid = "\">\n";
    private static final String liEnd = "</li>\n";

    private final List<Integer> openId = new LinkedList<>();
    private final StringBuilder nav = new StringBuilder();

    private final List<Page> pages;

    public Navigation(List<Page> pages) {
        this.pages = pages;
    }

    public String generateNavigation(int pageCount) {
        nav.setLength(0);
        rootElement(pages.get(0));
        openedId(pages.get(pageCount));
        nav.append(ul);
        createNavigationMenu(pages.get(0), pageCount == 0);
        nav.append(ulEnd);
        openId.clear();
        return nav.toString();
    }

    private void openedId(Page page) {
        if (page.getParentId() != 0) {
            openId.add(page.getParentId());
            openedId(pages.get(page.getParentId() - 1));
        }
    }

    private void rootElement(Page element) {
        element.setRoot(true);
        if(element.getNextId() != 0) {
            rootElement(pages.get(element.getNextId() - 1));
        }
    }

    private void createNavigationMenu(Page element, boolean isIndex) {
        if (element.getChildId() != 0) {
            nav.append(li);
            if (element.isRoot()) {
                nav.append(" ").append(isRoot);
            }
            if (isOpen(element.getId())) {
                nav.append(" ").append(expandOpen);
            } else {
                nav.append(" ").append(expandClose);
            }
            if (element.getNextId() == 0) {
                nav.append(" ").append(isLast);
            }
            nav.append(liMid);
            nav.append(expand).append(content);
            if (isIndex) {
                nav.append(defaultImage);
            } else {
                nav.append(defaultImagePage);
            }
            nav.append(getLinkPage(element, isIndex)).append(contentEnd);
            nav.append(ul);
            createNavigationMenu(pages.get(element.getChildId() - 1), isIndex);
            nav.append(ulEnd);
            nav.append(liEnd);
        }

        if(element.getNextId() != 0) {
            if (element.getChildId() == 0) {
                nav.append(li);
                if (element.isRoot()) {
                    nav.append(" ").append(isRoot);
                }
                nav.append(" ").append(expandLeaf).append(liMid);
                nav.append(expand).append(content);
                if (isIndex) {
                    nav.append(defaultImage);
                } else {
                    nav.append(defaultImagePage);
                }
                nav.append(getLinkPage(element, isIndex)).append(contentEnd);
                nav.append(liEnd);
            }
            createNavigationMenu(pages.get(element.getNextId() - 1), isIndex);
        }

        if (element.getNextId() == 0 && element.getChildId() == 0) {
            nav.append(li);
            if (element.isRoot()) {
                nav.append(" ").append(isRoot);
            }
            nav.append(" ").append(expandLeaf).append(" ").append(isLast).append(liMid);
            nav.append(expand).append(content);
            if (isIndex) {
                nav.append(defaultImage);
            } else {
                nav.append(defaultImagePage);
            }
            nav.append(getLinkPage(element, isIndex)).append(contentEnd);
            nav.append(liEnd);
        }
    }

    public boolean isOpen(final int id) {
        return openId.stream().anyMatch(val -> val == id);
    }

    public String getLinkPage(Page element, boolean forIndex) {
        final String pageFileName = ((element.getId()-1) + "_" + element.getName() + HTML_SUFFIX)
                .replace(" ", "_");
        if (forIndex) {
            if (element.getId() == 1) {
                return String.format(linkPage, MAIN_PAGE_NAME + HTML_SUFFIX, element.getName());
            }
            return String.format(linkPage, DIR_PAGES + pageFileName, element.getName());
        } else {
            if (element.getId() == 1) {
                return String.format(linkPage, "../" + MAIN_PAGE_NAME + HTML_SUFFIX, element.getName());
            }
            return String.format(linkPage, pageFileName, element.getName());
        }
    }
}
