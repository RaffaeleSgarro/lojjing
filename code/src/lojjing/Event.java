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
        return 0;
    }

    public LocalDate day() {
        return new Date(timestamp()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
