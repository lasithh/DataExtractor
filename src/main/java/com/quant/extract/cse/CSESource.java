package com.quant.extract.cse;

import com.quant.extract.api.Source;
import lombok.Getter;

public abstract class CSESource implements Source {
    @Getter
    private final CSEHttpReader httpReader;

    protected CSESource(CSEHttpReader httpReader) {
        this.httpReader = httpReader;
    }
}
