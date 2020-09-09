package com.quant.extract.api.file;

import lombok.NonNull;

/**
 * This structure contains the binary data of the file and the location on the target data source of the file.
 */
public class DataFile {
    public enum FileType {
        CSV, PNG, PDF, TEXT
    }

    @NonNull
    private final String fileName;

    // File type
    @NonNull
    private final FileType fileType;

    // File Content
    @NonNull
    private final byte[] data;

    public DataFile(final String fileName, final FileType fileType, final byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public FileType getFileType() {
        return fileType;
    }

    public byte[] getData() {
        return data;
    }
}
