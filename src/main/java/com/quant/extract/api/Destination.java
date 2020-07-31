package com.quant.extract.api;

import com.quant.extract.api.file.DataFile;

import java.io.IOException;

public interface Destination {
    public void put(final DataFile dataFile);
}
