package ru.vvsu.helpcreator.service;

import javafx.concurrent.Task;
import ru.vvsu.helpcreator.model.Page;
import ru.vvsu.helpcreator.model.Project;
import ru.vvsu.helpcreator.model.Settings;
import ru.vvsu.helpcreator.utils.Navigation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static ru.vvsu.helpcreator.utils.ProjectPreferences.*;

public class HtmlGenerateTask extends Task<Void> {

    private final Project project;
    private final List<Page> pages;
    private final String path;

    private long MAX;
    private long progress;

    public HtmlGenerateTask(Project project, List<Page> pages, String path) {
        this.project = project;
        this.pages = pages;
        this.path = path;
    }

    @Override
    protected Void call() throws URISyntaxException, IOException {
        Navigation navigation = new Navigation(pages);

        URI uriTemplate = ClassLoader.getSystemResource(PATH_TO_TEMPLATE).toURI();
        final String indexPath = Paths.get(uriTemplate) + File.separator + MAIN_PAGE_NAME + HTML_SUFFIX;
        final String indexWritePath = path + File.separator + MAIN_PAGE_NAME + HTML_SUFFIX;
        MAX = Files.newBufferedReader(Paths.get(indexPath)).lines().count() * pages.size();
        progress = 0;
        createPage(navigation, indexPath, indexWritePath, 0);

        URI uriTemplatePages = ClassLoader.getSystemResource(PATH_TO_TEMPLATE_PAGES).toURI();
        for (int i = 1; i < pages.size(); i++) {
            final Page page = pages.get(i);
            final String pageFileName = ((page.getId()-1) + "_" + page.getName() + HTML_SUFFIX).replace(" ", "_");
            final String pagePath = Paths.get(uriTemplatePages) + File.separator + PAGE_NAME + HTML_SUFFIX;
            final String pageWritePath = path + File.separator + DIR_PAGES + pageFileName;
            createPage(navigation, pagePath, pageWritePath, i);
        }
        return null;
    }

    private void createPage(Navigation navigation, String readerPath, String writerPath, int index) {
        final String pageHeader = getPageHeader(project.getSettings());
        final String pageFooter = getPageFooter(project.getSettings());
        File writeFile = new File(writerPath);
        try (
                final BufferedReader bufferedReader
                        = Files.newBufferedReader(Paths.get(readerPath), StandardCharsets.UTF_8);
                final BufferedWriter bufferedWriter
                        = Files.newBufferedWriter(writeFile.toPath(), StandardCharsets.UTF_8)
        ) {
            String readLine;
            while ((readLine = bufferedReader.readLine()) != null) {
                updateProgress(++progress, MAX);
                bufferedWriter.write(readLine);
                if (readLine.strip().equals("<div class=\"container-header\">")) {
                    bufferedWriter.newLine();
                    bufferedWriter.write(pageHeader);
                }
                if (readLine.strip().equals("<div onclick=\"tree_toggle(arguments[0])\">")) {
                    bufferedWriter.newLine();
                    bufferedWriter.write(navigation.generateNavigation(index));
                }
                if (readLine.strip().equals("<section>")) {
                    bufferedWriter.newLine();
                    bufferedWriter.write(removeContented(pages.get(index).getHtml()));
                }
                if (readLine.strip().equals("<div class=\"container-footer\">")) {
                    bufferedWriter.newLine();
                    bufferedWriter.write(pageFooter);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPageHeader(Settings settings) {
        final String h2 = "<h2>";
        final String h2End = "</h2>\n";
        final String h3 = "<h3>%s</h3>\n";
        StringBuilder sb = new StringBuilder();
        sb.append(h2);
        if (settings.getProductName() != null) {
            sb.append(settings.getProductName());
        }
        if (settings.getProductVersion() != null) {
            if (settings.getProductName() != null) {
                sb.append(" ");
            }
            sb.append(settings.getProductVersion());
        }
        sb.append(h2End);
        if (settings.getTypeDoc() != null) {
            sb.append(String.format(h3, settings.getTypeDoc()));
        }
        return sb.toString();
    }

    private String getPageFooter(Settings settings) {
        final String h4 = "<h4>";
        final String h4End = "</h4>";
        StringBuilder sb = new StringBuilder();
        sb.append(h4);
        if (settings.getCompanyName() != null) {
            sb.append(settings.getCompanyName());
        }
        if (settings.getYear() != null) {
            if (settings.getCompanyName() != null && !settings.getCompanyName().isEmpty()) {
                sb.append(",");
            }
            sb.append(" ").append(settings.getYear());
        }
        sb.append(h4End);
        return sb.toString();
    }

    private String removeContented(String html) {
        return html.replaceFirst("contenteditable=\"true\"", "");
    }
}
