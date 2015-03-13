package org.baracus.context;

import android.R;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import org.baracus.dao.BaracusOpenHelper;
import org.baracus.dao.ConfigurationDao;
import org.baracus.errorhandling.CustomErrorHandler;
import org.baracus.errorhandling.ErrorHandlingFactory;
import org.baracus.errorhandling.ErrorSeverity;
import org.baracus.errorhandling.StandardErrorHandler;
import org.baracus.lifecycle.ApplicationContextInitializer;
import org.baracus.orm.AbstractModelBase;
import org.baracus.signalling.*;
import org.baracus.util.Logger;
import org.baracus.validation.ValidationFactory;
import org.baracus.validation.Validator;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created with IntelliJ IDEA.  <br>
 * User: marcus                 <br>
 * Date: 10.07.12               <br>
 * Time: 06:05                  <br>
 * <hr>
 * <p/>
 * Base Application Context class. In order to use BARACUS you must inherit this class.
 * use the registerBeanClass() function to add all Your bean classes. Implement Initializable and
 * Destroyable interface in order to have creation / destruction lifecycle management support.
 * <p/>
 * <hr>
 * Example Context Implementation :
 * <pre>
 * {@code
 *
 * public class ApplicationContext extends BaracusApplicationContext{
 *
 * static {
 * registerBeanClass(BankDao.class);
 * ...
 * }
 *
 *
 * private static final Logger logger = new Logger(ApplicationContext.class);
 *
 * private ApplicationContext() {
 * // protection constructor
 * }
 *
 *
 * }
 *
 * To make use of Your class as an app container, You must register it in the
 * AndroidManifest.xml's application tag :
 *
 * {@code
 *
 * <application android:icon="@drawable/icon"
 * android:label="@string/app_name"
 * android:debuggable="true"
 * android:theme="@android:style/Theme.DeviceDefault"
 * android:name=".wonderapp.application.ApplicationContext">
 *
 *
 * }
 *
 * </pre>
 *
 */
public abstract class BaracusApplicationContext extends Application {

    // infrastructure beans

    // DB Access
    private static SQLiteDatabase db;
    private static BaracusOpenHelper baracusOpenHelper;


    private static boolean semaphore = false;
    private static int refCount = 0;

    // Awareness Refs, event handlers
    protected final static Map<Class<?>, DeleteAwareComponent> deleteListeners = new HashMap<Class<?>, DeleteAwareComponent>();
    protected final static Map<Class<?>, DataSetChangeAwareComponent> changeListener = new HashMap<Class<?>, DataSetChangeAwareComponent>();
    protected final static Map<Class<?>, Set<DataChangeAwareComponent>> dataListener = new HashMap<Class<?>, Set<DataChangeAwareComponent>>();
    protected final static Map<Class<? extends GenericEvent>, Set<GenericEventAwareComponent<? extends GenericEvent>>> eventConsumers = new HashMap<Class<? extends GenericEvent>, Set<GenericEventAwareComponent<? extends GenericEvent>>>();

    private static final Logger logger = new Logger(BaracusApplicationContext.class);

    private static volatile BaracusApplicationContext __instance = null;

    private static ActivityLifecycleCallbacks callbacks;

    private static final BeanContainer beanContainer = new BeanContainer();

    private static ValidationFactory validationFactory;
    private static ErrorHandlingFactory errorHandlingFactory;

    private static ApplicationContextInitializer applicationContextInitializer = null;

    static {
        registerBeanClass(ConfigurationDao.class);
        registerBeanClass(ValidationFactory.class);
        registerBeanClass(ErrorHandlingFactory.class);
    }

    private static String databasePath;

    public BaracusApplicationContext() {
        if (__instance != null) {
            throw new ContainerAlreadyStartedException();
        }
        __instance = this;
        make();
    }

    /**
     * Context is not built up yet
     */
    public static class ContextNotYetCreatedException extends RuntimeException {
        ContextNotYetCreatedException(String reason) {
            super(reason);
        }
    }

    public static class ContainerAlreadyStartedException extends RuntimeException {
    }

    /**
     * thrown, if a unlocateable resource is requested
     */
    private static final class BadResourceAccessException extends RuntimeException {
        public BadResourceAccessException(Throwable throwable) {
            super(throwable);
        }
    }

    private static boolean init = false;

    public static synchronized void initApplicationContext() {
        if (!init) {
            beanContainer.createInstances();
//            beanContainer.holdBean(Context.class, __instance);   // Inject a context simply
            beanContainer.performInjections();
            beanContainer.performPostConstuct();
            beanContainer.treatKnownUiComponents();

            validationFactory = getBean(ValidationFactory.class);
            errorHandlingFactory = getBean(ErrorHandlingFactory.class);

            if (applicationContextInitializer != null) {
                beanContainer.performInjection(applicationContextInitializer);
                applicationContextInitializer.afterContextIsBuilt();
            }

            init = true;
        }
    }

    /**
     * performs a reinitialization of the context. This function has to be called
     * if bean implementations have been substituted by other candidates
     */
    public static void reinitializeContext() {
        init = false;
        initApplicationContext();
    }

    public static synchronized void make() {
        if (!semaphore) {
            semaphore = true;
            callbacks = new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    logger.debug("onActivityCreated called for $1", activity.getClass().getName());
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
                    logger.debug("onActivityStarted called for $1", activity.getClass().getName());
                    BeanContainer.addActiveActivity(activity);
                }

                @Override
                public void onActivityResumed(Activity activity) {
                    logger.debug("onActivityResumed called for $1", activity.getClass().getName());
                    BeanContainer.removePausedActivity(activity);
                    beanContainer.performInjection(activity);
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    logger.debug("onActivityPaused called for $1", activity.getClass().getName());
                    BeanContainer.addPausedActivity(activity);
                }

                @Override
                public void onActivityStopped(Activity activity) {
                    logger.debug("onActivityStopped called for $1", activity.getClass().getName());
                    BeanContainer.removeActiveActivity(activity);
                    beanContainer.performOutjection(activity);
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                    logger.debug("onActivitySaveInstanceState called for $1", activity.getClass().getName());

//                    beanContainer.performOutjection(activity.getClass());
                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    logger.debug("onActivityDestroyed called for $1", activity.getClass().getName());
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
     * Register a bean class to be managed by the container, which is designated
     * to be injected into an interface or a superclass. This methods throws an exception if
     * theImplementation is not of type theInterface
     *
     * @param theSupertype      - the supertype to be used for injection
     * @param theImplementation - the instance type to be used to be injected
     */
    public final static void registerBeanClass(Class<?> theSupertype, Class<?> theImplementation) {
        beanContainer.registerBeanClass(theSupertype, theImplementation);
    }

    /**
     * replaces the implementation of the supertype by the passed implementation.
     * when you make use of this feature, don't forget to call the reinitializeContext() function
     * in order to get all instances created and DI performed with the new types after
     * performing all replacements. This feature is especially useful when You make use
     * of different implementations of an interface which shall be able to be hot-replaced
     * in the application. However, the passed implementation MUST be assignable to theSupertype
     * and it is essential to reinit the context after all replacements!
     *
     * @param theSupertype - the supertype to be used for injection
     * @param theImplementation - - the instance type to be used to replace the existing instance
     */
    public final static void reRegisterBeanClass(Class<?> theSupertype, Class<?> theImplementation) {
        beanContainer.replaceBeanClass(theSupertype, theImplementation);
    }

    /**
     * resolve a string and replace parameters by passed strings
     * e.g. resolveString(R.string.foo,4711)
     *
     * @param msgId - the android string resource id
     * @param vars  - the variables replacing $1,$2...$n in the string
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
     * @param var   - the variables replacing $1,$2...$n in the string
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
            validationFactory = null;
            errorHandlingFactory = null;

            semaphore = false;
            init = false;
            System.gc();
        }
    }


    /**
     * register a deletion listener implementing the DeleteAwareComponent interface. This listener
     * is called automatically on the deletion of the associated class. notice, if a delete listener
     * causes in exception in the callback processing, it will be automatically removed from the listener table
     *
     * @param clazz - the class
     * @param dac   - the delete listener
     */
    public static synchronized void registerDeleteListener(Class<? extends AbstractModelBase> clazz, DeleteAwareComponent dac) {
        logger.debug("Registered DeleteListener $1 for class $2", clazz.getSimpleName(), dac.getClass().getSimpleName());
        deleteListeners.put(clazz, dac);
    }

    /**
     * emit a delete event on the passed model class
     *
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
     *
     * @param clazz
     * @param dac
     */
    public static synchronized void registerSetChangeListener(Class<? extends AbstractModelBase> clazz, DataSetChangeAwareComponent dac) {
        logger.debug("Registered SetChangeListener $1 for class $2", clazz.getSimpleName(), dac.getClass().getSimpleName());
        changeListener.put(clazz, dac);
    }

    /**
     * emits a change event
     *
     * @param clazz
     */
    public static synchronized void emitSetChangeEvent(Class<? extends AbstractModelBase> clazz) {
        if (changeListener.containsKey(clazz)) {
            DataSetChangeAwareComponent<?> dac = changeListener.get(clazz);
            try {
                dac.onChange(clazz);
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
     * @param clazz - the model class, for which we want to listen for changes
     * @param dac   - the change listener instance
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
     * removes a data change listener instance explicitly from the map
     *
     * @param dac - the change listener instance
     */
    public static synchronized void unregisterDataChangeListener(DataChangeAwareComponent<?> dac) {
        for (Set<DataChangeAwareComponent> sets : dataListener.values()) {
            if (sets.remove(dac)) {
                logger.debug("DAC was successfully removed $1", dac);
            }
        }
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
                    for (DataChangeAwareComponent component : dac) {
                        try {
                            component.onChange(changedItem);
                        } catch (Exception e) {
                            logger.error("Caught exception while emitting change set event", e);
                            dac.remove(component);
                        }
                    }
                }
            }
        }
    }

    /**
     * register a generic listener for a generic event.
     *
     * @param eventClass - the event class
     * @param handler    - the handler
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
     *
     * @param eventClass
     */
    public static synchronized void freeGenericListeners(Class<? extends GenericEvent> eventClass) {
        Set<GenericEventAwareComponent<? extends GenericEvent>> set = eventConsumers.get(eventClass);
        if (set != null) {
            set.clear();
        }
    }

    /**
     * Free all consumers of data change event
     *
     * @param forClazz - the model class whose event listeners should be removed
     */
    public static synchronized void freeDataChangeListeners(Class<? extends AbstractModelBase> forClazz) {
        Set<DataChangeAwareComponent> set = dataListener.get(forClazz);
        if (set != null) {
            set.clear();
        }
    }


    /**
     * emit a generic event to all registered listeners
     *
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
     * register a named validator for performing field validation in forms.
     *
     * @param name      - the name of the validator
     * @param validator - the validator instance
     */
    public static synchronized void registerValidator(String name, Validator<?> validator) {
        validationFactory.registerValidator(name, validator);
    }

    /**
     * register a named validator using the simple class name (FooBarValidator -> fooBarValidator)
     *
     * @param validator - the validator instance
     */
    public static void registerValidator(Validator<?> validator) {
        validationFactory.registerValidator(validator);
    }

    /**
     * make sure, that all named validators put into the comma seperated list are available
     * inside the context
     *
     * @param validatorList
     */
    public static synchronized void verifyValidators(String validatorList) {
        if (validationFactory != null) { // needed, otherwise preview in idea will throw NPE
            validationFactory.verifyValidators(validatorList);
        }
    }

    /**
     * removes all errors from the view set before and then performs
     * validations on the passed view and applies all errors to the
     * view. use viewHasErrors() to ask, if there are any issues bound to the
     * view
     *
     * @param view - the view to process
     */
    public static synchronized void validateView(View view) {
        resetErrors(view);
        validationFactory.validateView(view);
        errorHandlingFactory.applyErrorsOnView(view);
    }

    /**
     * performs a validation on the passed activity by trying to obtain
     * the underlying view and validating it
     *
     * @param activity
     * @return true, if the validation succeeded
     */
    public static synchronized boolean validateActivity(Activity activity) {
        View underlyingView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        resetErrors(underlyingView);
        validationFactory.validateView(underlyingView);
        applyErrorsOnView(underlyingView);
        return !viewHasErrors(underlyingView);
    }


    /**
     * registers an onFocusChangeListener to all view elements implementing
     * the @see ConstrainedView interface to perform on-the-fly-validation.
     * If you want Your View to be able to receive a validation callback
     * - e.g. in order to manage the visibility of an OK-Button or sth. -
     * Your View must implement the @see ValidatableView interface in
     * order to receive a validation notification callbacks.
     * <p/>
     * If you implement a @see ManagedFragment, simply call
     * the enableFocusChangeBasedValidation() function in the onCreate-method
     *
     * @param view - the view to register
     */
    public static void registerValidationListener(View view) {
        validationFactory.registerValidationListener(view);
    }

    /**
     * register a custom error handler. Use this stuff only, if You want to use specific view components
     * to handle Your errors, if you want to use standard android handling for any component,
     * use the registerStandardErrorHandler() function instead!
     *
     * @param handler - the handler instance
     */
    public static synchronized void registerCustomErrorHandler(CustomErrorHandler handler) {
        errorHandlingFactory.registerCustomErrorHandler(handler);
    }

    /**
     * register a standard error handler. Using a standard error handler will make use
     * of the android standard error handling.
     *
     * @param handler - the handler instance
     */
    public static synchronized void registerStandardErrorHandler(StandardErrorHandler handler) {
        errorHandlingFactory.registerStandardErrorHandler(handler);
    }


    /**
     * return true, if the passed view instance contains errors. Error handling should be always
     * done on the root view of a form!
     *
     * @param container - the form view containing malicious components
     * @return true, if any error is bound to the form instance
     */
    public static boolean viewHasErrors(View container) {
        return errorHandlingFactory.viewHasErrors(container);
    }

    /**
     * map all bound errors to all findeable receivers on the container.
     *
     * @param container - the container to map errors for
     */
    public static void applyErrorsOnView(View container) {
        errorHandlingFactory.applyErrorsOnView(container);
    }

    /**
     * adds an error to the passed view. use this function only, if want to perform manual form
     * validation! if you rely on automatic validation and error routing, simply call validateView
     *
     * @param container        - the container view
     * @param affectedResource - the resource id of the component, where the error occured
     * @param messageId        - the message id to display
     * @param severity         - the severity of the problem (currently unused)
     * @param params           - the parameters to be mapped to the resource resolution
     */
    public static void addErrorToView(View container, int affectedResource, int messageId, ErrorSeverity severity, String... params) {
        errorHandlingFactory.addErrorToView(container, affectedResource, messageId, severity, params);
    }

    /**
     * clear all errors of the view container
     *
     * @param container - the container
     */
    public static void resetErrors(View container) {
        errorHandlingFactory.resetErrors(container);
    }

    /**
     * unregister all error handlers for a view. this should be called implicitly by the
     *
     * @param v
     * @see ManagedFragment and the
     * @see ManagedActivity class
     * <p/>
     * If you are using own extension, make sure that you call this function before destroying
     * the view in order to avoid memory leaks
     */
    public static void unregisterErrorhandlersForView(View v) {
        errorHandlingFactory.unregisterCustomErrorHandlersForView(v);
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
     * @param <T>   - class parametrizer
     * @return the instance of the bean or null
     */
    public static <T> T getBean(Class<T> clazz) {
        return (T) BeanContainer.getBean(clazz);
    }

    /**
     * run a type based dependency injection on the passed object
     *
     * @param o - the object where injection shall be performed on
     */
    public synchronized static void performInjectionsOn(Object o) {
        if (!semaphore) {
            beanContainer.performInjection(o);
        }
    }

    /**
     * creates a bean instance not cached by the container - no singleton! -
     * for your personal transient use. does not support custom constructors!
     *
     * @param clazz - the class to be instantiaten
     * @param <T>   the type
     * @return an instance of T with all refs to components injected
     */
    public static <T> T createPrototypeBean(Class<T> clazz) {
        try {
            T instance = beanContainer.instantiatePojo(clazz);
            performInjectionsOn(instance);
            beanContainer.performPostConstructOn(instance);
            return instance;
        } catch (Exception e) {
            throw new Exceptions.IntantiationException(e);
        }
    }

    /**
     * resolve the passed name into a android resource ID, this means
     * the number representation in R.id.
     *
     * @param name - the name of a view component (e.g. btnOk)
     * @return the ID (eg -47236333)
     */
    public static final int getResource(String name) {
        try {
            Field f = R.layout.class.getField(name);
            return f.getInt(null);
        } catch (Exception e) {
            logger.error("ERROR!", e);
            throw new BadResourceAccessException(e);
        }
    }

    /**
     * set the after-init application initializer. allows you to add a hook to the
     * container lifecycle to be able to do something after the context is up
     * (comparable to JEE Startup-Singleton)
     *
     * @param applicationContextInitializer
     */
    public static void setApplicationContextInitializer(ApplicationContextInitializer applicationContextInitializer) {
        BaracusApplicationContext.applicationContextInitializer = applicationContextInitializer;
    }

    /**
     * finds all impementing singleton beans of the passed superclass and returns them
     *
     * @param superclass - the superclass to look for
     * @param <T>        - the type parameter of the superclass
     * @return a List<T> containing all found instances
     */
    public static <T> List<T> getBeansOfType(Class<T> superclass) {
        List<T> result = new ArrayList<T>();
        for (Object o : beanContainer.beanMap.values()) {
            if (superclass.isAssignableFrom(o.getClass())) {
                result.add((T) o);
            }
        }
        return result;
    }

}
