package org.baracus.util;

import java.util.*;

/**
 * A set of simple generic data helpers
 * <p/>
 * Created by marcus on 24.07.14.
 */
public class DataUtil {

    public static interface Hashifier<U, T> {
        public U getValue(T item);
    }


    /**
     * Hashing helper function. Create a 1:1 HashMap from the passed collection.
     * If a key is used, which is non-unique inside of the passed collection, an
     * IllegalArgumentException is thrown.
     *
     * @param items     - the item collection
     * @param hashifier - the hashifier object used to extract the key out of the individual object
     * @param <T>       - item type param
     * @param <U>       - item key param
     * @return Map<key,item> containing all elements
     */
    public static <T, U> Map<U, T> hashify(Collection<T> items, Hashifier<U, T> hashifier) {
        HashMap<U, T> result = new HashMap<U, T>();
        for (T t : items) {
            U key = hashifier.getValue(t);
            if (result.containsKey(key)) {
                throw new IllegalArgumentException("The hashifier for the passed item list must produce unique keys! Otherwise use hashify2List function!");
            }
            result.put(key, t);
        }
        return result;
    }


    /**
     * Hashing helper function. Create a 1:1 HashMap from the passed collection.
     * If a key is used, which is non-unique inside of the passed collection, an
     * IllegalArgumentException is thrown.
     *
     * @param items     - the item collection
     * @param hashifier - the hashifier object used to extract the key out of the individual object
     * @param <T>       - item type param
     * @param <U>       - item key param
     * @return Map<key,item> containing all elements
     * <p/>
     * UNTESTED
     */
    public static <T, U> Map<U, List<T>> hashify2List(Collection<T> items, Hashifier<U, T> hashifier) {
        HashMap<U, List<T>> result = new HashMap<U, List<T>>();
        for (T t : items) {
            U key = hashifier.getValue(t);
            List<T> itemCollection;
            if (!result.containsKey(key)) {
                itemCollection = new ArrayList<T>();
                result.put(key, itemCollection);
            } else {
                itemCollection = result.get(key);
            }
            itemCollection.add(t);
        }
        return result;
    }
}