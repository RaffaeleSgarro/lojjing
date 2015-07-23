package lojjing;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class PreprocessorTest extends TestCase {

    private Preprocessor target;

    @BeforeMethod
    public void setUp() throws Exception {
        target = new Preprocessor();
    }

    @Test
    public void testRemoveCAUSEStanzas() {
        process("CAUSE", "Should remove additional CAUSE stanzas");
    }

    private void process(String resource, String message) {
        String original = resource("/preprocessor/" + resource + "-original.txt");
        String expected = resource("/preprocessor/" + resource + "-processed.txt");
        String actual = target.preprocess(original);
        assertEquals(actual, expected, message);
    }
}