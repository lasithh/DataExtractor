import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.quant.extract.api.DataMover;
import com.quant.extract.cloud.GoogleBucket;
import com.quant.extract.cse.destination.TradeSummaryDestination;
import com.quant.extract.cse.naming.FileNameProvider;
import com.quant.extract.cse.reader.CSEHttpReader;
import com.quant.extract.cse.source.TradeSummarySource;

import java.io.FileInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExtractFunction implements HttpFunction {
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        GoogleBucket googleBucket = GoogleBucket.builder()
                .bucketName("cse_data")
                .projectId("scenic-setup-274303")
                .credentials(GoogleCredentials.fromStream(
                        new FileInputStream("/Users/hapu/Quant/keys/scenic-setup-274303-f9a5dd9b19d3.json")))
                .build();

        // Fetch Daily Summary and store it to the google cloud
        TradeSummarySource tradeSummary =
                new TradeSummarySource(CSEHttpReader.getInstance(), new FileNameProvider(new CSEHttpReader()));
        TradeSummaryDestination tradeSummaryDestination = new TradeSummaryDestination(googleBucket);
        DataMover moveDailyTradeSummary = new DataMover(tradeSummary, tradeSummaryDestination);

        // Execute movers
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> result = executor.submit(moveDailyTradeSummary);

        result.get();

        executor.shutdown();
    }
}
