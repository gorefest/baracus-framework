package org.baracus.util;

import java.util.HashMap;

/**
 * Logging configuration to define a package-hierarchical Logging
 *
 * Created by marcus on 02.11.2015.
 */
public class LoggingConfiguration {

    public static final String DEFAULT = "<default>";
    private final HashMap<String, Logger.Level> configuredMap = new HashMap<String, Logger.Level>(); // the package names configured by packages, classes and strings and their log levels
    private final HashMap<String, Logger.Level> packageMap = new HashMap<String, Logger.Level>();    // the real package names determined by rolling up the inheritance tree. this is determined once and then reused ever and ever


    public LoggingConfiguration() {
        configuredMap.put(DEFAULT, Logger.Level.ERROR);
    } // Default is only to log errors

    /**
     * @param pack - the package Name
     * @param level - the Log level
     * @return true, if the package can produce a log on the passed level
     */
    public boolean isLoggable(Package pack, Logger.Level level) {
        if (packageMap.containsKey(pack.getName())) {
            return isLoggable(packageMap.get(pack.getName()), level);
        } else {
            determinePackageFromConfig(pack);
            return isLoggable(packageMap.get(pack.getName()), level);
        }
    }

    /**
     * walk through all configured packages and find
     *    a) an exact match in order to return
     *    b) the closest package in relation to the passed package (criteria : the package prefixing the package with the most package
     *    separators wins)
     *
     * in both cases - best match or direct match, the passed package will be associated with the determined log level.
     *
     * @param pack - the package
     *
     */
    private void determinePackageFromConfig(Package pack) {
        String bestMatch = null;
        Logger.Level bestMatchLevel = null;
        int bestMatchPrecision = 0;
        for (String packageName:configuredMap.keySet()){
            if (packageName.equals(pack.getName())){
                packageMap.put(pack.getName(), configuredMap.get(packageName));
                return;
            } else if (pack.getName().startsWith(packageName)) {
                int precision = packageName.split(".").length;
                if (precision > bestMatchPrecision) {
                    bestMatch = packageName;
                    bestMatchLevel = configuredMap.get(packageName);
                }
            }
        }

        if (bestMatch != null) {
            packageMap.put(pack.getName(), bestMatchLevel);
        } else {
            packageMap.put(pack.getName(), configuredMap.get(DEFAULT));
        }
    }

    /**
     * determines if a log level is sufficient to log
     * @param level - the level configured
     * @param l - the level to ask for
     * @return true, if a log level is matched or included of the configured level
     */
    private boolean isLoggable(Logger.Level level, Logger.Level l) {
        switch (level) {
            case TRACE:
                return true;
            case DEBUG:
                return !l.equals(Logger.Level.TRACE);
            case INFO:
                return !l.equals(Logger.Level.TRACE) && !l.equals(Logger.Level.DEBUG);
            case WARN:
                return l.equals(Logger.Level.WARN) || l.equals(Logger.Level.ERROR);
            case ERROR:
                return l.equals(Logger.Level.ERROR);
        }
        return false;
    }

    /**
     * set the log level for a specific package
     * @param p - the package
     * @param level - the level to be set
     */
    public void setLogLevel(Package p, Logger.Level level) {
        configuredMap.put(p.getName(), level);
    }

    /**
     * set the log level for the package of a specific class
     * @param p - the class
     * @param level - the level to be set
     */
    public void setLogLevel(Class <?> p, Logger.Level level) {
        configuredMap.put(p.getPackage().getName(), level);
    }

    /**
     * set the log level for the package name
     *
     * @param packageName - the package name (e.g. "org", "org.baracus" etc)
     * @param level - the level to be set
     */
    public void setLogLevel(String packageName, Logger.Level level) {
        configuredMap.put(packageName, level);
    }

    /**
     * Modifies the default log level for all packages.
     *
     * @param level
     */
    public void setDefaultLogLevel(Logger.Level level){
        configuredMap.put(DEFAULT, level);
    }

    /**
     * Whenever you modify the Configuration during runtime, you should reset it. This will delete
     * the cached per-package logging level by clearing the package map
     */
    public void reset() {
        packageMap.clear();
    }

}
