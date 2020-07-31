package com.quant.extract.cse.destination;

import com.quant.extract.api.Destination;
import com.quant.extract.api.file.DataFile;
import com.quant.extract.cloud.GoogleBucket;
import lombok.NonNull;

public class TradeSummaryDestination implements Destination {
    @NonNull
    private final GoogleBucket bucket;

    public TradeSummaryDestination(@NonNull final GoogleBucket bucket) {
        this.bucket = bucket;
    }

    @Override
    public void put(@NonNull final DataFile dataFile) {

        bucket.put("eod/trade_summary", dataFile);
    }
}
