package lojjing;

import java.util.regex.Pattern;

public class Normalizer {

    private static final Pattern more = Pattern.compile("\\.\\.\\. [\\d]+ more");

    public String normalize(String line) {
        if (line.contains("sun.reflect.")) {
            return "";
        } else if (more.matcher(line).find()) {
            return "... NN more";
        } else if (line.contains("org.atmosphere.cpr.DefaultBroadcasterFactory$BroadcasterCreationException")) {
            return "DefaultBroadcasterFactory$BroadcasterCreationException";
        } else if (line.contains("java.util.concurrent.RejectedExecutionException")) {
            return "java.util.concurrent.RejectedExecutionException";
        } else if (line.contains("com.sun.proxy")) {
            return "com.sun.proxy.*";
        } else if (line.contains("Messaggio dal SISS: Il dato \"codicePrestazione\"") && line.contains(" non e' valido.")) {
            return "Prestazione obsoleta";
        } else if (line.startsWith("Caused by: com.vaadin.data.util.converter.Converter$ConversionException") && line.endsWith("to java.lang.Integer")) {
            return "Caused by: com.vaadin.data.util.converter.Converter$ConversionException: Could not convert 'STR' to java.lang.Integer";
        } else if (line.contains("java.net.DualStackPlainSocketImpl")
                || line.contains("java.net.TwoStacksPlainSocketImpl")) {
            return "";
        } else {
            return line;
        }
    }

}
