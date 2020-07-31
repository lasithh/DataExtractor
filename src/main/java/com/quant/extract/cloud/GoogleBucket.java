package com.quant.extract.cloud;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.quant.extract.api.file.DataFile;
import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ToString
@Builder
public class GoogleBucket {
    @NonNull
    private final String bucketName;

    @NonNull
    private final String projectId;

    @NonNull
    private final GoogleCredentials credentials;

    public GoogleBucket(final String bucketName, final String projectId,
                        final GoogleCredentials credentials) {
        this.bucketName = bucketName;
        this.projectId = projectId;
        this.credentials = credentials;
    }

    public void put(final String filePath, final DataFile dataFile)  {
        final Storage storage = StorageOptions.newBuilder()
                .setCredentials(credentials).build().getService();

        final BlobId blobId = BlobId.of(bucketName,
                filePath + dataFile.getFileName() + "." + dataFile.getFileType().toString().toLowerCase());
        final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        // Save the file into the bucket
        storage.create(blobInfo, dataFile.getData());
    }
}
