package net.mantucon.baracus.lifecycle;

/**
 * Post-Appliction-Init-Initializer
 * <p/>
 * Implement this class in order to perform after-app-init tasks
 * like cache creation etc pp.
 * <p/>
 * An ApplicationContextInitializer is called as the final step of the
 * make() method in the BaracusApplicationContext.
 * <p/>
 * You can register it using the BaracusApplicationContext.setApplicationContextInitializer
 * method
 * <p/>
 * <p/>
 * Created by marcus on 18.06.14.
 */
public interface ApplicationContextInitializer {

    /**
     * implement this method to perform after-context-init tasks
     */
    public void afterContextIsBuilt();

}

