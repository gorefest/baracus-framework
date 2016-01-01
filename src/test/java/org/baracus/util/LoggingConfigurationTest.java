package org.baracus.util;

import org.baracus.DummyClassForLogging;
import org.baracus.dao.BaseDao;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by marcus on 02.11.2015.
 */
public class LoggingConfigurationTest {


    LoggingConfiguration candidate;

    static int debugs = 0;
    static int errors = 0;
    static int warns = 0;

    static {
        Logger.enableTestLogger();
        Logger.setLogTarget(new Logger.LogTarget() {
            public void debug(String tag, String msg, Throwable t) {
                debugs++;
            }

            public void debug(String tag, String msg) {
                debugs++;
            }

            public void info(String tag, String msg, Throwable t) {

            }

            public void info(String tag, String msg) {

            }

            public void warn(String tag, String msg, Throwable t) {
                warns++;
            }

            public void warn(String tag, String msg) {
                warns++;
            }

            public void error(String tag, String msg, Throwable t) {
                errors++;
            }

            public void error(String tag, String msg) {
                errors++;
            }

            public void trace(String tag, String msg, Throwable t) {

            }

            public void trace(String tag, String msg) {

            }
        });
    }

    @Before
    public void before() {
        candidate = new LoggingConfiguration();
        errors = 0;
        debugs = 0;
        warns = 0;
    }

    /**
     * Logging and inherita
     *
     *
     *     org              ----> WARN
     *      |
     *      |----.baracus   ----> WARN (inherited)
     *      |
     *      |----.util      ----> DEBUG
     *      |
     *      |----.dao       ----> ERROR
     *
     * @throws Exception
     */
    @Test
    public void testIsLoggable() throws Exception {
        candidate.setLogLevel(org.baracus.dao.BaseDao.class.getPackage(), Logger.Level.ERROR);
        candidate.setLogLevel(LoggingConfiguration.class, Logger.Level.DEBUG);
//        candidate.setLogLevel(DummyClassForLogging.class.getPackage(), Logger.Level.WARN);
        candidate.setLogLevel("org.baracus", Logger.Level.WARN);

        Logger.setLoggingConfiguration(candidate);

        Logger daoLogger = new Logger(BaseDao.class);
        Logger utilLogger = new Logger(LoggingConfiguration.class);
        Logger dummyLogger = new Logger(DummyClassForLogging.class);

        daoLogger.info("FOO");
        assertEquals(1, debugs);    // one debug output done by Constructor call!

        utilLogger.debug("BAR");
        assertEquals(2, debugs);

        utilLogger.error("FOOO");
        assertEquals(1, errors);    // THIS MUST FIRE ALWAYS

        daoLogger.error("BAAR");
        assertEquals(2, errors);

        dummyLogger.debug("FOOBART"); // The inherited level for this package "WARN", nothing must happen here
        assertEquals(2, debugs);

        dummyLogger.warn("WARNING"); // This MUST increase the warn counter
        assertEquals(1, warns);

    }

    @Test
    public void testSetLogLevel() throws Exception {

    }
}