package org.baracus.orm;

/**
 * Created by marcus on 03.07.14.
 */
public abstract class OptimisticLockingModelBase extends ModelBase implements OptmisticLocking {

    public static final FieldList fieldList = new FieldList(AbstractModelBase.class.getSimpleName());

    public static class OptimisticLockException extends RuntimeException {
        public final String entityName;

        public OptimisticLockException(String entityName) {
            super("Optimistic locking failed for entity " + entityName);
            this.entityName = entityName;
        }
    }

    /**
     * the field list of the entity.
     */

    public static final Field versionCol = new Field("version", ModelBase.fieldList.size(), true);


    static {
        fieldList.add(ModelBase.fieldList);
        fieldList.add(versionCol);
    }

    private int version = 0;

    protected OptimisticLockingModelBase(String tableName) {
        super(tableName);
    }

    protected OptimisticLockingModelBase(String tableName, Long id) {
        super(tableName, id);
    }

    public static Field getVersionCol() {

        return versionCol;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptimisticLockingModelBase)) {
            return false;
        }

        OptimisticLockingModelBase that = (OptimisticLockingModelBase) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (!getTableName().equals(that.getTableName())) {
            return false;
        }

        return true;
    }
}

