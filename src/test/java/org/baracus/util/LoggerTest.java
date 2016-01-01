package org.baracus.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by marcus on 04.11.2015.
 */
public class LoggerTest {

    Logger candidate = new Logger(this.getClass());

    static {
        Logger.enableTestLogger();
    }

    @Test
    public void testProcessMessageArgs() throws Exception {
        String probe = candidate.processMessageArgs("$1 times $2 makes $3",4,3,12);
        assertNotNull(probe);
        assertEquals("4 times 3 makes 12", probe);
    }
}