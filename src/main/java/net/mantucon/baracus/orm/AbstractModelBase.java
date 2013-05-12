package net.mantucon.baracus.orm;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.12
 * Time: 08:10
 *
 * Base class to all persistence objects. You want a table based persistence bean?
 * Simply inherit this class and add your fields to the field list.
 *
 * this very static way of wiring the table is a very fast one.
 *
 * After defining the model bean, be sure that you have a @see MigrationStep creating the table and
 * defining a DAO bean managing the persistence access.
 *
 * Example  :

 public class ConfigurationParameter extends AbstractModelBase {

    public static final String TABLE_CONFIGURATION = "configuration";

    private static int prefix=0;

    private String configParameter;
    private String configParameterValue;

    public static final FieldList fieldList = new FieldList(ConfigurationParameter.class.getSimpleName());
    public static final Field configParamCol = new Field("config_parameter",AbstractModelBase.fieldList.size() + prefix++);
    public static final Field configParamValueCol = new Field("config_parameter_value",AbstractModelBase.fieldList.size() + prefix++);

    static {
        fieldList.add(AbstractModelBase.fieldList);
        fieldList.add(configParamCol);
        fieldList.add(configParamValueCol);
    }

    public ConfigurationParameter() {
        super(TABLE_CONFIGURATION);
    }

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof ConfigurationParameter)) {
          return false;
        }
        if (!super.equals(o)) {
          return false;
        }

        ConfigurationParameter that = (ConfigurationParameter) o;

        if (!configParameter.equals(that.configParameter)) {
            return false;
        }

        if (configParameterValue != null ? !
            configParameterValue.equals(that.configParameterValue) : that.configParameterValue != null)
             {   return false; }

        return true;
     }

     @Override
     public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + configParameter.hashCode();
        result = 31 * result + (configParameterValue != null ? configParameterValue.hashCode() : 0);
        return result;
     }
 }


 *
 *
 *
 */
public abstract class AbstractModelBase implements Identifiable {

    /**
     * the field list of the entity.
     */

    public static final Field idCol = new Field("id",0, true);

    public static final FieldList fieldList = new FieldList(AbstractModelBase.class.getSimpleName(), idCol);

/*    static {
        fieldList.add(idCol);
    }*/

    protected Long id;
    private boolean isTransient = true;
    private final String tableName;


    protected AbstractModelBase(String tableName) {
        this.tableName = tableName;
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
}
