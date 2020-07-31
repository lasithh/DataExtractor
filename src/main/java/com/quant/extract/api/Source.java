package com.quant.extract.api;


import com.quant.extract.api.file.DataFile;

import java.util.List;

public interface Source {
    List<DataFile> files() throws SourceException;
}
