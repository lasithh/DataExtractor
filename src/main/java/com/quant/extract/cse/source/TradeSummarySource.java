package com.quant.extract.cse.source;

import com.quant.extract.api.file.DataFile;
import com.quant.extract.cse.naming.FileNameProvider;
import com.quant.extract.cse.reader.CSEHttpReader;
import lombok.NonNull;

import java.util.List;

public final class TradeSummarySource extends CSESource {
    private final FileNameProvider fileNameProvider;

    public TradeSummarySource(@NonNull final CSEHttpReader httpReader,
                              @NonNull final FileNameProvider fileNameProvider) {
        super(httpReader);
        this.fileNameProvider = fileNameProvider;
    }

    @Override
    public List<DataFile> files() {
        byte[] tradeSummary = post("/api/tradeSummary");
        return List.of(new DataFile(fileNameProvider.getFileName(), DataFile.FileType.CSV, tradeSummary));
    }
}
