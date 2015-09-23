package lojjing;

import org.apache.commons.io.IOUtils;

import java.io.*;

public class Report {

    private final File reportDir;
    private ReportJsonData reportJsonData;

    public Report(File reportDir) {
        this.reportDir = reportDir;
    }

    public File directory() {
        return reportDir;
    }

    public void setUp() throws Exception {
        reportJsonData = new ReportJsonData(new PrintWriter(new File(reportDir, "report.json"), "UTF-8"));

        copy("metricsgraphics.css");
        copy("bootstrap.min.css");
        copy("metricsgraphics.min.js");
        copy("underscore.min.js");
        copy("mustache.min.js");
        copy("main.js");
        copy("index.html");
        copy("README.txt");
    }

    public void flush() throws Exception {
        reportJsonData.flush();
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
        InputStream in = getClass().getResourceAsStream(fullResourceName);
        if (in == null)
            throw new RuntimeException("Could not find resource with name " + fullResourceName);
        return in;
    }

    public void add(Event evt, String content) throws Exception {
        reportJsonData.add(evt, content);
    }

    public int countClusters() {
        return reportJsonData.countClusters();
    }
}
