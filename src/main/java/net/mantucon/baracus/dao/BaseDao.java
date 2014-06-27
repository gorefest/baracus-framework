package net.mantucon.baracus.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import net.mantucon.baracus.annotations.Bean;
import net.mantucon.baracus.context.BaracusApplicationContext;
import net.mantucon.baracus.orm.*;
import net.mantucon.baracus.util.Logger;

import java.util.*;

/**
 * DAO Base Class. If You want to make use of DAOs, You must derive this class to manage a
 * POJO deriving AbstractModelBase
 *
 * @param <T>
 * @see ConfigurationDao for an example DAO.
 */
public abstract class BaseDao<T extends AbstractModelBase> {

    @Bean
    private SQLiteDatabase db;

    private final Logger logger = new Logger(this.getClass());

    protected final Class<T> managedClass;

    /**
     * Rowmapper component providing the object mapping functions
     * example :
     * <p/>
     * RowMapper<ConfigurationParameter> rowMapper = new RowMapper<ConfigurationParameter>() {
     *
     * @param <T>
     * @Override public ConfigurationParameter from(Cursor c) {
     * ConfigurationParameter result = new ConfigurationParameter();
     * result.setId(c.getLong(idCol.fieldIndex));
     * result.setConfigParameter(c.getString(configParamCol.fieldIndex));
     * result.setConfigParameterValue(c.getString(configParamValueCol.fieldIndex));
     * result.setTransient(false);
     * return result;
     * }
     * @Override public String getAffectedTable() { return TABLE_CONFIGURATION;  }
     * @Override public FieldList getFieldList() { return ConfigurationParameter.fieldList; }
     * @Override public Field getNameField() {
     * return ConfigurationParameter.configParamCol;
     * }
     * <p/>
     * public ContentValues getContentValues(ConfigurationParameter parm) {
     * ContentValues result = new ContentValues();
     * if (parm.getId() != null) { result.put(idCol.fieldName, parm.getId()); }
     * if (parm.getConfigParameter() != null) { result.put(configParamCol.fieldName, parm.getConfigParameter()); }
     * if (parm.getConfigParameterValue()!= null) { result.put(configParamValueCol.fieldName, parm.getConfigParameterValue()); }
     * return result;
     * }
     * };
     */
    public static interface RowMapper<T extends AbstractModelBase> {
        /**
         * maps the cursor content to a model bean (aka entity bean)
         *
         * @param c - the cursor
         * @return the model bean
         * @see ConfigurationDao.RowMapper
         */
        T from(Cursor c);

        /**
         * @return the affected table name
         * @see ConfigurationDao.RowMapper
         */
        String getAffectedTable();

        /**
         * @return the field list of your model bean
         * @see ConfigurationDao.RowMapper
         * @see net.mantucon.baracus.model.ConfigurationParameter
         */
        FieldList getFieldList();

        /**
         * just for convinience, if your model has an unique name identifier (e.g. bankName(
         * you can return this field. a matching dao then automatically has got a getByName function
         *
         * @return the name field
         * @see ConfigurationDao.RowMapper
         */
        Field getNameField();

        /**
         * makes a SQLite content values object out of your model bean in order to
         * perform an update or an insert on the entity.
         *
         * @param item - the model bean
         * @return the content values object
         * @see ConfigurationDao.RowMapper
         */
        ContentValues getContentValues(T item);
    }

    /**
     * Interface for a RowMapper supporting timestamp columns to make
     * use of generic time based functions
     *
     * @param <T>
     */
    public static interface TimestampSupportingRowmapper<T extends AbstractModelBase> extends RowMapper<T> {
        /**
         * @return the field containing the timestamp information
         */
        Field getLastModifiedField();

        /**
         * @return the field containing the creation timstamp information
         */
        Field getCreationDateField();
    }

    /**
     * Lock-in Constructor. Replaces the deprecated getManagedClass() function in order to save
     * performance
     *
     * @param managedClass - the model class Your Dao manages
     */
    protected BaseDao(Class<T> managedClass) {
        this.managedClass = managedClass;
    }

    /**
     * performs a delete operation on the db
     *
     * @param model
     * @return
     */
    public int delete(final AbstractModelBase model) {
        int result = 0;
        if (!model.isTransient()) {
            Long id = model.getId();
            result = deleteById(id);
            BaracusApplicationContext.emitDeleteEvent(managedClass);
            model.setTransient(true);
        } else {
            logger.warn("Warning. You tried to delete a transient entity of type $1. No operation performed!.", model.getClass().getName());
            result = -1;
        }
        return result;
    }

    public int deleteById(Long id) {
        int result;
        result = db.delete(getRowMapper().getAffectedTable(), getIdField() + " = ?", new String[]{id.toString()});
        return result;
    }

