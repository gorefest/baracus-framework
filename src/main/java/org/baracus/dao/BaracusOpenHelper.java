package org.baracus.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.baracus.lifecycle.Destroyable;
import org.baracus.migr8.MigrationStep;
import org.baracus.migr8.ModelVersion000;
import org.baracus.util.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * OpenHelper base class used to manage Your database within baracus.   <br>
 * Automatic database management includes context & connection management plus
 * database creation and automatic migration support.<br>
 *
 * You must implement this class providing a target version, a database name and the
 * android context. Also, You must add it to the beans using Your ApplicationContext!
 *
 * </blockquote>
 * </pre>
 * <p/>
 * <hr>
 * Example:
 * <p/>
 * <pre>
 * {@code
 * public class OpenHelper extends BaracusOpenHelper {
 *
 * public static final String DATABASE_NAME = "my_application.db";      // database name
 * public static final int TARGET_DATABASE_VERSION = 110;             // target versiion
 *
 * static  {
 * upgradeSteps.put(100, new ModelVersion100()); // one upgrade since initial version
 * // here you can add all migration steps. please
 * // be sure to apply increasing numbers.
 * }
 *
 *
 * public OpenHelper(Context mContext) {
 * super(mContext, DATABASE_NAME, TARGET_DATABASE_VERSION);
 * }
 *
 * public MigrationStep getInitialModel() {
 * return new ModelVersion100();
 * }
 * }
 * </pre>
 * <p/>
 * Example Registration :
 * <pre>
 * {@code
 *
 * public class ApplicationContext extends BaracusApplicationContext{
 * ...
 * static {
 * ...
 * registerBeanClass(OpenHelper.class);
 * ...
 * }
 *
 * }
 * }
 * </pre>
 */
public abstract class BaracusOpenHelper extends SQLiteOpenHelper implements Destroyable {

    /**
     * Exception preventing us from reusing a version number. This can to severe damage on Your
     * database. Do not rebind Migration objects
     */
    public static class VersionNumberAlreadyBoundException extends IllegalArgumentException {
        public VersionNumberAlreadyBoundException(String s) {
            super(s);
        }
    }

    private final Logger logger = new Logger(getClass());   // Logger

    protected static Map<Integer, MigrationStep> upgradeSteps = new HashMap<Integer, MigrationStep>();  // Here are the upgrade steps

    private final String databaseName;        // set this value via constructor in order to define the db name

    /*  set this value via constructor in order to set the target version
        we are going to iterate all bound version migrators from initialVersion
        until target version
     */
    private final int targetDatabaseVersion;

    static {
        upgradeSteps.put(110, new ModelVersion000());
    }

    /**
     * Open Helper for the android database
     *
     * @param mContext              - the android context
     * @param databaseName          - the database name of your app
     * @param targetDatabaseVersion the target version. automatic migration will be done until this version
     */
    protected BaracusOpenHelper(Context mContext, String databaseName, int targetDatabaseVersion) {
        super(mContext, databaseName, null, targetDatabaseVersion);
        this.databaseName = databaseName;
        this.targetDatabaseVersion = targetDatabaseVersion;
    }

    /**
     * Creation function . do not edit. do not override. it takes care of the creation of your
     * database
     *
     * @param db
     */
    @Override
    public final void onCreate(SQLiteDatabase db) {

        MigrationStep initialModel = getInitialModel();
        initialModel.applyVersion(db);

        onUpgrade(db, initialModel.getModelVersionNumber(), targetDatabaseVersion);

    }


    public MigrationStep getInitialModel() {
        return new ModelVersion000();
    }

    /**
     * use this method in order to add further migration steps to your db.
     * each release with database changes should bring a migration step
     * <p/>
     * you should not change the prior defined steps any more. this will
     * make your database safe for creation and migration on
     * a brand new target system.
     *
     * @param step - the migration step
     */
    protected static final void addMigrationStep(MigrationStep step) {
        if (upgradeSteps.containsKey(step.getModelVersionNumber())) {
            throw new VersionNumberAlreadyBoundException("The version number " + step.getModelVersionNumber() + " is already present in migration object list!");
        }
        upgradeSteps.put(step.getModelVersionNumber(), step);
    }

    /**
     * Do not modify this function. Do not override. It takes care of your database
     * migration.
     *
     * @param db         - database ref
     * @param oldVersion - the current version
     * @param newVersion - the version to migrate to
     */
    @Override
    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion + 1; i <= newVersion; ++i) {
            MigrationStep step = upgradeSteps.get(i);
            if (step != null) {
                logger.info("Applying Version $1", i);
                step.applyVersion(db);
            } else {
                logger.debug("No Version for $1", i);
            }
        }
    }

    public void onDestroy() {

        try {
            this.close();
        } catch (Exception e) {
            logger.error("On destroy failed", e);
        }

    }

    public String getDatabaseName() {
        return databaseName;
    }


}