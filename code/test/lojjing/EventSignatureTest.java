package lojjing;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

public class EventSignatureTest {

    private String group;

    @Test
    public void SOAPExceptionImpl_001() throws Exception {
        setGroup("SOAPExceptionImpl/001");
        assertSameSignature("001", "002");
    }

    @Test
    public void SOAPExceptionImpl_002() throws Exception {
        setGroup("SOAPExceptionImpl/002");
        assertSameSignature("001", "002", "003", "004");
    }

    @Test
    public void RuntimeException_Push_failed() throws Exception {
        setGroup("RuntimeException_Push_failed/001");
        assertSameSignature("001", "002", "003");
    }

    @Test
    public void UndeclaredThrowableException_001() throws Exception {
        setGroup("UndeclaredThrowableException/001");
        assertSameSignature("001", "002");
    }

    @Test
    public void ProceduraSISSNonRiuscita_001() throws Exception {
        setGroup("ProceduraSISSNonRiuscita/001");
        assertSameSignature("001", "002");
    }

    @BeforeMethod
    public void setUp() throws Exception {
        group = null;
    }

    @AfterMethod
    public void tearDown() throws Exception {
        group = null;
    }

    private void setGroup(String group) {
        this.group = group;
    }

    private void assertSameSignature(String... ids) throws Exception {
        if (group == null)
            throw new RuntimeException("Did you forget to call setGroup(String)?");

        if (ids.length < 2)
            throw new RuntimeException("You should supply at least 2 instances of the same trace");

        final String normalized = ids[0];
        final int normalizedHash = hash(normalized);

        for (int i = 1; i < ids.length; i++) {
            String current = ids[i];
            int currentHash = hash(current);
            assertEquals(currentHash, normalizedHash, current + " doesn't have same signature as " + normalized);
        }
    }

    private int hash(String id) throws IOException {
        String resource = "/signatures/" + group + "/" + id + ".txt";
        InputStream in = EventSignatureTest.class.getResourceAsStream(resource);

        if (in == null)
            throw new RuntimeException("Could not locate resource " + resource);

        Event event = new Event("", "", IOUtils.toString(in, "UTF-8"));
        return event.signature();
    }
}
