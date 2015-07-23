package lojjing;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class TestCase {

    protected String resource(String resource) {
        InputStream in = EventSignatureTest.class.getResourceAsStream(resource);

        if (in == null)
            throw new RuntimeException("Could not locate resource " + resource);

        try {
            return IOUtils.toString(in, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Could not load " + resource, e);
        }
    }
}
