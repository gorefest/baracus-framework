package net.mantucon.baracus.orm;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 26.09.12
 * Time: 10:10
 * Object reference. This is a type-to-type reference helper. It helps you
 * to have lazy references without any dynamic proxying. The price you pay
 * is accessing the Reference via an additional accessor (e.g. customer.getBankAccount().getObject());
 *
 * When setting an instane of a reference in your code in order to save
 * it, use the ObjectReference implementation.
 *
 * When writing a dao mapping function, use the lazy reference carrying the lazy loading information to
 * post-load the reference object on first access.
 *
 */
public interface Reference<T extends AbstractModelBase> {
    T getObject();
    Long getObjectRefId();
}
