package com.quant.extract.api;

/**
 * Moves data from {@link DataMover#source} to the {@link DataMover#destination}.
 */
public class DataMover implements Runnable {

    private final Source source;

    private final Destination destination;

    public DataMover(final Source source, Destination destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public void run() {
        source.files().forEach(e -> destination.put(e));
    }
}