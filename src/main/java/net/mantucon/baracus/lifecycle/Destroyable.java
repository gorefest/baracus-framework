package net.mantucon.baracus.lifecycle;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 03.04.13
 * <p/>
 * Implement this interface, if You want to add a pre-destroy behaviour
 * to Your bean
 */
public interface Destroyable {

    /**
     * Lifecycle callback called on bean destruction
     */
    public void onDestroy();
}
