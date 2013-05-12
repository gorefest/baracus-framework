package net.mantucon.baracus.migr8;

import android.database.sqlite.SQLiteDatabase;
import net.mantucon.baracus.model.ConfigurationParameter;
import net.mantucon.baracus.util.Logger;

/**
 * Initial Model creating the configuration table to be used by the configuration DAO.
 *
 * This table enables you to store key-value-pairs into your database.
 */
public class ModelVersion000 implements MigrationStep {

    private static final Logger logger = new Logger(ModelVersion000.class);

    @Override
    public void applyVersion(SQLiteDatabase db) {

        String stmt = "CREATE TABLE " + ConfigurationParameter.TABLE_CONFIGURATION
                + "( "+ ConfigurationParameter.idCol.fieldName+" INTEGER PRIMARY KEY"
                + ", "+ ConfigurationParameter.configParamCol.fieldName+ " TEXT"
                + ", "+ ConfigurationParameter.configParamValueCol.fieldName+ " TEXT)";
        logger.info(stmt);
        db.execSQL(stmt);    }

    @Override
    public int getModelVersionNumber() {
        return 0;
    }
}
