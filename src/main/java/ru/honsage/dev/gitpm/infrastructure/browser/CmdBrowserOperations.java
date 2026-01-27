package ru.honsage.dev.gitpm.infrastructure.browser;

import ru.honsage.dev.gitpm.domain.ports.BrowserOperations;
import ru.honsage.dev.gitpm.domain.ports.BrowserType;
import ru.honsage.dev.gitpm.infrastructure.utils.OSUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class CmdBrowserOperations implements BrowserOperations {
    @Override
    public void open(String url, BrowserType browserType) {
        if (OSUtils.isWindows()) {
            openInBrowserWin(url, browserType);
        } else {
            openInBrowserUnix(url, browserType);
        }
    }

    private void openInBrowserWin(String url, BrowserType browserType) {
        String browserExeAlias;
        switch (browserType) {
            case YANDEX -> browserExeAlias = "browser";
            case CHROME -> browserExeAlias = "chrome";
            case FIREFOX -> browserExeAlias = "firefox";
            case OPERA -> browserExeAlias = "opera";
            case EDGE -> browserExeAlias = "msedge";
            default -> {
                launchDefaultBrowser(url);
                return;
            }
        }
        try {
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", browserExeAlias, url});
        } catch (IOException e) {
            OSUtils.alert("Не удалось открыть браузер: " + e.getMessage(), "error");
        }
    }

    private void openInBrowserUnix(String url, BrowserType browserType) {
        launchDefaultBrowser(url);
    }

    private void launchDefaultBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            OSUtils.alert("Не удалось открыть браузер: " + e.getMessage(), "error");
        }
    }
}
