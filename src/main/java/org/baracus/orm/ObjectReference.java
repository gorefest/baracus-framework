package org.baracus.orm;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 26.09.12
 * Time: 10:10
 * Object Reference. Use this item in order to set a discrete - non-lazy - object referenc
 * to an object. Use this item esp. when setting a reference to another entity in order
 * to persist the reference.
 */
public class ObjectReference<T extends AbstractModelBase> implements Reference<T> {

    private T object;

    public ObjectReference(T object) {
        this.object = object;
    }

    public ObjectReference() {
    }

    @Override
    public T getObject() {
        return object;
    }

    @Override
    public Long getObjectRefId() {
        return object.getId();
    }

    public void setObject(T object) {
        this.object = object;
    }
}
