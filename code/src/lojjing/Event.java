package lojjing;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Event {

    private static final DateFormat csvFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String timestamp;
    private final String core;
    private final String content;

    public Event(String timestamp, String core, String content) {
        this.timestamp = timestamp;
        this.core = core;
        this.content = content;
    }

    public long timestamp() {
        try {
            return csvFormat.parse(timestamp).getTime();
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse timestamp " + timestamp, e);
        }
    }

    public String core() {
        return core;
    }

    public String group() {
        return "" + core.charAt(0);
    }

    public int signature() {
        String[] lines = content.split("\\r?\\n");

        if (lines.length < 3)
            return -1;

        int signature = 0;
        // skip log line

        // First exception line is of the form ExceptionClass[: message]
        int colonIndex = lines[1].indexOf(':');
        if (colonIndex > 0) {
            signature += lines[1].substring(0, colonIndex).hashCode();
        } else {
            signature += lines[1].hashCode();
        }

        for (int i = 2; i < lines.length; i++) {
            String line = lines[i];
            Normalizer normalizer = new Normalizer();
            signature += normalizer.normalize(line).trim().hashCode();
        }

        return signature;
    }

    public LocalDate day() {
        return new Date(timestamp()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public String message() {
        String[] lines = content.split("\\r?\\n");

        if (lines.length < 2)
            return "N/A";

        return lines[1];
    }
}
