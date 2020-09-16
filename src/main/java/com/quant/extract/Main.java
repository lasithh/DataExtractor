package com.quant.extract;

import com.quant.extract.api.DataMover;
import com.quant.extract.api.Destination;
import com.quant.extract.cse.CSEHttpReader;
import com.quant.extract.cse.naming.FileNameProvider;
import com.quant.extract.cse.parse.SymbolsParser;
import com.quant.extract.cse.source.AnnouncementsSource;
import com.quant.extract.cse.source.ListedCompanySource;
import com.quant.extract.cse.source.TradeSummarySource;
import com.quant.extract.destination.OnMemoryDestination;
import com.quant.extract.destination.cloud.GoogleBucketDestination;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Log4j2
public class Main {
    public static void main(String[] args) throws Exception {
        extractTradeSummary();
        extractAnnouncements();
    }

    public static void extractAnnouncements() throws IOException, ExecutionException, InterruptedException {
        CSEHttpReader cseHttpReader = CSEHttpReader.getInstance();
        // Fetch Daily Summary and store it to the google cloud
        ListedCompanySource companySource = new ListedCompanySource(cseHttpReader);
        OnMemoryDestination companies = new OnMemoryDestination();
        // Setup data mover
        DataMover mover = new DataMover(companySource, companies);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> result = executor.submit(mover);

        // Wait for execution to finish
        result.get();

        List<String> symbols = new SymbolsParser().parseSymbols(companies.getFile().getData());
        List<Future> futures = new ArrayList<>();
        for (String symbol : symbols) {
            System.out.println(symbol);
            // Fetch announcements and  store it to the google cloud
            AnnouncementsSource announcementsSource = new AnnouncementsSource(cseHttpReader, symbol);
            Destination announcementsDestination = new GoogleBucketDestination("company_data/" + symbol + "/announcements");

            DataMover announcementsMover = new DataMover(announcementsSource, announcementsDestination);
            executor.submit(announcementsMover).get();
        }

        executor.shutdown();
    }

    public static void extractTradeSummary() throws IOException, ExecutionException, InterruptedException {
        CSEHttpReader cseHttpReader = CSEHttpReader.getInstance();
        // Fetch Daily Summary and store it to the google cloud
        TradeSummarySource tradeSummary =
                new TradeSummarySource(cseHttpReader, new FileNameProvider(new CSEHttpReader()));
        Destination tradeSummaryDestination = new GoogleBucketDestination("eod/trade_summary/");

        // Setup data mover
        DataMover mover = new DataMover(tradeSummary, tradeSummaryDestination);

        // Execute movers
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> result = executor.submit(mover);

        // Wait for execution to finish
        result.get();

        executor.shutdown();
    }
}
