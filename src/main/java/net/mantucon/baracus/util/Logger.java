package net.mantucon.baracus.util;

import android.util.Log;

/**
 * Created by IntelliJ IDEA.
 * User: mnt
 * Date: 07.02.12
 * Time: 19:21
 * very basic logger providin a log4j style combined with a slf4j vararg styled logging function
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

    private static Level level = Level.DEBUG;

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

        @Override
        public void debug(String tag, String msg, Throwable t) {
            dump(tag, msg, t);
        }

        @Override
        public void debug(String tag, String msg) {
            dump(tag, msg, null);
        }

        @Override
        public void info(String tag, String msg, Throwable t) {
            dump(tag, msg, t);
        }

        @Override
        public void info(String tag, String msg) {
            dump(tag, msg, null);
        }

        @Override
        public void warn(String tag, String msg, Throwable t) {
            dump(tag, msg, t);
        }

        @Override
        public void warn(String tag, String msg) {
            dump(tag, msg, null);
        }

        @Override
        public void error(String tag, String msg, Throwable t) {
            dump(tag, msg, t);
        }

        @Override
        public void error(String tag, String msg) {
            dump(tag, msg, null);
        }

        @Override
        public void trace(String tag, String msg, Throwable t) {
            dump(tag, msg, t);
        }

        @Override
        public void trace(String tag, String msg) {
            dump(tag, msg, null);
        }
    }


    private final String processMessageArgs(final String message, final Object... args) {
        String result = message;
        for (int i = 0; i < args.length; ++i) {
            result = result.replace("$" + (i + 1), String.valueOf(args[i]));
        }
        return result;
    }

    public Logger(String loggerId) {
        this.loggerId = loggerId;
        log(Level.DEBUG, loggerId + " was registered");
    }

    public Logger(Class<?> classToLog) {
        this(classToLog.getSimpleName());
    }

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
        switch (level) {
            case TRACE:
                return true;
            case DEBUG:
                return !l.equals(Level.TRACE);
            case INFO:
                return !l.equals(Level.TRACE) && !l.equals(Level.DEBUG);
            case WARN:
                return l.equals(Level.WARN) || l.equals(Level.ERROR);
            case ERROR:
                return l.equals(Level.ERROR);
        }
        return false;
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
