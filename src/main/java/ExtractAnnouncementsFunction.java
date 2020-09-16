import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.quant.extract.api.DataMover;
import com.quant.extract.api.Destination;
import com.quant.extract.cse.CSEHttpReader;
import com.quant.extract.cse.parse.SymbolsParser;
import com.quant.extract.cse.source.AnnouncementsSource;
import com.quant.extract.cse.source.ListedCompanySource;
import com.quant.extract.destination.OnMemoryDestination;
import com.quant.extract.destination.cloud.GoogleBucketDestination;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExtractAnnouncementsFunction implements HttpFunction {
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        CSEHttpReader cseHttpReader = CSEHttpReader.getInstance();
        // Fetch Daily Summary and store it to the google cloud
        ListedCompanySource companySource = new ListedCompanySource(cseHttpReader);
        OnMemoryDestination companies = new OnMemoryDestination();
        // Setup data mover
        DataMover mover = new DataMover(companySource, companies);
        ExecutorService executor = Executors.newFixedThreadPool(3);
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
            futures.add(executor.submit(announcementsMover));

            // Process in batches of 10
            if (futures.size() > 3) {
                for(Future future: futures) {
                    future.get();
                }
                futures.clear();
            }
        }

        executor.shutdown();
    }
}
