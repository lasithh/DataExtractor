package com.quant.extract.cse.naming;

import com.quant.extract.cse.reader.CSEHttpReader;
import lombok.Getter;
import lombok.NonNull;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileNameProvider {

    @NonNull
    @Getter
    private final String fileName;

    public FileNameProvider(final CSEHttpReader httpReader) {
        final byte[] bytes = httpReader.get("api/lastUpdateTime");
        JSONObject jsonObject = new JSONObject(new String(bytes));
        long lastUpdateTime = Long.valueOf((String) jsonObject.get("lastUpdateTime"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        fileName = LocalDate.ofEpochDay(lastUpdateTime).format(formatter);
    }
}
