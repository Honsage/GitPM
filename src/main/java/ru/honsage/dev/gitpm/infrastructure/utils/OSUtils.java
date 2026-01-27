package ru.honsage.dev.gitpm.infrastructure.utils;

import javafx.scene.control.Alert;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class OSUtils {
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

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

    public static void alert(String message, String type) {
        Alert.AlertType alertType = Alert.AlertType.ERROR;
        if (type.toLowerCase().startsWith("warning")) alertType = Alert.AlertType.WARNING;
        Alert alert = new Alert(alertType, message);
        alert.showAndWait();
    }
}
