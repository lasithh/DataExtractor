package com.quant.extract.cse.source;

import com.quant.extract.api.SourceException;
import com.quant.extract.api.file.DataFile;
import com.quant.extract.cse.reader.CSEHttpReader;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.List;

public class LastUpdatedTimeSource extends CSESource {
    public LastUpdatedTimeSource(CSEHttpReader httpReader) {
        super(httpReader);
    }

    @Override
    public List<DataFile> files() throws SourceException {
        final byte[] bytes = get("api/lastUpdateTime");
        JSONObject jsonObject = new JSONObject(new String(bytes));
        long lastUpdateTime = (long) jsonObject.get("lastUpdatedTime");
        return List.of(new DataFile("lastUpdateTime",
                DataFile.FileType.TEXT,
                ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(lastUpdateTime).array()));

    }
}
