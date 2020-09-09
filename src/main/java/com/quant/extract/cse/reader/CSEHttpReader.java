package com.quant.extract.cse.reader;

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

    public byte[] get(@NonNull final String relativeUrl) throws SourceException {
        try {
            String response = readFromCSE(relativeUrl, "GET");
            return response.getBytes(StandardCharsets.UTF_8);
        } catch (IOException | InterruptedException e) {
            throw new SourceException(e);
        }
    }

    public byte[] post(@NonNull final String relativeUrl) throws SourceException {
        try {
            String response = readFromCSE(relativeUrl, "POST");
            return response.getBytes(StandardCharsets.UTF_8);
        } catch (IOException | InterruptedException e) {
            throw new SourceException(e);
        }
    }

    private String readFromCSE(final String relativeUrl, String method) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.cse.lk/" + relativeUrl))
                .header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(""))
                .build();

        // Send request then print
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
