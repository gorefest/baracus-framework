package net.mantucon.baracus.orm;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.12
 * Time: 08:10
 * <p/>
 * Base class to all internal persistence objects.
 * <p/>
 * If You want to add a persistence bean, simply inherit ModelBase!
 *
 * @see net.mantucon.baracus.orm.ModelBase
 */
public abstract class AbstractModelBase implements Identifiable {

    public static final FieldList fieldList = new FieldList(AbstractModelBase.class.getSimpleName());

    protected Long id;
    private boolean isTransient = true;
    private final String tableName;
    private final boolean isOldStyle;


    AbstractModelBase(String tableName, boolean isOldStyle) {
        this.tableName = tableName;
        this.isOldStyle = isOldStyle;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean aTransient) {
        isTransient = aTransient;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractModelBase)) {
            return false;
        }

        AbstractModelBase that = (AbstractModelBase) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (!tableName.equals(that.tableName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + tableName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AbstractModelBase{" +
                "id=" + id +
                ", isTransient=" + isTransient +
                ", tableName='" + tableName + '\'' +
                '}';
    }

    /**
     * @return true, if You are using a legacy model bean
     */
    public boolean isOldStyle() {
        return isOldStyle;
    }
}
