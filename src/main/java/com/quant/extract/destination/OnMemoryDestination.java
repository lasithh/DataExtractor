package com.quant.extract.destination;

import com.quant.extract.api.Destination;
import com.quant.extract.api.file.DataFile;
import lombok.Getter;

public class OnMemoryDestination implements Destination {
    @Getter
    private DataFile file;

    @Override
    public void put(DataFile dataFile) {
        this.file = dataFile;
    }
}
