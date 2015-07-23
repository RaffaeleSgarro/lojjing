package lojjing;

public class Preprocessor {

    public String preprocess(String content) {
        String[] lines = content.split("\\r?\\n");

        if (lines.length < 3)
            return content;

        ExceptionModel root = null;
        ExceptionModel exceptionModel = null;

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty())
                break;

            if (exceptionModel == null) {
                root = new ExceptionModel(line);
                exceptionModel = root;
                continue;
            }

            if (line.startsWith("Caused by: ")) {
                ExceptionModel next = new ExceptionModel(line.substring("Caused by: ".length()));
                exceptionModel.causedBy(next);
                exceptionModel = next;
                continue;
            }

            exceptionModel.frame(line);
        }

        if (root == null)
            return content;

        // Remove wrappers
        ExceptionModel filtered = root.filter();

        return filtered != null ? filtered.toString() : content;
    }

    private static class ExceptionModel {

        private final String title;
        private final StringBuilder frames = new StringBuilder();

        private ExceptionModel cause;

        private ExceptionModel(String title) {
            this.title = title;
        }

        public ExceptionModel(ExceptionModel current) {
            this.title = current.title;
            this.frames.append(current.frames);
        }

        public void frame(String frameElement) {
            if (frameElement == null || frameElement.isEmpty())
                throw new RuntimeException("Frame element can't be empty");

            if (frames.length() != 0) {
                frames.append("\n");
            }

            frames.append(frameElement);
        }

        public void causedBy(ExceptionModel cause) {
            if (cause == null)
                throw new NullPointerException("Cause can't be set with a null argument");

            if (cause == this)
                throw new RuntimeException("An exception can't cause itself!");

            this.cause = cause;
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();

            append(str);

            ExceptionModel e = this;

            while (e.cause != null) {
                str.append("\n");
                str.append("Caused by: ");
                e.cause.append(str);
                e = e.cause;
            }

            return str.toString();

        }

        private void append(StringBuilder str) {
            str.append(title);
            str.append("\n");
            str.append(frames.toString());
        }

        public boolean isNot(String exception) {
            return !title.startsWith(exception);
        }

        // - rpc2.commons.RpcRemoteException
        // - java.lang.reflect.UndeclaredThrowableException
        public ExceptionModel filter() {
            ExceptionModel currentIn = this;

            ExceptionModel root = null;
            ExceptionModel currentOut = null;

            while (currentIn != null) {
                if (currentIn.isNot("rpc2.commons.RpcRemoteException")
                        && currentIn.isNot("java.lang.reflect.UndeclaredThrowableException")) {
                    ExceptionModel next = new ExceptionModel(currentIn);

                    if (root == null) {
                        root = next;
                        currentOut = root;
                    } else {
                        currentOut.causedBy(next);
                        currentOut = next;
                    }
                }

                currentIn = currentIn.cause();
            }

            return root;
        }

        private ExceptionModel cause() {
            return cause;
        }
    }
}
