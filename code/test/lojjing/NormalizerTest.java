package lojjing;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class NormalizerTest {

    private Normalizer target;
    private String thisLine;

    @Test
    public void testMore() throws Exception {
        thisOne("\t... 15 more");
        matches("\t... 16 more");
    }

    @Test
    public void testReflection() throws Exception {
        thisOne("\tat sun.reflect.GeneratedMethodAccessor13.invoke(Unknown Source)");
        matches("\tat sun.reflect.GeneratedMethodAccessor17.invoke(Unknown Source)");
    }

    @Test
    public void testExcludeAtmosphere() throws Exception {
        thisOne("Caused by: org.atmosphere.cpr.DefaultBroadcasterFactory$BroadcasterCreationException: java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@1f5e459 rejected from java.util.concurrent.ScheduledThreadPoolExecutor@62b0e3[Shutting down, pool size = 2, active threads = 0, queued tasks = 0, completed tasks = 1953]\n");
        matches("Caused by: org.atmosphere.cpr.DefaultBroadcasterFactory$BroadcasterCreationException: java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@1f5e987 rejected from java.util.concurrent.ScheduledThreadPoolExecutor@62b0e3[Shutting down, pool size = 2, active threads = 0, queued tasks = 0, completed tasks = 1953]\n");
    }

    @Test
    public void testExcludeRejectedExecutionException() throws Exception {
        thisOne("Caused by: java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@31b94d rejected from java.util.concurrent.ScheduledThreadPoolExecutor@7c7582[Shutting down, pool size = 4, active threads = 0, queued tasks = 0, completed tasks = 6837]");
        matches("Caused by: java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@31b94d rejected from java.util.concurrent.ScheduledThreadPoolExecutor@7c7782[Shutting down, pool size = 4, active threads = 0, queued tasks = 0, completed tasks = 6837]");
    }

    @Test
    public void testExcludeSISSObsoleteItem() throws Exception {
        thisOne("Messaggio dal SISS: Il dato \"codicePrestazione\" : \"0090495.01\" non e' valido.");
        matches("Messaggio dal SISS: Il dato \"codicePrestazione\" : \"UB5\" non e' valido.");
    }

    @BeforeMethod
    public void setUp() throws Exception {
        target = new Normalizer();
        thisLine = null;
    }

    private void thisOne(String thisLine) {
        this.thisLine = thisLine;
    }

    private void matches(String thatLine) {
        assertEquals(target.normalize(thisLine), target.normalize(thatLine), "Line [" + thatLine + "] Line 2 [ " + thisLine + " ]");
    }
}