package ru.honsage.dev.gitpm.domain.ports;

public interface HtmlConverter {
    String convertToDataUri(String htmlPath, String assetsFolderPath);
}
