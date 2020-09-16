package com.quant.extract.api;

import java.util.HashMap;
import java.util.Map;

public class DataMover implements Runnable {

    private final Map<Source, Destination> mapping;

    public DataMover(final Source source, final Destination destination) {
        this.mapping = new HashMap<>();
        this.mapping.put(source, destination);
    }

    public void add(final Source source, final Destination destination) {
        mapping.put(source, destination);
    }

    @Override
    public void run() {
        mapping.forEach((s, d) -> s.get().forEach(file -> d.put(file)));
    }
}