package com.quant.extract.cse.naming;

import com.quant.extract.api.file.DataFile;
import com.quant.extract.cse.reader.CSEHttpReader;
import com.quant.extract.cse.source.LastUpdatedTimeSource;
import lombok.Getter;
import lombok.NonNull;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileNameProvider {

    @NonNull
    @Getter
    private final String fileName;

    public FileNameProvider(final CSEHttpReader httpReader) {
        LastUpdatedTimeSource src = new LastUpdatedTimeSource(httpReader);
        List<DataFile> files = src.files();

        byte[] data = files.get(0).getData();
        long lastUpdatedTime = ByteBuffer.wrap(data).getLong();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        fileName = LocalDate.ofInstant(Instant.ofEpochMilli(lastUpdatedTime), ZoneId.of(ZoneId.SHORT_IDS.get("IST"))).format(formatter);
    }
}
