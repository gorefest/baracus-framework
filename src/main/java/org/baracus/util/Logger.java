package org.baracus.util;

import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: mnt
 * Date: 07.02.12
 *
 * logger component providing a log4j logging combined with a slf4j vararg styled logging function
 *
 * Usage :
 *
 * {@code
 *  ...
 *  Logging configuration = new LoggingConfiguration();
 *  configuration.setLogLevel("org", Logger.Level.DEBUG);
 *  Logger.setLoggingConfiguration(configuration);
 *  Logger.debug("$1 times $2 makes $3", 3,4,12);
 *
 * }
 *
 */
public class Logger {

    private static String TAG = "PLEASE SET TAG";

    private final String loggerId;

    enum Level {
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE,
    }

    private static LoggingConfiguration loggingConfiguration = new LoggingConfiguration();

    private final Class<?> targetClass;

    private static LogTarget logTarget = new AndroidLogger();

    public static void enableTestLogger() {
        logTarget = new SysOutLogger();
    }

    public static interface LogTarget {
        public void debug(String tag, String msg, Throwable t);

        public void debug(String tag, String msg);

        public void info(String tag, String msg, Throwable t);

        public void info(String tag, String msg);

        public void warn(String tag, String msg, Throwable t);

        public void warn(String tag, String msg);

        public void error(String tag, String msg, Throwable t);

        public void error(String tag, String msg);

        public void trace(String tag, String msg, Throwable t);

        public void trace(String tag, String msg);
    }

    public static class AndroidLogger implements LogTarget {
        public void trace(String tag, String msg) {
            Log.v(tag, msg);
        }

        public void trace(String tag, String msg, Throwable tr) {
            Log.v(tag, msg, tr);
        }

        public void warn(String tag, String msg, Throwable tr) {
            Log.w(tag, msg, tr);
        }

        public void warn(String tag, Throwable tr) {
            Log.w(tag, tr);
        }

        public void debug(String tag, String msg, Throwable tr) {
            Log.d(tag, msg, tr);
        }

        public void info(String tag, String msg, Throwable tr) {
            Log.i(tag, msg, tr);
        }

        public void info(String tag, String msg) {
            Log.i(tag, msg);
        }

        public void warn(String tag, String msg) {
            Log.w(tag, msg);
        }

        public void error(String tag, String msg) {
            Log.e(tag, msg);
        }

        public void error(String tag, String msg, Throwable tr) {
            Log.e(tag, msg, tr);
        }

        public void debug(String tag, String msg) {
            Log.d(tag, msg);
        }
    }

    public static class SysOutLogger implements LogTarget {

        @SuppressWarnings("PMD")
        public void dump(String tag, String msg, Throwable t) {
            System.out.println(tag + " " + msg);
            if (t != null) {
                t.printStackTrace();
            }
        }

        public void debug(String tag, String msg, Throwable t) {
            dump(tag, msg, t);
        }

        public void debug(String tag, String msg) {
            dump(tag, msg, null);
        }

        public void info(String tag, String msg, Throwable t) {
            dump(tag, msg, t);
        }

        public void info(String tag, String msg) {
            dump(tag, msg, null);
        }

        public void warn(String tag, String msg, Throwable t) {
            dump(tag, msg, t);
        }

        public void warn(String tag, String msg) {
            dump(tag, msg, null);
        }

        public void error(String tag, String msg, Throwable t) {
            dump(tag, msg, t);
        }

        public void error(String tag, String msg) {
            dump(tag, msg, null);
        }

        public void trace(String tag, String msg, Throwable t) {
            dump(tag, msg, t);
        }

        public void trace(String tag, String msg) {
            dump(tag, msg, null);
        }
    }


    public static void setLogTarget(LogTarget logTarget) {
        Logger.logTarget = logTarget;
    }

    final String processMessageArgs(final String message, final Object... args) {
        String result = message;
        for (int i = 0; i < args.length; ++i) {
            result = result.replace("$" + (i + 1), String.valueOf(args[i]));
        }
        return result;
    }

    /**
     * Constructor. Pass class to log
     *
     * @param classToLog
     */
    public Logger(Class<?> classToLog) {
        this.loggerId = classToLog.getSimpleName();
        this.targetClass = classToLog;
        log(Level.DEBUG, loggerId + " was registered");
    }

