package ru.honsage.dev.gitpm.domain.ports;

public enum BrowserType {
    YANDEX,
    CHROME,
    FIREFOX,
    OPERA,
    EDGE,
    DEFAULT;

    public static BrowserType fromString(String browserTypeName) {
        return switch (browserTypeName) {
            case "YANDEX" -> YANDEX;
            case "CHROME" -> CHROME;
            case "FIREFOX" -> FIREFOX;
            case "OPERA" -> OPERA;
            case "EDGE" -> EDGE;
            default -> DEFAULT;
        };
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
