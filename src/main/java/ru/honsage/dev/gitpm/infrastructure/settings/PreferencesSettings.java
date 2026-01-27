package ru.honsage.dev.gitpm.infrastructure.settings;

import ru.honsage.dev.gitpm.domain.ports.AppSettings;
import ru.honsage.dev.gitpm.domain.ports.BrowserType;
import ru.honsage.dev.gitpm.domain.ports.ShellType;

import java.util.prefs.Preferences;

public class PreferencesSettings implements AppSettings {
    private final Preferences prefs;

    public PreferencesSettings(String nodeName) {
        this.prefs = Preferences.userRoot().node(nodeName);
    }

    @Override
    public ShellType getShellType() {
        return ShellType.fromString(prefs.get("shell", ShellType.CMD.toString()));
    }

    @Override
    public BrowserType getBrowserType() {
        return BrowserType.fromString(prefs.get("browser", BrowserType.DEFAULT.toString()));
    }

    @Override
    public void setShellType(ShellType shellType) {
        prefs.put("shell", shellType.toString());
    }

    @Override
    public void setBrowserType(BrowserType browserType) {
        prefs.put("browser", browserType.toString());
    }
}
