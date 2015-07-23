package lojjing;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) throws Exception {

        File reportDirectory = new File("report");

        if (args.length != 1) {
            String url = "https://icpteam.slack.com/files/raffaelesgarro/F07V9P0HY/app_log_errors_2015-07-07_2015-07-21_utf8.csv.bz2";
            throw new RuntimeException("You need to supply the CSV filename as the first argument. Download at " + url);
        }

        String filename = args[0];

        log.info("CSV input file is {}", filename);

        if (reportDirectory.exists()) {
            FileUtils.forceDelete(reportDirectory);
        }

        FileUtils.forceMkdir(reportDirectory);

        log.info("Working directory is {}", reportDirectory.getAbsolutePath());

        Reader in = new InputStreamReader(new FileInputStream(filename), "UTF-8");
        CSVParser parser = CSVFormat.newFormat(',')
                .withQuote('"')
                .withQuoteMode(QuoteMode.MINIMAL)
                .withHeader()
                .parse(in);

        Report report = new Report(reportDirectory);

        int exceptions = 0;

        long start = System.currentTimeMillis();

        log.info("Start processing events");

        for (CSVRecord csv : parser) {
            Preprocessor preprocessor = new Preprocessor();
            String content = preprocessor.preprocess(csv.get("content"));
            Event evt = new Event(csv.get("log_timestamp"), csv.get("core_id"), content);
            report.add(evt, content);
            exceptions++;
        }

        report.flush();

        in.close();

        long runningTimeSeconds = (System.currentTimeMillis() - start) / 1000L;
        double average = 0;
        if (exceptions != 0)
            average = (double) exceptions / runningTimeSeconds;

        log.info("Processed {} events in {} seconds, averaging {} events per second", exceptions, runningTimeSeconds, average);
        log.info("Found {} clusters", report.countClusters());
        log.info("Report available at {}. See the README", reportDirectory.getAbsolutePath());
    }
}
