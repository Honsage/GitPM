package ru.honsage.dev.gitpm.infrastructure.utils;

import javafx.scene.control.Alert;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class OSUtils {
    public static void copyToClipBoard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    public static void openFolder(String path) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(path));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Не удалось открыть папку: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static void openInBrowser(String url) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(url));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Не удалось открыть браузер: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
