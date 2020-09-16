package com.quant.extract.destination.cloud;

import com.google.auth.oauth2.GoogleCredentials;
import com.quant.extract.api.Destination;
import com.quant.extract.api.file.DataFile;

import java.io.IOException;

public class GoogleBucketDestination implements Destination {
    private final GoogleBucket bucket;
    private final String path;

    public GoogleBucketDestination(final String path) throws IOException {
        this.bucket = GoogleBucket.builder()
                .bucketName("cse_data")
                .projectId("scenic-setup-274303")
                .credentials(GoogleCredentials.fromStream(GoogleBucketDestination.class.getClassLoader().getResourceAsStream("scenic-setup-274303-f9a5dd9b19d3.json")))
                .build();
        this.path = path;
    }

    @Override
    public void put(DataFile dataFile) {
        bucket.put(path, dataFile);
    }
}
