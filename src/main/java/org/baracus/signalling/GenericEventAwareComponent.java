package org.baracus.signalling;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 27.06.13
 * Time: 19:13
 * To change this template use File | Settings | File Templates.
 */
public interface GenericEventAwareComponent<T extends GenericEvent> {
    void handleEvent(T event);
}
