package com.quant.extract.cse.naming;

import com.quant.extract.api.SourceException;
import com.quant.extract.api.file.DataFile;
import com.quant.extract.cse.CSEHttpReader;
import com.quant.extract.cse.CSESource;
import lombok.Getter;
import lombok.NonNull;
import org.json.JSONObject;

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
        List<DataFile> files = src.get();

        byte[] data = files.get(0).getData();
        long lastUpdatedTime = ByteBuffer.wrap(data).getLong();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        fileName = LocalDate.ofInstant(Instant.ofEpochMilli(lastUpdatedTime), ZoneId.of(ZoneId.SHORT_IDS.get("IST"))).format(formatter);
    }

    private static class LastUpdatedTimeSource extends CSESource {
        public LastUpdatedTimeSource(CSEHttpReader httpReader) {
            super(httpReader);
        }

        @Override
        public List<DataFile> get() throws SourceException {
            final byte[] bytes = getHttpReader().getJson("api/lastUpdateTime");
            JSONObject jsonObject = new JSONObject(new String(bytes));
            long lastUpdateTime = (long) jsonObject.get("lastUpdatedTime");
            return List.of(new DataFile("lastUpdateTime",
                    DataFile.FileType.TEXT,
                    ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(lastUpdateTime).array()));

        }
    }
}



