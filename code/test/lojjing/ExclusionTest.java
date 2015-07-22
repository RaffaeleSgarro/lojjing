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

    private void shouldExclude(String line) {
        Exclusion target = new Exclusion(line);
        assertTrue(target.exclude(), "Line [" + line + "] should have been excluded");
    }
}