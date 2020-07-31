package com.quant.extract.cse.source;

import com.quant.extract.api.Source;
import com.quant.extract.cse.reader.CSEHttpReader;

abstract class CSESource implements Source {
    private final CSEHttpReader httpReader;

    protected CSESource(CSEHttpReader httpReader) {
        this.httpReader = httpReader;
    }

    protected byte[] get(final String relativeUrl) {
        return httpReader.get(relativeUrl);
    }
}
