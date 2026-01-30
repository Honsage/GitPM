package ru.honsage.dev.gitpm.application.services;

import ru.honsage.dev.gitpm.domain.ports.HtmlConverter;

public class DocumentationService {
    private final HtmlConverter htmlConverter;

    public DocumentationService(HtmlConverter htmlConverter) {
        this.htmlConverter = htmlConverter;
    }

    public String getUserManualUri() {
        String htmlPath = "/ru/honsage/dev/gitpm/html/user-manual.html";
        String assetsFolderPath = "/ru/honsage/dev/gitpm/images/user-manual";
        return htmlConverter.convertToDataUri(htmlPath, assetsFolderPath);
    }
}
