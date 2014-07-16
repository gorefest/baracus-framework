package net.mantucon.baracus.orm;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.12
 * Time: 15:18
 * <p/>
 * Lazy collection implementation. This collection is fitted with and LazyLoader implementation
 * taking care of the load of the data on the first access to this collection.
 */
public class LazyCollection<T> implements List<T> {

    /**
     * Lazy Loader interface. Use this lazy loader in order to load the collection lazily after
     * the first access.
     *
     * @param <T>
     */
    public static interface LazyLoader<T> {
        public List<T> loadReference();
    }


    static enum CollectionState {
        Armed,
        Loaded
    }

    private CollectionState collectionState = CollectionState.Armed;

    final LazyLoader<T> lazyLoader;

    /**
     * Constructor. A LazyCollection must be fitted with a lazy loading helper
     *
     * @param lazyLoader - the component managing the load of the collection on first access.
     */
    public LazyCollection(LazyLoader<T> lazyLoader) {
        this.lazyLoader = lazyLoader;
    }


    private ArrayList<T> referencedData = new ArrayList<T>();

    /**
     * Helper function checking if a lazy load has to be performed.
     * <p/>
     * If a lazy loader is armed, it will call the loadReference function on the
     * lazy loader on first access.
     */
    private void checkReferencedData() {
        synchronized (collectionState) {
            if (collectionState == CollectionState.Armed) {
                collectionState = CollectionState.Loaded;
                referencedData.addAll(lazyLoader.loadReference());
            }
        }
    }


    public boolean add(T object) {
        checkReferencedData();
        return referencedData.add(object);
    }

    public boolean addAll(int index, Collection<? extends T> collection) {
        checkReferencedData();
        return referencedData.addAll(index, collection);
    }

    public List<T> subList(int start, int end) {
        checkReferencedData();
        return referencedData.subList(start, end);
    }

    public T remove(int index) {
        checkReferencedData();
        return referencedData.remove(index);
    }

    public int lastIndexOf(Object object) {
        checkReferencedData();
        return referencedData.lastIndexOf(object);
    }

    public int size() {
        checkReferencedData();
        return referencedData.size();
    }

    public boolean contains(Object object) {
        checkReferencedData();
        return referencedData.contains(object);
    }

    public T get(int index) {
        checkReferencedData();
        return referencedData.get(index);
    }

    public void ensureCapacity(int minimumCapacity) {
        checkReferencedData();
        referencedData.ensureCapacity(minimumCapacity);
    }

    public void add(int index, T object) {
        checkReferencedData();
        referencedData.add(index, object);
    }

    public Iterator<T> iterator() {
        checkReferencedData();
        return referencedData.iterator();
    }

    public ListIterator<T> listIterator(int location) {
        checkReferencedData();
        return referencedData.listIterator(location);
    }

    public boolean containsAll(Collection<?> collection) {
        checkReferencedData();
        return referencedData.containsAll(collection);
    }

    public <T> T[] toArray(T[] contents) {
        checkReferencedData();
        return referencedData.toArray(contents);
    }

    public void clear() {
        checkReferencedData();
        referencedData.clear();
    }

    public boolean addAll(Collection<? extends T> collection) {
        checkReferencedData();
        return referencedData.addAll(collection);
    }

    public ListIterator<T> listIterator() {
        checkReferencedData();
        return referencedData.listIterator();
    }

    public boolean removeAll(Collection<?> collection) {
        checkReferencedData();
        return referencedData.removeAll(collection);
    }

    public void trimToSize() {
        checkReferencedData();
        referencedData.trimToSize();
    }

    public int indexOf(Object object) {
        checkReferencedData();
        return referencedData.indexOf(object);
    }

    public Object[] toArray() {
        checkReferencedData();
        return referencedData.toArray();
    }

    public T set(int index, T object) {
        checkReferencedData();
        return referencedData.set(index, object);
    }

    public boolean retainAll(Collection<?> collection) {
        checkReferencedData();
        return referencedData.retainAll(collection);
    }

    public boolean isEmpty() {
        checkReferencedData();
        return referencedData.isEmpty();
    }

    public boolean remove(Object object) {
        checkReferencedData();
        return referencedData.remove(object);
    }
}
