package net.mantucon.baracus.signalling;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 27.03.13
 * Time: 11:22
 *
 * Use this class to
 *
 */
@Deprecated
public abstract class ConfigurationChangeListener{

    private final String configurationElementName;

    public ConfigurationChangeListener(String configurationElement) {
        this.configurationElementName = configurationElement;
    }

    public String getConfigurationElementName() {
        return configurationElementName;
    }

    public abstract void onChange();

}
