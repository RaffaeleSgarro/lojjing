package lojjing;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class PreprocessorTest extends TestCase {

    @Test
    public void testRemoveCAUSEStanzas() {
        process("CAUSE", "Should remove additional CAUSE stanzas");
    }

    @Test
    public void testRpcRemoteException_001() {
        process("RpcRemoteException-001", "Should remove the wrapping in RpcRemoteException");
    }

    @Test
    public void testUndeclaredThrowableException_001() {
        process("UndeclaredThrowableException-001", "Should remove UndeclaredThrowableException");
    }

    @Test
    public void testIllegalStateException() {
        process("IllegalStateException", "Should be left as is");
    }

    private void process(String resource, String message) {
        String original = resource("/preprocessor/" + resource + "-original.txt");
        String expected = resource("/preprocessor/" + resource + "-processed.txt");
        Preprocessor target = new Preprocessor();
        String actual = target.preprocess(original);
        assertEquals(actual, expected, message);
    }
}