package net.mantucon.baracus.context;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import net.mantucon.baracus.dao.BaracusOpenHelper;
import net.mantucon.baracus.lifecycle.Destroyable;
import net.mantucon.baracus.lifecycle.Initializeable;
import net.mantucon.baracus.util.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 *
 * Bean container carrying all bean instances order to keep the
 * Application somewhat tight.
 *
 */
public class BeanContainer {

    private static final Logger logger = new Logger(BeanContainer.class);

    // We are carrying a className to Object map and a Class to Object map
    // this is simply done to avoid too many type based questions
    protected final static Map<String, Object> beanMap = new HashMap<String, Object>();
    protected final static Map<Class<?>, Object> clazzMap= new HashMap<Class<?>, Object>();

    // fragment holder. registering all fragments as bean will cause them
    // to be held here. Registering fragments as bean makes them become
    // injection capable and being used as a injection target
    protected final static Set<Fragment> knownFragments = new HashSet<Fragment>();

    // stats maps for handling context events
    protected final static Map<Class<?>, Object> activeActivitiesMap= new HashMap<Class<?>, Object>();
    protected final static Map<Class<?>, Object> pausedActivitiesMap= new HashMap<Class<?>, Object>();
    protected final static Map<Class<?>, Object> existingActivitiesMap= new HashMap<Class<?>, Object>();


    /**
     * Bean instanciation fucked up. Thrown when the instantiation of
     * a bean caised an errer
     */
    public static class IntantiationException extends RuntimeException {
        IntantiationException(Throwable reason) {
            super(reason);
        }
    }

    /**
     * Exception while destroying a bean. thrown if a shutdown caused
     * an error
     */
    public static class BeanDestructionException extends RuntimeException {
        BeanDestructionException(Throwable reason) {
            super(reason);
        }
    }

    /**
     * Registration of a bean failed.
     */
    public static class RegistrationException extends RuntimeException {
        RegistrationException(Throwable reason) {
            super(reason);
        }
    }

    /**
     * Injection of a bean caused an error
     */
    public static class InjectionException extends RuntimeException {
        InjectionException(Throwable reason) {
            super(reason);
        }
        InjectionException(String msg, Throwable reason) {
            super(msg, reason);
        }
    }




    /**
     * instanciate all registered beans
     */
    void createInstances() {
        for (Class<?> clazz : clazzMap.keySet()) {
            if (clazzMap.get(clazz) == null) {
                try {
                    instantiateBean(clazz);
                    logger.debug("Instantiation of $1 succeded.", clazz.getName());
                } catch (Exception e) {
                    logger.debug("Instantiation of $1 failed. Reason : $2", clazz.getName(), e.getMessage());
                    throw new IntantiationException(e);
                }
            }
        }
    }

    /**
     * perform postconstruct method on all bean instances implementing Initializeable
     */
    void performPostConstuct() {
        for (Object o : beanMap.values()) {
            if (o instanceof Initializeable) {
                logger.debug("Running Post Construction method on $1",o.getClass().getName());
                ((Initializeable) o).postConstruct();
            }
        }
    }

