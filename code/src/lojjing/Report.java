package lojjing;

import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Report {

    private static final DateTimeFormatter jsonFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final File reportsDir;
    private final JsonWriter report;
    private final Map<Integer, Series> seriesMap = new HashMap<>();

    public Report(File reportsDir) throws Exception {
        this.reportsDir = reportsDir;
        report = new JsonWriter(new PrintWriter(new File(reportsDir, "report.json"), "UTF-8"));
        report.beginObject();
    }

    public void add(Event event, String trace) throws Exception {
        Series series = seriesMap.get(event.signature());

        if (series == null) {
            series = new Series(event, trace);
            seriesMap.put(event.signature(), series);
        }

        series.add(event);
    }

    public void flush() throws Exception {
        List<Series> all = new ArrayList<>();
        all.addAll(seriesMap.values());
        Collections.sort(all, (s1, s2) -> s2.total() - s1.total());

        report.name("headers");
        report.beginArray();

        for (Series series : all) {
            report.beginObject();
            report.name("message");
            report.value(series.message());
            report.name("signature");
            report.value(series.signature());
            report.name("count");
            report.value(series.total());
            report.name("trace");
            report.value(series.trace());
            report.endObject();
        }

        report.endArray();

        report.name("data");
        report.beginArray();

        for (Series series : all) {
            series.flush();
        }

        report.endArray();

        report.endObject();
        report.flush();

        copy("metricsgraphics.css");
        copy("bootstrap.min.css");
        copy("metricsgraphics.min.js");
        copy("underscore.min.js");
        copy("mustache.min.js");
        copy("main.js");
        copy("index.html");
    }

    private void copy(String resource) throws Exception {
        InputStream in = res(resource);
        OutputStream out = new FileOutputStream(new File(directory(), resource));
        IOUtils.copy(in, out);
        in.close();
        out.flush();
        out.close();
    }

    private InputStream res(String resource) {
        String fullResourceName = "/report/" + resource;
        InputStream in = Report.class.getResourceAsStream(fullResourceName);
        if (in == null)
            throw new RuntimeException("Could not find resource with name " + fullResourceName);
        return in;
    }

    private class Series {

        private final List<Aggregator> aggregators = new ArrayList<>();
        private final Event event;
        private final String trace;

        private Aggregator aggregator;
        private int total = 0;

        private Series(Event event, String trace) {
            this.event = event;
            this.trace = trace;
        }

        public void flush() throws Exception {
            report.beginArray();
            for (Aggregator aggregator : aggregators) {
                aggregator.flush();
            }
            report.endArray();
        }

        public void add(Event event) throws Exception {
            if (aggregator == null || !aggregator.contains(event)) {
                aggregator = new Aggregator(event);
                aggregators.add(aggregator);
            }

            aggregator.add(event);
            total++;
        }

        public int total() {
            return total;
        }

        public int signature() {
            return event.signature();
        }

        public String message() {
            return event.message();
        }

        public String trace() {
            return trace;
        }
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
