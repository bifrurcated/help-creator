package ru.vvsu.helpcreator.utils;

import java.io.File;
import java.util.prefs.Preferences;

public class ProjectSettings {
    public static final String ARTIFACT_ID = "help-creator";
    public static final String PROJECT_SETTING_NAME = "project.settings";
    public static final String SAVE_SUFFIX = ".save";
    public static final String DIR_SAVE = File.separator + "save";

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
