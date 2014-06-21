package net.mantucon.baracus.model;

import net.mantucon.baracus.orm.Field;
import net.mantucon.baracus.orm.FieldList;
import net.mantucon.baracus.orm.LegacyModelBase;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.12
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationParameter extends LegacyModelBase {

    public static final String TABLE_CONFIGURATION = "configuration";

    private static int prefix = 0;

    private String configParameter;
    private String configParameterValue;

    public static final FieldList fieldList = new FieldList(ConfigurationParameter.class.getSimpleName());
    public static final Field configParamCol = new Field("config_parameter", LegacyModelBase.fieldList.size() + prefix++);
    public static final Field configParamValueCol = new Field("config_parameter_value", LegacyModelBase.fieldList.size() + prefix++);

    static {
        fieldList.add(LegacyModelBase.fieldList);
        fieldList.add(configParamCol);
        fieldList.add(configParamValueCol);
    }

    public ConfigurationParameter() {
        super(TABLE_CONFIGURATION);
    }

    public String getConfigParameter() {
        return configParameter;
    }

    public void setConfigParameter(String configParameter) {
        this.configParameter = configParameter;
    }

    public String getConfigParameterValue() {
        return configParameterValue;
    }

    public void setConfigParameterValue(String configParameterValue) {
        this.configParameterValue = configParameterValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
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
        if (configParameterValue != null ? !configParameterValue.equals(that.configParameterValue) : that.configParameterValue != null) {
            return false;
        }


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
