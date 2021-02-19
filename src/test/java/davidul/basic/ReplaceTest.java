package davidul.basic;

import junit.framework.TestCase;
import org.junit.Test;

public class ReplaceTest extends TestCase {

    @Test
    public void testReplace() {
        final Replace replace = new Replace();
        replace.replace("localhost", "ID::1");
    }
}