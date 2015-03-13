package org.baracus.orm;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.12
 * Time: 15:34
 * Lazy reference implementation. Allows You to reference a collection lazily.
 * <p/>
 * Use this function in DAO-RowMappers in order to enable lazy on-access loading on
 * referenced entities, which are not bound to a container (e.g. 1:1 ref Customer to BankAccount)
 * or use this to describe a n:1 relation in the n partner (e.g. BankAccount to Bank)
 */
public class LazyReference<T extends AbstractModelBase> implements Reference<T> {

    private enum CollectionState {
        Armed,
        Loaded
    }

    private CollectionState collectionState = CollectionState.Armed;

    private T instance = null;

    /*
        The reference loader responsible to perform the lazy object access
     */
    private final ReferenceLoader<T> referenceLoader;

    public LazyReference(ReferenceLoader referenceLoader) {
        this.referenceLoader = referenceLoader;
    }

    @Override
    public T getObject() {
        if (collectionState == CollectionState.Armed) {
            synchronized (this) {
                collectionState = CollectionState.Loaded;
                instance = referenceLoader.loadObject();
            }
        }
        return instance;
    }

    @Override
    public Long getObjectRefId() {
        return referenceLoader.getId();
    }


}
