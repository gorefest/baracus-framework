package org.baracus.migr8;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 03.08.12
 * Time: 10:03
 * <p/>
 * Automatic sequential database migration. A migration step consists of several DDL commands
 * (CREATE TABLE, DROP TABLE, ALTER TABLE). For every release of your software with database
 * modification you should have one Migrationstep.
 * <p/>
 * You should have an initial implementation creating your database and register it
 * in your openHelper. This initial implementation must not use version 0, which is
 * used by baracus in order to create a configuration table.
 * <p/>
 * Further versions should be added with increasing version numbers.
 *
 * @see org.baracus.dao.BaracusOpenHelper for an example OpenHelper
 * @see ModelVersion000 for an example MigrationStep
 */
public interface MigrationStep {

    void applyVersion(SQLiteDatabase db);

    int getModelVersionNumber();
}
