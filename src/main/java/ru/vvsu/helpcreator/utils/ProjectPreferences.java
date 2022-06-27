package ru.vvsu.helpcreator.utils;

import ru.vvsu.helpcreator.Main;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

public final class ProjectPreferences {

    private static URI jrtBaseURI;
    private static boolean IS_JRT;

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
            IS_JRT = true;
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

    public static Path getFileSystemPath(URI uri, String ... more){
        if (IS_JRT) {
            Path path = Path.of(uri);
            FileSystem fileSystem = FileSystems.getFileSystem(URI.create("jrt:/"));
            return fileSystem.getPath(path.toString(), more);
        }
        return Path.of(Path.of(uri).toString(), more);
    }

    public static String[] getFolderNamesFromDirectory(URI targetDirectory) {
        File[] directories = null;
        String[] folderNames = new String[0];
        List<String> folderNamesList = new ArrayList<>();
        if(targetDirectory.getScheme().equals("jrt")) {
            Path path = Path.of(targetDirectory);
            assert(Files.exists(path));
            FileSystem jrtFS = FileSystems.getFileSystem(URI.create("jrt:/"));
            assert(Files.exists(jrtFS.getPath(path.toString())));
            try {
                DirectoryStream<Path> stream = Files.newDirectoryStream(jrtFS.getPath(path.toString()));
                for(Path entry: stream) {
                    if(Files.isDirectory(entry)) {
                        folderNamesList.add(entry.getFileName().toString());
                    }
                }
                folderNames = folderNamesList.toArray(new String[0]);
            } catch(Exception e) {
                e.printStackTrace();
            }
        } else {
            directories = new File(targetDirectory).listFiles(File::isDirectory);
        }
        if(directories != null) {
            folderNames = new String[directories.length];
            for(int i=0; i<directories.length; i++) {
                folderNames[i] = directories[i].getName();
            }
        }
        return folderNames;
    }
}
