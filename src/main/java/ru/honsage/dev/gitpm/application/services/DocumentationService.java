package ru.honsage.dev.gitpm.application.services;

import ru.honsage.dev.gitpm.domain.ports.HtmlConverter;

public class DocumentationService {
    private final HtmlConverter htmlConverter;

    public DocumentationService(HtmlConverter htmlConverter) {
        this.htmlConverter = htmlConverter;
    }

    public String getUserManualUri() {
        String htmlPath = "/ru/honsage/dev/gitpm/docs/manual/index.html";
        String assetsFolderPath = "/ru/honsage/dev/gitpm/docs/manual";
        return htmlConverter.writeToTempFile(htmlPath, assetsFolderPath);
    }
}
