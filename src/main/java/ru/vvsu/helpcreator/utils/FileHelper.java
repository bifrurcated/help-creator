package ru.vvsu.helpcreator.utils;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static ru.vvsu.helpcreator.utils.ProjectPreferences.*;

public final class FileHelper {
    
    public static void serialize(Serializable object, String path) {
        try (
                FileOutputStream outputStream = new FileOutputStream(path);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object deserialize(String path) {
        try (
                FileInputStream fileInputStream = new FileInputStream(path);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)
        ) {
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copyDirectory(Path sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
        Files.walk(sourceDirectoryLocation)
                .filter(source -> !source.getFileName().toString().equals(MAIN_PAGE_NAME + HTML_SUFFIX))
                .filter(source -> !source.getFileName().toString().equals(PAGE_NAME + HTML_SUFFIX))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                            .substring(sourceDirectoryLocation.toString().length()));
                    if (!(Files.exists(destination) && Files.isDirectory(destination))) {
                        try {
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public static void copyFile(Path from, Path to) throws IOException {
        if (from.toFile().isFile()) {
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        if (directory.delete()) {
            System.out.println(directory + " is deleted");
        } else {
            System.out.println("Directory not deleted");
        }
    }

    public static String directoryChoicer(String initialDirectory) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("?????????????? ???????????????????? ??????????????");
        final String path;
        if (initialDirectory == null || initialDirectory.isEmpty()) {
            path = new File("").getAbsolutePath();
        } else {
            path = initialDirectory;
        }
        directoryChooser.setInitialDirectory(new File(path));
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            return selectedDirectory.getAbsolutePath();
        }
        return null;
    }

    public static String imageChoicer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("?????????????? ?????????????????????? ??????????????");
        final String path = new File("").getAbsolutePath();
        fileChooser.setInitialDirectory(new File(path));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("IMAGE", "*.png", "*.jpg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("All file", "*.*")
        );
        final File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }
        return null;
    }
}
