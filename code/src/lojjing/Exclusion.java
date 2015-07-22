package lojjing;

import java.util.regex.Pattern;

public class Exclusion {

    private static final Pattern more = Pattern.compile("\\.\\.\\. [\\d]+ more");

    private final String line;

    public Exclusion(String line) {
        this.line = line;
    }

    public boolean exclude() {
        return reflection() || more() || atmosphere();
    }

    private boolean atmosphere() {
        return line.contains("org.atmosphere.cpr.DefaultBroadcasterFactory$BroadcasterCreationException");
    }

    private boolean more() {
        return more.matcher(line).find();
    }

    private boolean reflection() {
        return line.contains("sun.reflect.");
    }

}
