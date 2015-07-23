package lojjing;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        // line ends normalized by preprocessor
        String[] lines = content.split("\\n");

        if (lines.length < 3)
            return -1;

        int signature = 0;

        // log line removed from preprocessor
        // First exception line is of the form ExceptionClass[: message]
        int colonIndex = lines[0].indexOf(':');
        if (colonIndex > 0) {
            signature += lines[0].substring(0, colonIndex).hashCode();
        } else {
            signature += lines[0].hashCode();
        }

        for (int i = 1; i < lines.length; i++) {
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

        String messageLine = lines[1];

        if (messageLine.equals("java.lang.NullPointerException")) {
            return "NPE at " + abbreviateInvocation(lines[2].substring(4));
        } else {
            return messageLine;
        }
    }

    public String abbreviateInvocation(String str) {
        Pattern obfuscated = Pattern.compile("(?<package>.+?)\\.(?<class>[^\\.]+)\\.(?<method>[a-zA-Z0-9]+)\\(.*\\) \\(return .+\\)\\(a:(?<line>[\\d]+)\\)");
        Matcher m = obfuscated.matcher(str);

        Pattern java = Pattern.compile("(?<package>.+?)\\.(?<class>[^\\.]+)\\.(?<method>[a-zA-Z0-9]+)\\(.*:(?<line>[\\d]+)\\)");
        Matcher m2 = java.matcher(str);

        if (m.matches()) {
            String pkg = m.group("package");
            String klass = m.group("class");
            String method = m.group("method");
            String line = m.group("line");
            return pkg(pkg) + "." + klass + "." + method + ":" + line;
        } else if (m2.matches()) {
            String pkg = m2.group("package");
            String klass = m2.group("class");
            String method = m2.group("method");
            String line = m2.group("line");
            return pkg(pkg) + "." + klass + "." + method + ":" + line;
        } else {
            return str;
        }
    }

    private String pkg(String in) {
        return Arrays.stream(in.split("\\.")).map(n -> Character.toString(n.charAt(0))).collect(Collectors.joining("."));
    }
}
