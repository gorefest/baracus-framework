package net.mantucon.baracus.orm;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.12
 * Time: 08:10
 * <p/>
 * Base class to all persistence objects. You want a table based persistence bean?
 * Simply inherit this class and add your fields to the field list.
 * <p/>
 * this very static way of wiring the table is a very fast one.
 * <p/>
 * After defining the model bean, be sure that you have a @see MigrationStep creating the table and
 * defining a DAO bean managing the persistence access.
 * <p/>
 * Example  :
 * <p/>
 * public class ConfigurationParameter extends AbstractModelBase {
 * <p/>
 * public static final String TABLE_CONFIGURATION = "configuration";
 * <p/>
 * private static int prefix=0;
 * <p/>
 * private String configParameter;
 * private String configParameterValue;
 * <p/>
 * public static final FieldList fieldList = new FieldList(ConfigurationParameter.class.getSimpleName());
 * public static final Field configParamCol = new Field("config_parameter",AbstractModelBase.fieldList.size() + prefix++);
 * public static final Field configParamValueCol = new Field("config_parameter_value",AbstractModelBase.fieldList.size() + prefix++);
 * <p/>
 * static {
 * fieldList.add(AbstractModelBase.fieldList);
 * fieldList.add(configParamCol);
 * fieldList.add(configParamValueCol);
 * }
 * <p/>
 * public ConfigurationParameter() {
 * super(TABLE_CONFIGURATION);
 * }
 *
 * @Override public boolean equals(Object o) {
 * if (this == o) return true;
 * if (!(o instanceof ConfigurationParameter)) {
 * return false;
 * }
 * if (!super.equals(o)) {
 * return false;
 * }
 * <p/>
 * ConfigurationParameter that = (ConfigurationParameter) o;
 * <p/>
 * if (!configParameter.equals(that.configParameter)) {
 * return false;
 * }
 * <p/>
 * if (configParameterValue != null ? !
 * configParameterValue.equals(that.configParameterValue) : that.configParameterValue != null)
 * {   return false; }
 * <p/>
 * return true;
 * }
 * @Override public int hashCode() {
 * int result = super.hashCode();
 * result = 31 * result + configParameter.hashCode();
 * result = 31 * result + (configParameterValue != null ? configParameterValue.hashCode() : 0);
 * return result;
 * }
 * }
 */
public abstract class ModelBase extends AbstractModelBase implements Identifiable {

    /**
     * the field list of the entity.
     */

    public static final Field idCol = new Field("_id", 0, true);


    static {
        fieldList.add(idCol);
    }


    protected ModelBase(String tableName) {
        super(tableName, false);
    }

    /**
     * @return the string to be used in order to move Your old persistence bean
     * to the new id column style. Simply create a new ModelVersion using
     * Migr8 and fire this statement for each table You want to migrate
     * from legacy id style to new _id style.
     */
    public String getMigrationStatement() {
        return "UPDATE sqlite_master SET SQL=REPLACE(SQL,'id','_id') WHERE NAME = '" + this.getTableName() + "'";
    }

}
