package com.quant.extract.cse;

import com.quant.extract.api.SourceException;
import lombok.NonNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class CSEHttpReader {
    private static final CSEHttpReader reader = new CSEHttpReader();

    public static CSEHttpReader getInstance() {
        return reader;
    }

    public byte[] getJson(@NonNull final String relativeUrl) throws SourceException {
        try {
            String response = readFromCSE(relativeUrl, "GET", "application/json");
            return response.getBytes(StandardCharsets.UTF_8);
        } catch (IOException | InterruptedException e) {
            throw new SourceException(e);
        }
    }

    public byte[] postJson(@NonNull final String relativeUrl) throws SourceException {
        try {
            String response = readFromCSE(relativeUrl, "POST", "application/json");
            return response.getBytes(StandardCharsets.UTF_8);
        } catch (IOException | InterruptedException e) {
            throw new SourceException(e);
        }
    }

    public byte[] postJson(@NonNull final String relativeUrl, @NonNull final String body) throws SourceException {
        try {
            String response = readFromCSE(relativeUrl, "POST", "application/json;charset=UTF-8", body);
            return response.getBytes(StandardCharsets.UTF_8);
        } catch (IOException | InterruptedException e) {
            throw new SourceException(e);
        }
    }

    public byte[] post(@NonNull final String relativeUrl, @NonNull final String contentType) throws SourceException {
        return post(relativeUrl, contentType, "");
    }

    public byte[] post(@NonNull final String relativeUrl, @NonNull final String contentType, @NonNull final String body) throws SourceException {
        try {
            String response = readFromCSE(relativeUrl, "POST", contentType, body);
            return response.getBytes(StandardCharsets.UTF_8);
        } catch (IOException | InterruptedException e) {
            throw new SourceException(e);
        }
    }

    private String readFromCSE(final String relativeUrl, final String method, final String contentType) throws IOException, InterruptedException {
        return readFromCSE(relativeUrl, method, contentType, "");
    }

    private String readFromCSE(final String relativeUrl, final String method, final String contentType, final String body) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.cse.lk/" + relativeUrl))
               .header("Content-Type", contentType)
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .build();

        // Send request then print
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
