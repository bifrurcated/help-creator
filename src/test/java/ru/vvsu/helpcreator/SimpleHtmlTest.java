package ru.vvsu.helpcreator;

import com.lightdev.app.shtm.SHTMLEditorPane;
import com.lightdev.app.shtm.SHTMLPanel;
import com.lightdev.app.shtm.SHTMLPanelImpl;
import com.lightdev.app.shtm.SplashScreen;

import javax.swing.*;
import java.awt.*;

public class SimpleHtmlTest {
    public static void main(String[] args) {
        SHTMLPanelImpl.setInternalUiResources();
        JFrame frame = new JFrame("HTML Editor");
        SHTMLPanel panel = SHTMLPanelImpl.createSHTMLPanel();
        SHTMLEditorPane editorPane = new SHTMLEditorPane();
        SplashScreen.showInstance();
        editorPane.setPreferredSize(new Dimension(1024, 720));
//        panel2.setPreferredSize(new Dimension(1000, 500));
//        panel2.setBackground(Color.blue);
        panel.setPreferredSize(new Dimension(1024, 720));
        frame.getContentPane().add(panel);
        frame.pack();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        //Center the window
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true); // show the window
        //panel.getMostRecentFocusOwner().requestFocus();
        SplashScreen.hideInstance();
    }
}
