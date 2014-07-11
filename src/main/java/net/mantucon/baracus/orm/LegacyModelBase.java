package net.mantucon.baracus.orm;

/**
 * Legacy old model class using the "id" column.
 *
 * @deprecated the "id" clashes with androids "_id" convention
 * and makes it uneasy to harmonize by hand.
 * You should consider altering Your tables using
 * migr8 in order to rename the id column to an _id column
 * and then implement the ModelBase class
 */
@Deprecated
public abstract class LegacyModelBase extends AbstractModelBase implements Identifiable {

    public static final FieldList fieldList = new FieldList(AbstractModelBase.class.getSimpleName());

    /**
     * the field list of the entity.
     */

    public static final Field idCol = new Field("id", 0, true);


    static {
        fieldList.add(idCol);
    }


    protected LegacyModelBase(String tableName) {
        super(tableName, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ModelBase)) {
            return false;
        }

        ModelBase that = (ModelBase) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (!getTableName().equals(that.getTableName())) {
            return false;
        }

        return true;
    }


}

