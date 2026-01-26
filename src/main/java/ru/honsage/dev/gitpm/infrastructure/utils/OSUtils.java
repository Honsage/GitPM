package ru.honsage.dev.gitpm.infrastructure.utils;

import javafx.scene.control.Alert;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

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

    public static void openInBrowser(String url) {
        // TODO: support browserType transfer to method
        String browserType = "default";
        if (isWindows()) {
            openInBrowserWin(browserType, url);
        } else {
            openInBrowserUnix(browserType, url);
        }
    }

    private static void openInBrowserUnix(String browserType, String url) {
        launchDefaultBrowser(url);
    }

    private static void openInBrowserWin(String browserType, String url) {
        switch (browserType) {
            case "yandex" -> launchBrowserWin("browser", url);
            case "chrome" -> launchBrowserWin("chrome", url);
            case "firefox" -> launchBrowserWin("firefox", url);
            case "opera" -> launchBrowserWin("opera", url);
            default -> launchDefaultBrowser(url);
        }
    }

    private static void launchDefaultBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Не удалось открыть браузер: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private static void launchBrowserWin(String browserExe, String url) {
        try {
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", browserExe, url});
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Не удалось открыть браузер: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private static List<String> getBrowserExeList() {
        return List.of(
                "browser",
                "chrome",
                "firefox",
                "opera"
        );
    }
}
