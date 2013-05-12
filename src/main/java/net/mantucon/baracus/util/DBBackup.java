package net.mantucon.baracus.util;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import net.mantucon.baracus.context.BaracusApplicationContext;
import net.mantucon.baracus.dao.BaracusOpenHelper;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Date;

/**
 * Make a database backup or restore a database in a running application. If you want to make
 * use of this feature, you must fully rely on BaracusApplicationContext managed database handling
 * or be VERY sure that you have all handles under control!
 */
public class DBBackup {


    public static class BackupResult {
        private final long size;
        private final String backupDbName;
        private final boolean successful;
        private String reason;

        public BackupResult(long size, String backupDbName) {
            this.size = size;
            this.backupDbName = backupDbName;
            this.successful = true;
        }

        public BackupResult(long size, String backupDbName, String reason) {
            this.size = size;
            this.backupDbName = backupDbName;
            this.successful = false;
            this.reason = reason;
        }

        public long getSize() {
            return size;
        }

        public String getBackupDbName() {
            return backupDbName;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public String getReason() {
            return reason;
        }
    }

    private DBBackup() {
        // Utility class constructor
    }

    public static BackupResult performDatabaseBackup() {

        final String currentDBPath = BaracusApplicationContext.getDatabasePath();
        BaracusOpenHelper openHelper = BaracusApplicationContext.connectOpenHelper();

        // final String currentDBPath = "//data//net/mantucon/databases/"+OpenHelper.DATABASE_NAME;
        final String backupDBPath = DateUtil.toReverseDate(new Date()) + "_" + openHelper.getDatabaseName() + ".tac";

        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = null;
                    FileChannel dst = null;
                    long size = 0;
                    try {
                        src = new FileInputStream(currentDB).getChannel();
                        dst = new FileOutputStream(backupDB).getChannel();
                        size = src.size();
                        dst.transferFrom(src, 0, size);
                    } finally {

                        if (src != null) {
                            src.close();
                        }
                        if (dst != null) {
                            dst.close();
                        }
                    }
                    return new BackupResult(size, backupDBPath);

                } else {
                    return new BackupResult(0, backupDBPath, "Database source file " + currentDBPath + " was not found!");
                }
            } else {
                return new BackupResult(0, backupDBPath, "SD path " + sd + " is write protected!");
            }
        } catch (IOException e) {
            return new BackupResult(0, backupDBPath, e.getMessage());
        }
    }


    public static BackupResult restore(String dbFileName) {
        final String currentDBPath = BaracusApplicationContext.getDatabasePath();

        try {
            File sd = Environment.getExternalStorageDirectory();
            File currentDB = new File(currentDBPath);
            File backupDB = new File(sd, dbFileName);

            if (!currentDB.canWrite()) {
                new Logger(DBBackup.class).error("DB NOT WRITEABLE");
            } else {
                currentDB.delete();
                // This is necessary to avoid any loitering DB Refs in GC!
                System.gc();
            }

            if (backupDB.exists()) {
                if (isValidDb(backupDB.getAbsolutePath())) {
                    BaracusApplicationContext.destroy(true);
                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    Long size = src.size();
                    dst.transferFrom(src, 0, size);
                    src.close();
                    dst.close();
                    return new BackupResult(size, dbFileName);
                } else {
                    return new BackupResult(0, dbFileName, " is not a valid database!");
                }
            } else {
                return new BackupResult(0, dbFileName, "File does not exist");
            }
        } catch (IOException e) {
            return new BackupResult(0, dbFileName, e.getMessage());
        } finally {
            BaracusApplicationContext.initApplicationContext();
            BaracusApplicationContext.make();
        }
    }

    public static boolean isValidDb(String dbFile) {
        SQLiteDatabase db = null;
        boolean valid = true;
        try {
            db = SQLiteDatabase.openDatabase(dbFile, null, SQLiteDatabase.OPEN_READONLY);
            valid = true;
        } catch (Exception e) {
            valid = false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return valid;
    }


    public static String[] getAvailableFiles() {
        File sd = Environment.getExternalStorageDirectory();
        return sd.list(new FilenameFilter() {
            @java.lang.Override
            public boolean accept(File dir, String filename) {
                final String path = dir.getAbsolutePath() + File.separator + filename;
                return (filename.endsWith(".tac")) && !new File(path).isDirectory() && isValidDb(path);
            }
        });
    }

}
