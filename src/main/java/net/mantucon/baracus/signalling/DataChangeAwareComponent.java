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
public interface DataChangeAwareComponent<T extends AbstractModelBase> {
    public void onChange(T changedInstance);
}
