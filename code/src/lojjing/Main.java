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

        if (reportDirectory.exists()) {
            FileUtils.forceDelete(reportDirectory);
        }

        FileUtils.forceMkdir(reportDirectory);

        Reader in = new InputStreamReader(new FileInputStream(filename), "UTF-8");
        CSVParser parser = CSVFormat.newFormat(',')
                .withQuote('"')
                .withQuoteMode(QuoteMode.MINIMAL)
                .withHeader()
                .parse(in);

        Report report = new Report(reportDirectory);

        report.blacklist(-1);
        report.blacklist(-1261522583);
        report.blacklist(-1404517635);

        for (CSVRecord csv : parser) {
            Event evt = new Event(csv.get("log_timestamp"), csv.get("core_id"), csv.get("content"));
            report.add(evt);
        }

        report.flush();

        in.close();

        log.info("Report available at " + reportDirectory.getAbsolutePath());
    }
}
