package lojjing;

import com.google.gson.Gson;
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
    private final Set<Integer> blacklist = new HashSet<>();

    public Report(File reportsDir) throws Exception {
        this.reportsDir = reportsDir;
        report = new JsonWriter(new PrintWriter(new File(reportsDir, "report.json"), "UTF-8"));
        report.beginArray();
    }

    public void add(Event event) throws Exception {
        Series series = seriesMap.get(event.signature());

        if (series == null) {
            series = new Series(event.signature());
            seriesMap.put(event.signature(), series);
        }

        series.add(event);
    }

    public void flush() throws Exception {

        List<Series> top10 = new ArrayList<>();

        List<Series> all = new ArrayList<>();
        all.addAll(seriesMap.values());
        Collections.sort(all, (s1, s2) -> s2.total() - s1.total());

        int available = 10;

        System.err.println("Remove this!");

        for (Series series : all) {
            if (!blacklist.contains(series.signature)) {
                top10.add(series);
                series.flush();
                available--;
            }

            if (available == 0)
                break;
        }

        report.endArray();
        report.flush();

        copy("metricsgraphics.css");
        copy("metricsgraphics.min.js");

        Map<String, String> params = new HashMap<>();
        params.put("legend", new Gson().toJson(top10.stream().map(series -> "Signature: " + series.signature()).toArray()));
        template("report.html", params);
    }

    private void template(String resource, Map<String, String> params) throws Exception {
        InputStream in = res(resource);
        String template = IOUtils.toString(in, "UTF-8");
        in.close();
        String text = template;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            text = text.replace("<%= " + entry.getKey() + " %>", entry.getValue());
        }
        OutputStream out = new FileOutputStream(new File(directory(), resource));
        IOUtils.write(text, out, "UTF-8");
        out.flush();
        out.close();
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
        return Report.class.getResourceAsStream("/report/" + resource);
    }

    public void blacklist(int signature) {
        blacklist.add(signature);
    }

    private class Series {

        private final int signature;
        private final List<Aggregator> aggregators = new ArrayList<>();

        private Aggregator aggregator;
        private int total = 0;

        private Series(int signature) {
            this.signature = signature;
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
            return signature;
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
