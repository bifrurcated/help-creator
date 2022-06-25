package ru.vvsu.helpcreator.utils;

import ru.vvsu.helpcreator.Main;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

public final class ProjectPreferences {

    private static URI jrtBaseURI;

    public static URI PATH_TO_TEMPLATE;
    public static URI PATH_TO_TEMPLATE_PAGES;

    public static final String ARTIFACT_ID = "help-creator";
    public static final String PROJECT_SETTING_NAME = "project.settings";
    public static final String SAVE_SUFFIX = ".save";
    public static final String HTML_SUFFIX = ".html";
    public static final String DIR_SAVE = File.separator + "save";
    public static final String DIR_HTML = File.separator + "html";
    public static final String DIR_PAGES = "pages" + File.separator;
    public static final String PATH_TO_LOGO = "img/logo.png";
    public static final String MAIN_PAGE_NAME = "index";
    public static final String PAGE_NAME = "page";

    public static final Pattern PATTERN_PROJECT_NAME = Pattern.compile("[a-zA-Zа-яА-Я][a-zA-Zа-яА-Я0-9\\s]{0,51}$");

    public static void initializePaths() {
        if (checkJRT()) {
            PATH_TO_TEMPLATE = URI.create(jrtBaseURI + "ru/vvsu/helpcreator/template/");
            PATH_TO_TEMPLATE_PAGES = URI.create(jrtBaseURI + "ru/vvsu/helpcreator/template/pages/");
        } else {
            PATH_TO_TEMPLATE = URI.create(String.valueOf(Main.class.getResource("/ru/vvsu/helpcreator/template")));
            PATH_TO_TEMPLATE_PAGES = URI.create(String.valueOf(Main.class.getResource("/ru/vvsu/helpcreator/template/pages")));
        }
    }

    private static boolean checkJRT() {
        URL resource = Main.class.getResource("/ru/vvsu/helpcreator/template");
        if(resource == null || resource.getProtocol().equals("jrt")) {
            jrtBaseURI = URI.create("jrt:/helpcreator/");
            return true;
        }
        return false;
    }

    public static boolean isNotUseProjectPath(Preferences preferences, String projectPath) {
        final int projectCount = preferences.getInt(ARTIFACT_ID, 0);
        if (projectCount > 0) {
            for (int i = 1; i <= projectCount; i++) {
                final String haveProjectPath = preferences.get(ARTIFACT_ID + i, "");
                if (!haveProjectPath.isEmpty() && haveProjectPath.equalsIgnoreCase(projectPath)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void putProjectPathIfAbsent(Preferences preferences, String projectPath) {
        if (isNotUseProjectPath(preferences, projectPath)) {
            final int projectCount = preferences.getInt(ARTIFACT_ID, 0) + 1;
            preferences.putInt(ARTIFACT_ID, projectCount);
            preferences.put(ARTIFACT_ID+projectCount, projectPath);
        }
    }
}