    /**
     * fetches an model bean out the db using the Long id.
     *
     * @param id - the id
     * @return the model bean;
     */
    public T getById(Long id) {

        RowMapper<T> rowMapper = getRowMapper();
        Cursor c = null;
        T result = null;
        try {
            c = db.query(true, rowMapper.getAffectedTable(), rowMapper.getFieldList().getFieldNames(), getIdField() + "= ?", new String[]{id.toString()}, null, null, null, null);
            if (!c.isAfterLast() && c.moveToNext()) {
                result = rowMapper.from(c);
            } else {
                result = null;
            }
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return result;
    }

    /**
     * return a unique item identified by it's name (e.g. Bank.bankName). To make use of this feature,
     * Your rowmapper must return a proper name column in the getNameCol function (@see ConfigurationDao.RowMapper)
     *
     * @param name - the name String
     * @return the object identified by this name
     */
    public T getByName(String name) {
        logger.trace("get object by name $1", name);

        RowMapper<T> rowMapper = getRowMapper();
        Field nameField = rowMapper.getNameField();

        if (nameField == null) {
            throw new UnsupportedOperationException("NAME FIELD IS NOT DEFINED FOR THIS TYPE : " + rowMapper.getAffectedTable() + ". You have to implement Your RowMapper's getNameField function properly to make use of this feature.");
        }
        Cursor c = null;
        T result = null;
        try {

            c = db.query(true, rowMapper.getAffectedTable(), rowMapper.getFieldList().getFieldNames(), nameField + "= ?", new String[]{name}, null, null, null, null);
            if (!c.isAfterLast() && c.moveToNext()) {
                result = rowMapper.from(c);
            } else {
                result = null;
            }
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return result;
    }

    /**
     * @return all entities of Your type in database.
     */
    public List<T> loadAll() {
        Cursor c = null;
        List<T> result = new LinkedList<T>();
        try {
            c = getLoadAllCursor();
            result = iterateCursor(c);

        } catch (Exception e) {
            logger.error("An Error has occured", e);
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }
        return result;
    }

    /**
     * @return a Cursor for the loadAll Query. Notice, You have to take care of this cursor by Yourself!!
     * @since 0.8
     */
    public Cursor getLoadAllCursor() {
        RowMapper<T> rowMapper = getRowMapper();
        return db.query(true, rowMapper.getAffectedTable(), rowMapper.getFieldList().getFieldNames(), null, null, null, null, null, null);
    }

    /**
     * Transaction handle. Enables You to have transactions with sqlite in a jdbc-alike way.
     * You can get it by calling the beginTransaction function
     */
    public final class TxHandle {
        private final SQLiteDatabase db;

        private TxHandle(SQLiteDatabase db) {
            this.db = db;
        }

        public final void commit() {
            this.db.setTransactionSuccessful();
            this.db.endTransaction();
        }

        public final void rollback() {
            this.db.endTransaction();
        }

    }

    /**
     * starts a new sqlite transaction
     *
     * @return the transaction handle
     */
    public TxHandle getTransaction() {
        db.beginTransaction();
        return new TxHandle(db);
    }

    /**
     * save an item without reusing an existing transaction (autocommit)
     *
     * @param item - the model bean to save
     */
    public void save(T item) {
        save(item, null);
    }

    /**
     * save an item in a running transaction. requires you later to call txHandle.commit() in
     * order to commit the transaction.
     *
     * @param item   - the model bean
     * @param handle - the transaction handle
     */
    public void save(T item, TxHandle handle) {
        logger.trace("save object $1", item);

        boolean requiresSetChange = false;
        boolean requiresInstanceChange = false;

        RowMapper<T> rowMapper = getRowMapper();
        ContentValues cv = rowMapper.getContentValues(item);

        boolean localTransaction = handle == null;
        if (localTransaction) {
            db.beginTransaction();
        }

        try {
            if (item.getId() == null || item.isTransient()) {
                if (item instanceof Timestamped) {
                    Timestamped t = (Timestamped) item;
                    t.setCreationDate(new Date());
                    t.setLastModificationDate(new Date());
                }
                long key = db.insert(rowMapper.getAffectedTable(), null, cv);
                item.setId(key);
                item.setTransient(false);
                requiresSetChange = true;
            } else {
                if (item instanceof Timestamped) {
                    Timestamped t = (Timestamped) item;
                    if (t.getCreationDate() == null) {
                        t.setCreationDate(new Date());
                    }
                    t.setLastModificationDate(new Date());
                }
                db.update(rowMapper.getAffectedTable(), cv, getIdField() + "= ?", new String[]{item.getId().toString()});
                requiresInstanceChange = true;
            }
            if (localTransaction) {
                db.setTransactionSuccessful();
            }
        } finally {
            if (localTransaction) {
                db.endTransaction();
            }
        }

        if (requiresSetChange) {
            BaracusApplicationContext.emitSetChangeEvent(managedClass);
        }

        if (requiresInstanceChange) {
            BaracusApplicationContext.emitDataChangeEvent(item);
        }
    }

    /**
     * @return the ID column. Override this, if You want to use another ID column
     * Notice: Your model must not include the AbstractModelBase-FieldList!
     * You must define the entire entity by Yourself!
     * <p/>
     * The naming technique is subject for change towards the android
     * standard (_id)!
     */
    protected String getIdField() {
        return ModelBase.class.isAssignableFrom(managedClass)
                ? ModelBase.idCol.fieldName :
                LegacyModelBase.idCol.fieldName;
    }

    /**
     * clears the entire table
     */
    public void deleteAll() {
        RowMapper<T> rowMapper = getRowMapper();
        db.delete(rowMapper.getAffectedTable(), null, null);

    }

    /**
     * iterate the cursor in order to have a list of entity afterwards
     *
     * @param c - the cursor
     * @return - all mapped entities
     */
    protected List<T> iterateCursor(Cursor c) {
        RowMapper<T> rowMapper = getRowMapper();
        List<T> result = new LinkedList<T>();
        while (!c.isAfterLast() && c.moveToNext()) {
            result.add(rowMapper.from(c));
        }
        return result;
    }

    /**
     * @return the rowmapper implemented by you. @see ConfigurationDao.RowMapper
     */
    public abstract RowMapper<T> getRowMapper();


    /**
     * save the entire list of entity beans without transaction (autocommit)
     *
     * @param list - the list of beans
     */
    public void saveAll(List<T> list) {
        saveAll(list, null);
    }

    /**
     * save the entire list of entity beans in a transaction. requires you to
     * call the txHandle.commit() later
     *
     * @param list   - the list of beans
     * @param handle - the tx handle
     */
    public void saveAll(final List<T> list, final TxHandle handle) {
        final boolean localTransaction = handle == null;
        final TxHandle txHandle = localTransaction ? getTransaction() : handle;
        try {
            for (T item : list) {
                save(item, handle);
            }

            if (localTransaction) {
                txHandle.commit();
            }
        } catch (RuntimeException e) {
            if (localTransaction) {
                txHandle.rollback();
            }
        }
    }

    /**
     * @param date - date from which we are going to query
     * @return all items modified after passed date
     */
    public List<T> getAllItemsModifiedAfter(Date date) {
        if (Timestamped.class.isAssignableFrom(managedClass)) {
            RowMapper<T> rowMapper = getRowMapper();
            if (!(rowMapper instanceof TimestampSupportingRowmapper)) {
                throw new IllegalArgumentException("Rowmapper of entity " + managedClass.getSimpleName() + " must implement the TimestampSupportingRowmapper interface to make use of this function!");
            }

            Cursor c = db.query(true, rowMapper.getAffectedTable(), rowMapper.getFieldList().getFieldNames(), ((TimestampSupportingRowmapper) rowMapper).getLastModifiedField().fieldName + " > ? ", new String[]{String.valueOf(date.getTime())}, null, null, null, null);
            return iterateCursor(c);
        } else {
            throw new IllegalArgumentException(managedClass.getSimpleName() + " must implement Timestamped to make use of this function!");
        }
    }

    /**
     * @return the db reference
     */
    protected SQLiteDatabase getDb() {
        return db;
    }

    /**
     * return all data matching the example. only fields set with a value
     * are regarded. If querying with wildcard, all Strings will be searched
     * using LIKE. All criteria is connected using AND.
     * <p/>
     * Notice, if You want wildcard queries, You are responsible to place the
     * joker symbols (%).
     *
     * @param example      - the example object
     * @param withWildCard - if true, all strings will be search with LIKE
     * @return a list matching all example data
     */
    public List<T> queryByExample(T example, boolean withWildCard) {
        ContentValues cv = getRowMapper().getContentValues(example);
        Set<Map.Entry<String, Object>> entries = cv.valueSet();
        String[] args = new String[entries.size()];
        StringBuilder clause = new StringBuilder();
        int i = 0;
        boolean withAnd = false;
        for (Map.Entry<String, Object> entry : entries) {
            if (withAnd) {
                clause.append(" AND ");
            } else {
                withAnd = true;
            }
            if (entry.getValue() instanceof String) {
                clause.append(entry.getKey()).append((withWildCard ? " LIKE ?" : " = ?"));
            } else {
                clause.append(entry.getKey()).append(" = ?");
            }
            args[i++] = entry.getValue().toString();
        }

        Cursor c = getDb().query(true, getRowMapper().getAffectedTable(), getRowMapper().getFieldList().getFieldNames(), clause.toString(), args, null, null, null, null);
        List<T> result = iterateCursor(c);
        c.close();
        return result;
    }
}

