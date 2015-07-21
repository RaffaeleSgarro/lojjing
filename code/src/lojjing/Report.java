package lojjing;

import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Report {

    private static final DateTimeFormatter jsonFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final File reportsDir;
    private final JsonWriter report;
    private final Map<Integer, Aggregator> aggregators = new HashMap<>();

    public Report(File reportsDir) throws Exception {
        this.reportsDir = reportsDir;
        report = new JsonWriter(new PrintWriter(new File(reportsDir, "report.json"), "UTF-8"));
        report.beginArray();
    }

    public void add(Event event) throws Exception {
        Aggregator aggregator = aggregators.get(event.signature());

        if (aggregator == null) {
            aggregator = new Aggregator(event);
            aggregators.put(event.signature(), aggregator);
        }

        if (!aggregator.contains(event)) {
            aggregator.flush();
            aggregator = new Aggregator(event);
            aggregators.put(event.signature(), aggregator);
        }

        aggregator.add(event);
    }

    public void flush() throws Exception {
        for (Aggregator aggregator : aggregators.values())
            aggregator.flush();

        report.endArray();
        report.flush();

        copy("metricsgraphics.css");
        copy("metricsgraphics.min.js");
        copy("report.html");
    }

    private void copy(String resource) throws Exception {
        InputStream in = Report.class.getResourceAsStream("/report/" + resource);
        OutputStream out = new FileOutputStream(new File(directory(), resource));
        IOUtils.copy(in, out);
        in.close();
        out.flush();
        out.close();
    }

    private class Aggregator {

        private final LocalDate day;
        private final int signature;

        private int counter = 0;

        public Aggregator(Event event) {
            day = event.day();
            signature = event.signature();
        }

        public void flush() throws Exception {
            report.beginObject();
            report.name("date").value(day.format(jsonFormatter));
            report.name("value").value(counter);
            report.endObject();
        }

        public void add(Event event) {
            if (event.signature() != signature)
                throw new RuntimeException("Not the same signature " + signature + ", " + event.signature());
            counter++;
        }

        public boolean contains(Event event) {
            return day.equals(event.day());
        }
    }

    public File directory() {
        return reportsDir;
    }
}
