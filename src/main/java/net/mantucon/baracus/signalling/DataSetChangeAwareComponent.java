package net.mantucon.baracus.signalling;


import net.mantucon.baracus.orm.AbstractModelBase;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 27.01.13
 * Time: 12:18
 * Implement this function to be aware of recordset changes on an certain entity type
 * Then, you can register it to the context and the function will be fired on each change
 * on the recordset you are listening to.
 */
public interface DataSetChangeAwareComponent<T extends AbstractModelBase> {
    /**
     * change notifier.
     *
     * @param clazz
     * @since 0.8 : pass the class of the modified object in order to deal with
     * inheritance hierarchies. If You don't need the param,
     * simply ignore it.
     */
    void onChange(Class<? extends AbstractModelBase> clazz);
}
