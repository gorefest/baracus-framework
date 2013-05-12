package net.mantucon.baracus.lifecycle;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 10.07.12
 *
 * Implement this bean, if You want to add some initialization behaviour to
 * Your bean
 *
 */
public interface Initializeable {

    /**
     * lifecycle function used for lifecycle management after creation and variable injection of a bean
     *
     * use this function in order to perform pre-use-but-after-creation-done-steps
     */
    void postConstruct();
}
