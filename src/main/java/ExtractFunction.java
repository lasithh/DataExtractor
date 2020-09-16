import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.quant.extract.api.DataMover;
import com.quant.extract.api.Destination;
import com.quant.extract.cse.CSEHttpReader;
import com.quant.extract.cse.naming.FileNameProvider;
import com.quant.extract.cse.source.TradeSummarySource;
import com.quant.extract.destination.cloud.GoogleBucketDestination;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExtractFunction implements HttpFunction {
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws InterruptedException, ExecutionException, IOException {
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
