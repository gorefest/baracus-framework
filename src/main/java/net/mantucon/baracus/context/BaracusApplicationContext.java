package net.mantucon.baracus.context;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import net.mantucon.baracus.dao.BaracusOpenHelper;
import net.mantucon.baracus.dao.ConfigurationDao;
import net.mantucon.baracus.orm.AbstractModelBase;
import net.mantucon.baracus.signalling.*;
import net.mantucon.baracus.util.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.  <br>
 * User: marcus                 <br>
 * Date: 10.07.12               <br>
 * Time: 06:05                  <br>
 * <hr>
 *
 * Base Application Context class. In order to use BARACUS you must inherit this class.
 * use the registerBeanClass() function to add all Your bean classes. Implement Initializable and
 * Destroyable interface in order to have creation / destruction lifecycle management support.
 *
 * <hr>
 * Example Context Implementation :
<pre>
 {@code

 public class ApplicationContext extends BaracusApplicationContext{

 static {
    registerBeanClass(BankDao.class);
    ...
 }


 private static final Logger logger = new Logger(ApplicationContext.class);

 private ApplicationContext() {
 // protection constructor
 }


 }

 To make use of Your class as an app container, You must register it in the
 AndroidManifest.xml's application tag :

 {@code

 <application android:icon="@drawable/icon"
 android:label="@string/app_name"
 android:debuggable="true"
 android:theme="@android:style/Theme.DeviceDefault"
 android:name=".wonderapp.application.ApplicationContext">


 }

 </pre>

 *
 */
public abstract class BaracusApplicationContext extends Application {

    // infrastructure beans

    // DB Access
    private static SQLiteDatabase db;
    private static BaracusOpenHelper baracusOpenHelper;


    private static boolean semaphore = false;
    private static int refCount = 0;

    // Awareness Refs
    protected final static Map<Class<?>, DeleteAwareComponent> deleteListeners = new HashMap<Class<?>, DeleteAwareComponent>();
    protected final static Map<Class<?>, DataSetChangeAwareComponent> changeListener = new HashMap<Class<?>, DataSetChangeAwareComponent>();
    protected final static Map<Class<?>, Set<DataChangeAwareComponent>> dataListener = new HashMap<Class<?>, Set<DataChangeAwareComponent>>();
    protected final static Map<Class<? extends GenericEvent>, Set<GenericEventAwareComponent<? extends GenericEvent>>> eventConsumers = new HashMap<Class<? extends GenericEvent>, Set<GenericEventAwareComponent<? extends GenericEvent>>>();


    private static final Logger logger = new Logger(BaracusApplicationContext.class);

    private static volatile BaracusApplicationContext __instance = null;

    private static ActivityLifecycleCallbacks callbacks;

    private static final BeanContainer beanContainer = new BeanContainer();

    static{
        registerBeanClass(ConfigurationDao.class);
    }

    private static String databasePath;

    public BaracusApplicationContext() {
        __instance = this;
        make();
    }

    /**
     * Context is not built up yet
     */
    public static class ContextNotYetCreatedException extends RuntimeException {
        ContextNotYetCreatedException (String reason) {
            super(reason);
        }
    }

    private static boolean init=false;
    public static synchronized void initApplicationContext() {
        if (!init) {
            beanContainer.createInstances();
//            beanContainer.holdBean(Context.class, __instance);   // Inject a context simply
            beanContainer.performInjections();
            beanContainer.performPostConstuct();
            beanContainer.treatKnownUiComponents();

            init = true;
        }
    }

    public static synchronized void make() {
        if (!semaphore) {
            semaphore = true;
            callbacks = new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    logger.debug("onActivityCreated called for $1",activity.getClass().getName());
                    BeanContainer.addExistingActivity(activity);
//                    beanContainer.holdBean(activity.getClass(), activity);
                    if (!init) {
                        logger.debug("build application context");
                        initApplicationContext();
                    }
                    beanContainer.performInjection(activity);

                }

                @Override
                public void onActivityStarted(Activity activity) {
                    logger.debug("onActivityStarted called for $1",activity.getClass().getName());
                    BeanContainer.addActiveActivity(activity);
                }

                @Override
                public void onActivityResumed(Activity activity) {
                    logger.debug("onActivityResumed called for $1",activity.getClass().getName());
                    BeanContainer.removePausedActivity(activity);
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    logger.debug("onActivityPaused called for $1",activity.getClass().getName());
                    BeanContainer.addPausedActivity(activity);
                }

                @Override
                public void onActivityStopped(Activity activity) {
                    logger.debug("onActivityStopped called for $1",activity.getClass().getName());
                    BeanContainer.removeActiveActivity(activity);
                    beanContainer.performOutjection(activity);
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                    logger.debug("onActivitySaveInstanceState called for $1",activity.getClass().getName());

//                    beanContainer.performOutjection(activity.getClass());
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    logger.debug("onActivityDestroyed called for $1",activity.getClass().getName());
                    BeanContainer.removeExistingActivity(activity);
                }
            };

            __instance.registerActivityLifecycleCallbacks(callbacks);



        }

        semaphore = false;

        refCount++;
    }

    /**
     * register a bean class managed by the container A managed bean
     * can implement the lifecycle interfaces Initializable (for post-
     * construction management) and Destroyable (for pre-destroy management).
     *
     * @param theClazz
     */
    public final static void registerBeanClass(Class<?> theClazz) {
        beanContainer.registerBeanClass(theClazz);
    }

    /**
     * resolve a string and replace parameters by passed strings
     * e.g. resolveString(R.string.foo,4711)
     * @param msgId - the android string resource id
     * @param vars - the variables replacing $1,$2...$n in the string
     * @return the substituted string
     */
    public static String resolveString(Integer msgId, String... vars) {
        String rawMsg = __instance.getApplicationContext().getString(msgId);

        if (vars != null && vars.length > 0) {

            int i = 1;
            for (String parm : vars) {
                rawMsg = rawMsg.replace("$" + i, parm);
                ++i;
            }
        }

        return rawMsg;
    }

    /**
     * resolve a string and replace parameters by passed strings
     * e.g. resolveString(R.string.foo,4711)
     * single parameter function to avoid array wrapping in case of single parameters
     *
     * @param msgId - the android string resource id
     * @param var - the variables replacing $1,$2...$n in the string
     * @return the substituted string
     */
    public static String resolveString(Integer msgId, String var) {
        String rawMsg = __instance.getApplicationContext().getString(msgId);
        rawMsg = rawMsg.replace("$1", var);
        return rawMsg;
    }

    /**
     * destroys the application context and shreds all beans. this function allows you
     * to shut down the entire bean context in your application without restarting it
     *
     * @param force - set to true, and all references are ignored
     */
    public static synchronized void destroy(boolean force) {
        refCount--;
        if (refCount == 0 || force) {

            beanContainer.shutdownContext();

            __instance.unregisterActivityLifecycleCallbacks(callbacks);

            connectDbHandle().close();
            deleteListeners.clear();
            changeListener.clear();
            db = null;
            semaphore = false;
            init = false;
            System.gc();
        }
    }


    /**
     * register a deletion listener implementing the DeleteAwareComponent interface. This listener
     * is called automatically on the deletion of the associated class. notice, if a delete listener
     * causes in exception in the callback processing, it will be automatically removed from the listener table
     * @param clazz - the class
     * @param dac - the delete listener
     */
    public static synchronized void registerDeleteListener(Class<? extends AbstractModelBase> clazz, DeleteAwareComponent dac) {
        logger.debug("Registered DeleteListener $1 for class $2", clazz.getSimpleName(), dac.getClass().getSimpleName());
        deleteListeners.put(clazz, dac);
    }

    /**
     * emit a delete event on the passed model class
     * @param clazz - the class to raise the event for
     */
    public static synchronized void emitDeleteEvent(Class<? extends AbstractModelBase> clazz) {
        if (deleteListeners.containsKey(clazz)) {
            DeleteAwareComponent<?> dac = deleteListeners.get(clazz);
            try {
                dac.onDelete();
            } catch (Exception e) {
                logger.error("Caught exception while emitting delete event", e);
                deleteListeners.remove(clazz);
            }
        }
    }

    /**
     * register a change listener on the entity. @see registerDeleteListener. same restrictions, same behaviour
     * but this time for change events
     * @param clazz
     * @param dac
     */
    public static synchronized void registerSetChangeListener(Class<? extends AbstractModelBase> clazz, DataSetChangeAwareComponent dac) {
        logger.debug("Registered SetChangeListener $1 for class $2", clazz.getSimpleName(), dac.getClass().getSimpleName());
        changeListener.put(clazz, dac);
    }

    /**
     * emits a change event
     * @param clazz
     */
    public static synchronized void emitSetChangeEvent(Class<? extends AbstractModelBase> clazz) {
        if (changeListener.containsKey(clazz)) {
            DataSetChangeAwareComponent<?> dac = changeListener.get(clazz);
            try {
                dac.onChange();
            } catch (Exception e) {
                logger.error("Caught exception while emitting change set event", e);
                changeListener.remove(clazz);
            }
        }
    }


    /**
     * register a change listener on the entity. @see registerDeleteListener. same restrictions, same behaviour
     * but this time for change events
     *
     * @param clazz
     * @param dac
     */
    public static synchronized void registerDataChangeListener(Class<? extends AbstractModelBase> clazz, DataChangeAwareComponent dac) {
        logger.debug("Registered SetChangeListener $1 for class $2", clazz.getSimpleName(), dac.getClass().getSimpleName());
        Set<DataChangeAwareComponent> set = dataListener.get(clazz);
        if (set == null) {
            set = new HashSet<DataChangeAwareComponent>();
            dataListener.put(clazz, set);
        }
        set.add(dac);
    }

    /**
     * emits a change event on a single data object to all registered event recipients
     *
     * @param changedItem - the changed item
     */
    public static synchronized void emitDataChangeEvent(AbstractModelBase changedItem) {

        if (changedItem != null) {
            if (dataListener.containsKey(changedItem.getClass())) {
                Set<DataChangeAwareComponent> dac = dataListener.get(changedItem.getClass());
                if (dac != null && dac.size() > 0) {
                    try {
                        for (DataChangeAwareComponent component : dac) {
                            component.onChange(changedItem);
                        }
                    } catch (Exception e) {
                        logger.error("Caught exception while emitting change set event", e);
                        dac.remove(changedItem.getClass());
                    }
                }
            }
        }
    }

    /**
     * register a generic listener for a generic event.
     *
     * @param eventClass - the event class
     * @param handler - the handler
     */
    public static synchronized void registerGenericListener(Class<? extends GenericEvent> eventClass, GenericEventAwareComponent<?> handler) {
        logger.debug("Registered Generic Listener $1 for class $2", eventClass.getSimpleName(), handler.getClass().getSimpleName());
        Set<GenericEventAwareComponent<? extends GenericEvent>> set = eventConsumers.get(eventClass);
        if (set == null) {
            set = new HashSet<GenericEventAwareComponent<? extends GenericEvent>>();
            eventConsumers.put(eventClass, set);
        }
        set.add(handler);
    }

    /**
     * Free all consumers of an generic event
     * @param eventClass
     */
    public static synchronized void freeGenericListeners(Class<? extends GenericEvent> eventClass){
        Set<GenericEventAwareComponent<? extends GenericEvent>> set = eventConsumers.get(eventClass);
        if (set != null) {
            set.clear();
        }
    }

    /**
     * Free all consumers of data change event
     * @param forClazz - the model class whose event listeners should be removed
     */
    public static synchronized void freeDataChangeListeners(Class<? extends AbstractModelBase> forClazz){
        Set<DataChangeAwareComponent> set = dataListener.get(forClazz);
        if (set != null) {
            set.clear();
        }
    }



    /**
     * emit a generic event to all registered listeners
     * @param event
     */
    public static synchronized void emitGenericEvent(GenericEvent event) {
        Set<GenericEventAwareComponent<?>> receivers = eventConsumers.get(event.getClass());
        if (receivers != null) {
            for (GenericEventAwareComponent receiver : receivers) {
                try {
                    receiver.handleEvent(event);
                } catch (Exception e) {
                    logger.error("Caught exception while emitting generic event", e);
                    receivers.remove(receiver);
                }
            }
        }
    }




    /**
     * @return the android context
     */
    public static synchronized Context getContext() {
        return __instance.getApplicationContext();
    }

    /**
     * @return the absolute path to the database
     */
    public static String getDatabasePath() {
        return databasePath;
    }

    /**
     * @return a db handle. notice, if you implement DAOs you do not need this functions, the db context is injected automatically!
     */
    public static synchronized SQLiteDatabase connectDbHandle() {
        if (db == null) {
            db = connectOpenHelper().getWritableDatabase();
            databasePath = db.getPath();
        }

        return db;
    }

    /**
     * @return the open helper. currently needed by the dao.
     */
    public static synchronized BaracusOpenHelper connectOpenHelper() {
        if (baracusOpenHelper == null) {
            for (Object o : beanContainer.beanMap.values()) {
                if (BaracusOpenHelper.class.isAssignableFrom(o.getClass())) {
                    baracusOpenHelper = (BaracusOpenHelper) o;
                }
            }
            if (baracusOpenHelper == null) {
                throw new ContextNotYetCreatedException("You must implement an OpenHelper deriving BaracusOpenHelper and register it to Your context!");
            }
        }
        return baracusOpenHelper;
    }

    /**
     * returns the baracus application context instance. notice,
     * normally you should not need this instance and fully rely
     * on the automated injection mechanisms
     *
     * @return the baracus application context instance
     */
    public static BaracusApplicationContext getInstance() {
        return __instance;
    }

    /**
     * @param clazz - the class of the bean to be returned
     * @param <T> - class parametrizer
     * @return the instance of the bean or null
     */
    public static <T> T  getBean(Class<T> clazz) {
        return (T) BeanContainer.clazzMap.get(clazz);
    }

    /**
     * run a type based dependency injection on the passed object
     * @param o - the object where injection shall be performed on
     */
    public synchronized  static void performInjectionsOn(Object o) {
        if (!semaphore) {
            beanContainer.performInjection(o);
        }
    }

    /**
     * creates a bean instance not cached by the container - no singleton! -
     * for your personal transient use. does not support custom constructors!
     * @param clazz - the class to be instantiaten
     * @param <T> the type
     * @return an instance of T with all refs to components injected
     */
    public static <T> T createPrototypeBean(Class<T> clazz) {
        try {
            T instance = beanContainer.instantiatePojo(clazz);
            performInjectionsOn(instance);
            return instance;
        } catch (Exception e) {
            throw new Exceptions.IntantiationException(e);
        }
    }
}
