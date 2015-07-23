package lojjing;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class PreprocessorLogErrorTest {

    private String log;

    @Test
    public void testLogStatement_1() {
        thisOne("2015-07-06 20:39:41,785 ERROR [main] aUx.Nul:29 - Updater web server bind tcp port 6789 is not valid. wait 10 seconds");
        becomes("Updater web server bind tcp port 6789 is not valid. wait 10 seconds");
    }

    @Test
    public void testLogStatement_2() {
        thisOne("2015-07-06 20:16:44,004 ERROR [main] Con.AUx:19 - Tcp port is already binded: 6789");
        becomes("Tcp port is already binded: 6789");
    }

    @BeforeMethod
    public void setUp() {
        log = null;
    }

    private void thisOne(String log) {
        this.log = log;
    }

    private void becomes(String expected) {
        Preprocessor target = new Preprocessor();
        assertEquals(target.preprocess(log), expected, "Failed to preprocess log statement");
    }
}
