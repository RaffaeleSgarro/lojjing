package lojjing;

import java.util.regex.Pattern;

public class Exclusion {

    private static final Pattern more = Pattern.compile("\\.\\.\\. [\\d]+ more");

    private final String line;

    public Exclusion(String line) {
        this.line = line;
    }

    public boolean exclude() {
        return reflection()
                || more()
                || atmosphere()
                || rejectedExecutionException()
                || proxy()
                || sissPreamble()
                || sissObsoleteItem();
    }

    private boolean sissPreamble() {
        return line.contains("Procedura SISS non riuscita");
    }

    private boolean sissObsoleteItem() {
        return line.contains("Messaggio dal SISS: Il dato \"codicePrestazione\"") && line.contains(" non e' valido.");
    }

    private boolean proxy() {
        return line.contains("com.sun.proxy");
    }

    private boolean rejectedExecutionException() {
        return line.contains("java.util.concurrent.RejectedExecutionException");
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
