package ru.honsage.dev.gitpm.application.services;

import ru.honsage.dev.gitpm.domain.ports.AppSettings;
import ru.honsage.dev.gitpm.domain.ports.BrowserOperations;
import ru.honsage.dev.gitpm.domain.ports.BrowserType;

public class WebBrowserService {
    private BrowserOperations browser;
    private AppSettings settings;

    private BrowserType selectedBrowserType;

    public WebBrowserService(BrowserOperations browserOps, AppSettings settings) {
        this.browser = browserOps;
        this.settings = settings;
    }

    public void openInBrowser(String url) {
        browser.open(url, selectedBrowserType);
    }

    public void setBrowserType(BrowserType browserType) {
        this.selectedBrowserType = browserType;
        this.settings.setBrowserType(browserType);
    }

    public BrowserType getBrowserType() {
        return selectedBrowserType;
    }
}
