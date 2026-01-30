package ru.honsage.dev.gitpm.infrastructure.html;

import ru.honsage.dev.gitpm.domain.ports.HtmlConverter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlConverterImpl implements HtmlConverter {
    @Override
    public String convertToDataUri(String htmlPath, String assetsFolderPath) {
        try {
            String htmlContent  = loadAsString(htmlPath);
            htmlContent = embedImages(htmlContent, assetsFolderPath);
            return createDataUri(htmlContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert HTML to Data URI", e);
        }
    }

    @Override
    public String writeToTempFile(String htmlPath, String assetsFolderPath) {
        try {
            String htmlContent = loadAsString(htmlPath);
            htmlContent = embedImages(htmlContent, assetsFolderPath);
            return createTempFile(htmlContent);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp HTML file", e);
        }
    }

    private String loadAsString(String path) throws IOException {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                throw new IOException("Resource not found: " + path);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private byte[] loadAsBytes(String path) throws IOException {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                throw new IOException("Resource not found: " + path);
            }
            return stream.readAllBytes();
        }
    }

    private String embedImages(String htmlContent, String imgFolderPath) {
        Pattern pattern = Pattern.compile(
                "<img\\s+[^>]*src=\"([^\"]+)\"[^>]*>",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(htmlContent);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String imgSource = matcher.group(1);

            if (imgSource.startsWith("data:") || imgSource.startsWith("http")) {
                matcher.appendReplacement(result, matcher.group());
                continue;
            }

            String fullImgPath = imgFolderPath + "/" + imgSource;

            try {
                byte[] imgBytes = loadAsBytes(fullImgPath);
                String mimeType = getMimeType(imgSource);
                String base64 = Base64.getEncoder().encodeToString(imgBytes);

                String replacement = matcher.group().replace(
                        "src=\"" + imgSource + "\"",
                        "src=\"data:" + mimeType + ";base64," + base64 + "\""
                );
                matcher.appendReplacement(result, replacement);
            } catch (IOException e) {
                matcher.appendReplacement(result, matcher.group());
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private String getMimeType(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".gif")) return "image/gif";
        if (filename.endsWith(".svg")) return "image/svg+xml";
        return "image/png";
    }

    private String createDataUri(String htmlContent) {
        String encoded = URLEncoder.encode(htmlContent, StandardCharsets.UTF_8)
                .replace("+", "%20");
        return "data:text/html;charset=utf-8," + encoded;
    }

    private String createTempFile(String htmlContent) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = "gitpm_manual_" + timestamp + ".html";

        Path tempDir = Files.createTempDirectory("gitpm_manual");
        Path htmlFile = tempDir.resolve(filename);

        Files.writeString(htmlFile, htmlContent, StandardCharsets.UTF_8);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.deleteIfExists(htmlFile);
                Files.deleteIfExists(tempDir);
            } catch (IOException _) {}
        }));

        return "file:///" + htmlFile.toAbsolutePath().toString()
                .replace("\\", "/")
                .replace(" ", "%20");
    }
}
