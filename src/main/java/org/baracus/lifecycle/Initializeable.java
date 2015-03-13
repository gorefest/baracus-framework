package org.baracus.lifecycle;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 10.07.12
 * <p/>
 * Implement this bean, if You want to add some initialization behaviour to
 * Your bean
 */
public interface Initializeable {

    /**
     * lifecycle function used for lifecycle management after creation and variable injection of a bean
     * <p/>
     * use this function in order to perform pre-use-but-after-creation-done-steps
     */
    void postConstruct();
}
