package lojjing;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class ExclusionTest {

    @Test
    public void testMore() throws Exception {
        shouldExclude("\t... 15 more");
    }

    @Test
    public void testReflection() throws Exception {
        shouldExclude("\tat sun.reflect.GeneratedMethodAccessor13.invoke(Unknown Source)");
    }

    @Test
    public void testExcludeAtmosphere() throws Exception {
        shouldExclude("Caused by: org.atmosphere.cpr.DefaultBroadcasterFactory$BroadcasterCreationException: java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@1f5e459 rejected from java.util.concurrent.ScheduledThreadPoolExecutor@62b0e3[Shutting down, pool size = 2, active threads = 0, queued tasks = 0, completed tasks = 1953]\n");
    }

    @Test
    public void testExcludeRejectedExecutionException() throws Exception {
        shouldExclude("Caused by: java.util.concurrent.RejectedExecutionException: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@31b94d rejected from java.util.concurrent.ScheduledThreadPoolExecutor@7c7582[Shutting down, pool size = 4, active threads = 0, queued tasks = 0, completed tasks = 6837]");
    }

    @Test
    public void testExcludeSISSObsoleteItem() throws Exception {
        shouldExclude("Messaggio dal SISS: Il dato \"codicePrestazione\" : \"0090495.01\" non e' valido.");
    }

    private void shouldExclude(String line) {
        Exclusion target = new Exclusion(line);
        assertTrue(target.exclude(), "Line [" + line + "] should have been excluded");
    }
}