    /**
     * set a logging configuration for the logger. typically, this is going to be done
     * when the application is started (e.g. when the application context is built)
     *
     * @param loggingConfiguration - the logging configuration
     */
    public static void setLoggingConfiguration(LoggingConfiguration loggingConfiguration) {
        Logger.loggingConfiguration = loggingConfiguration;
    }

    /**
     * log debug message. use $1 ... $n to declare the parameters
     *
     * e.g. "$1 elements have been processed"
     *
     * @param message
     * @param args
     */
    public void debug(final String message, final Object... args) {
        if (!isLoggable(Level.DEBUG)) {
            return;
        }
        log(Level.DEBUG, loggerId + " " + processMessageArgs(message, args));
    }

    public void error(final String message, final Object... args) {
        if (!isLoggable(Level.ERROR)) {
            return;
        }
        log(Level.ERROR, loggerId + " " + processMessageArgs(message, args));
    }

    public void fatal(final String message, final Object... args) {
        if (!isLoggable(Level.ERROR)) {
            return;
        }
        log(Level.ERROR, loggerId + " " + processMessageArgs(message, args));
    }

    public void info(final String message, final Object... args) {
        if (!isLoggable(Level.INFO)) {
            return;
        }
        log(Level.INFO, loggerId + " " + processMessageArgs(message, args));
    }

    public void trace(final String message, final Object... args) {
        if (!isLoggable(Level.TRACE)) {
            return;
        }
        log(Level.TRACE, loggerId + " " + processMessageArgs(message, args));
    }

    public void warn(final String message, final Object... args) {
        if (!isLoggable(Level.WARN)) {
            return;
        }
        log(Level.WARN, loggerId + " " + processMessageArgs(message, args));
    }

    public void debug(final String message) {
        log(Level.DEBUG, loggerId + " " + message);
    }

    public void error(final String message) {
        log(Level.ERROR, loggerId + " " + message);
    }

    public void fatal(final String message) {
        log(Level.ERROR, loggerId + " " + message);
    }

    public void info(final String message) {
        log(Level.INFO, loggerId + " " + message);
    }

    public void trace(final String message) {
        log(Level.TRACE, loggerId + " " + message);
    }

    public void warn(final String message) {
        log(Level.WARN, loggerId + " " + message);
    }


    public void warn(final String message, Throwable e) {
        log(Level.WARN, loggerId + " " + message, e);
    }

    public void trace(final String message, Throwable e) {
        log(Level.TRACE, loggerId + " " + message, e);
    }

    public void info(final String message, Throwable e) {
        log(Level.INFO, loggerId + " " + message, e);
    }

    public void fatal(final String message, Throwable e) {
        log(Level.ERROR, loggerId + " " + message, e);
    }

    public void error(final String message, Throwable e) {
        log(Level.ERROR, loggerId + " " + message, e);
    }

    public void debug(final String message, Throwable e) {
        log(Level.DEBUG, loggerId + " " + message, e);
    }


    private boolean isLoggable(Level l) {
        return loggingConfiguration.isLoggable(this.targetClass.getPackage(), l);
    }


    private void logMessage(String message, Level level2log, Throwable t) {
        if (message == null) {
            message = "null";
        }
        switch (level2log) {
            case TRACE:
                if (t != null) {
                    logTarget.trace(TAG, message, t);
                } else {
                    logTarget.trace(TAG, message);
                }
                break;
            case DEBUG:
                if (t != null) {
                    logTarget.debug(TAG, message, t);
                } else {
                    logTarget.debug(TAG, message);
                }
                break;
            case INFO:
                if (t != null) {
                    logTarget.info(TAG, message, t);
                } else {
                    logTarget.info(TAG, message);
                }
                break;
            case WARN:
                if (t != null) {
                    logTarget.warn(TAG, message, t);
                } else {
                    logTarget.warn(TAG, message);
                }
                break;
            case ERROR:
                if (t != null) {
                    logTarget.error(TAG, message, t);
                } else {
                    logTarget.error(TAG, message);
                }
                break;
        }
    }

    private void log(Level level, String message, Throwable e) {
        if (isLoggable(level)) {
            logMessage(message, level, e);
        }
    }

    private void log(Level level, String message) {
        if (isLoggable(level)) {
            logMessage(message, level, null);
        }
    }

    public static void setTag(String TAG) {
        Logger.TAG = TAG;
    }
}
