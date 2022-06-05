package ru.vvsu.helpcreator.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.File;

public class HelloController {

    @FXML
    private HTMLEditor htmlEditor;
    @FXML
    private WebView webView;
    @FXML
    private TextArea textAreaHtmlView;

    @FXML
    protected void onConvertToHTML(ActionEvent actionEvent) {
        File file = new File("E:\\1_УЧЁБА\\Курсовая\\Справка Пример\\test\\Настройки _ REG.RU.html");
        final String path = file.toURI().toString();
//        String htmlText = htmlEditor.getHtmlText();
//        textAreaHtmlView.setText(htmlText);
        WebEngine webEngine = webView.getEngine();

        webEngine.load(path);
    }
}