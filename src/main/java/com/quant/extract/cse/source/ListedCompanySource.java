package com.quant.extract.cse.source;

import com.quant.extract.api.SourceException;
import com.quant.extract.api.file.DataFile;
import com.quant.extract.cse.CSEHttpReader;
import com.quant.extract.cse.CSESource;

import java.util.List;

public class ListedCompanySource extends CSESource {
    public ListedCompanySource(CSEHttpReader httpReader) {
        super(httpReader);
    }

    @Override
    public List<DataFile> get() throws SourceException {
        byte[] bytes = getHttpReader().postJson("api/list_by_market_cap");
        return List.of(new DataFile("MarketCaps", DataFile.FileType.JSON, bytes));
    }
}