    /**
     * perform all bean injections on all beans
     */
    void performInjections() {
        for (Class<?> clazz : clazzMap.keySet()) {
            performInjection(clazz);
        }

        //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * locate the passed class instance and call injection
     * @param clazz - the class whose cached singleton instance shall be injected with components
     */
    void performInjection(Class<?> clazz) {
        Object o = clazzMap.get(clazz);
        performInjection(o);
    }

    /**
     * perform injection on the passed object instance
     * @param o
     */
    void performInjection(Object o) {
        Class<?> clazz = o.getClass();
        for (Field field : getAllDeclaredFields(clazz)) {
            String type = field.getType().getName();
            for (Class<?> clazz2 : clazzMap.keySet()) {
                if (type.equals(clazz2.getName())) {
                    field.setAccessible(true);
                    logger.debug("$1.$2 candidate is $3",clazz.getName(),field.getName(),clazz2.getName());
                    try {
                        field.set(o, clazzMap.get(clazz2));
                    } catch (IllegalAccessException e) {
                        throw new InjectionException("Failed to set "+clazz.getName()+"."+field.getName()+" with bean "+clazz2.getName(),e);
                    }
                } else if (type.equals(SQLiteDatabase.class.getName())) {
                    field.setAccessible(true);
                    logger.debug("$1.$2 candidate is $3",clazz.getName(),field.getName(),clazz2.getName());
                    try {
                        field.set(o, BaracusApplicationContext.getInstance().connectDbHandle());
                    } catch (IllegalAccessException e) {
                        throw new InjectionException("OMG SQLite injection issued a major clusterfuck",e);
                    }
                } else if (type.equals(BaracusOpenHelper.class.getName())) {
                    field.setAccessible(true);
                    logger.debug("$1.$2 candidate is $3",clazz.getName(),field.getName(),clazz2.getName());
                    try {
                        field.set(o, BaracusApplicationContext.getInstance().connectOpenHelper());
                    } catch (IllegalAccessException e) {
                        throw new InjectionException("OMG OpenHelper injection issued a major clusterfuck",e);
                    }
                }
            }
        }
    }

    /**
     * @param clazz - the class to scan
     * @return all fields of all types
     */
    Field[] getAllDeclaredFields(Class<?> clazz) {
        ArrayList<Field> fields = new ArrayList<Field>();

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        Class<?> superclass = clazz;

        while ((superclass = superclass.getSuperclass()) != null) {
            fields.addAll(Arrays.asList(superclass.getDeclaredFields()));
        }

        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * nullify component refs in all beans
     */
    void performDestruction() {
        for (Class<?> clazz : clazzMap.keySet()) {
            performOutjection(clazz);
        }

        for (Class<?> clazz : clazzMap.keySet()) {
            removeBean(clazz);
        }
    }

    /**
     * tidy upp all managed references of clazz in order to avoid loitering objects
     * @param clazz
     */
    void performOutjection(Class<?> clazz) {
        Object o = clazzMap.get(clazz);
        if (o == null) {
            logger.warn("Warning! Object of type $1 was already nulled!",clazz.getName());
        } else {
            performOutjection(o);
        }
    }

    void performOutjection(Object o) {
        Class<?> clazz = o.getClass();
        for (Field field : getAllDeclaredFields(clazz)) {
            String type = field.getType().getName();
            for (Class<?> clazz2 : clazzMap.keySet()) {
                if (type.equals(clazz2.getName())) {
                    field.setAccessible(true);
                    logger.debug("$1.$2 nullified",clazz.getName(),field.getName(),clazz2.getName());
                    try {
                        field.set(o, null);
                    } catch (IllegalAccessException e) {
                        throw new BeanDestructionException(e);
                    }
                }
            }
        }
    }


    /**
     * instanciate a bean class. if the bean class has got a constructor carrying the android Context,
     * this constructor is used to pass the context. Otherwise, your bean has to implement a default
     * constructor!
     *
     * @param theClazz     - the clazz to instanciate
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     */
    void instantiateBean(Class<?> theClazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Object result = null;
        for (Constructor c : theClazz.getConstructors()) {
            Class<?>[] parameterTypes = c.getParameterTypes();
            if (parameterTypes == null || parameterTypes.length == 0) {
                // default Constructor
                result = theClazz.newInstance();
                break;
            } else if (parameterTypes.length == 1 && parameterTypes[0].equals(Context.class)) {
                result =   c.newInstance(BaracusApplicationContext.getContext());
                break;
            }
        }

        if (result == null) {
            throw new InstantiationException(theClazz.getName()+" could not be instantiated. Please provide a) a public default constructor or b) a public constructor takinng the Android Context as it's only parameter!");
        }


        holdBean(theClazz, result);
    }

    /**
     * put the bean into the holders
     * @param theClazz
     * @param o
     */
    void holdBean(Class<?> theClazz, Object o) {
        beanMap.put(theClazz.getName(), o);
        clazzMap.put(theClazz ,o);
        if (o instanceof  Fragment) {
            knownFragments.add((Fragment)o);
        }
    }

    /**
     * remove the bean class from the holders
     * @param theClazz
     */
    void removeBean(Class<?> theClazz) {
        beanMap.put(theClazz.getName(), null);
        clazzMap.put(theClazz, null);
    }

    /**
     * register a bean class managed by the container A managed bean
     * can implement the lifecycle interfaces Initializable (for post-
     * construction management) and Destroyable (for pre-destroy management).
     *
     * @param theClazz
     */
    final void registerBeanClass(Class<?> theClazz) {
        final String clazzName =  theClazz.getName();
        if (!beanMap.containsKey(clazzName)) {
            try {
                holdBean(theClazz,null);

            } catch (Exception e) {
                throw new RegistrationException(e);
            }
        }
    }

    /**
     * Shred the beans.
     */
    synchronized void shutdownContext() {
            for (Object o : beanMap.values()) {
                if (o instanceof Destroyable) {
                    ((Destroyable) o).onDestroy();
                }
            }
            performDestruction();
            System.gc();
    }

    private void unregisterClasses() {
        for (Class<?> c : clazzMap.keySet()) {
            removeBean(c);
        }
    }

    static void addActiveActivity(Activity activity){
        addActivity(activity,activeActivitiesMap);
    }
    static void addExistingActivity(Activity activity){
        addActivity(activity,existingActivitiesMap);
    }
    static void addPausedActivity(Activity activity){
        addActivity(activity,pausedActivitiesMap);
    }

    static void removeActiveActivity(Activity activity){
        removeActivity(activity,activeActivitiesMap);
    }
    static void removeExistingActivity(Activity activity){
        removeActivity(activity,existingActivitiesMap);
    }
    static void removePausedActivity(Activity activity){
        removeActivity(activity,pausedActivitiesMap);
    }

    static void printStats() {
        logger.info("EXSTING $1", existingActivitiesMap.size());
        logger.info("ACTIVE $1", activeActivitiesMap.size());
        logger.info("RESUMED $1", pausedActivitiesMap.size());
    }


    private static void addActivity(final Activity activity, final Map<Class<?>, Object> map ) {

        if (map.containsKey(activity.getClass())) {
            logger.debug("Activity class $1 already in keyset", activity.getClass().getName());
        }

        if (map.containsValue(activity))  {
            logger.debug("Activity $1 already in values",activity);
        }

        map.put(activity.getClass(),activity);
        printStats();
    }

    private static void removeActivity(final Activity activity, final Map<Class<?>, Object> map ) {

        if (!map.containsKey(activity.getClass())) {
            logger.debug("Activity class $1 not in keyset", activity.getClass().getName());
        }

        if (!map.containsValue(activity))  {
            logger.debug("Activity $1 not in values",activity);
        }

        map.remove(activity.getClass());
        printStats();
    }


    public void treatKnownUiComponents() {
        for (Object o : activeActivitiesMap.values()) {
            performInjection(o);
        }

        for (Fragment f : knownFragments) {
            try {
                performInjection(f);
            } catch (Exception e) {
                logger.debug("$1 has caused an injection problem and is going to be removed from list.");
                knownFragments.remove(f);
            }
        }
    }
}
