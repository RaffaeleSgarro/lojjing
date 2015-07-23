package lojjing;

public class Preprocessor {

    public String preprocess(String content) {
        String[] lines = content.split("\\r?\\n");

        if (lines.length < 3)
            return content;

        StringBuilder str = new StringBuilder();

        // skip log line
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (!line.isEmpty()) {
                if (i > 1) {
                    str.append("\n");
                }
                str.append(line);
            } else {
                break;
            }
        }

        return str.toString();
    }
}
