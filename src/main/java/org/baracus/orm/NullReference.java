package org.baracus.orm;

/**
 * Crash-avoiding helper. Use this Reference to instantiate any
 * Reference. Instead of dealing NPE due to null references You will simply
 * get a Null value. Makes handling a little bit easier.
 * <p/>
 * Created by marcus on 12.07.14.
 */
public final class NullReference<T extends AbstractModelBase> implements Reference<T> {

    @Override
    public final T getObject() {
        return null;
    }

    @Override
    public final Long getObjectRefId() {
        return null;
    }
}
