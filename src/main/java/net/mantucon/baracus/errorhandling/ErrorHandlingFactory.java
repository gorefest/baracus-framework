package net.mantucon.baracus.errorhandling;

import android.view.View;
import android.widget.TextView;
import net.mantucon.baracus.annotations.Bean;
import net.mantucon.baracus.lifecycle.Destroyable;
import net.mantucon.baracus.lifecycle.Initializeable;

import java.util.*;

import static net.mantucon.baracus.context.BaracusApplicationContext.resolveString;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 25.09.13
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
@Bean
public class ErrorHandlingFactory implements Initializeable, Destroyable{

    // Error handlers
    private Map<View, Map<Integer, Object[]>> errorMap = new HashMap<View, Map<Integer, Object[]>>();
    private final Map<Integer, CustomErrorHandler> registeredHandlers = new HashMap<Integer, CustomErrorHandler>();
    private final Set<StandardErrorHandler> standardHandlers = new HashSet<StandardErrorHandler>();



    /**
     * adds an error with a specific error level to the passed view.
     * If an implementation of CustomErrorHandler is registered for the affectedResource, the error
     * will be automatically routed to that field
     *
     * Notice : only to be used when you disregard automatic form validation
     *
     * @param container - the containing view of the resource
     * @param affectedResource - the resource
     * @param messageId - a message ID
     * @param severity - the severity. can be used by the CustomErrorHandler.
     * @param params - the parameters varags used to replace $1..$n tags in the message text
     */
    public void addErrorToView(View container, int affectedResource, int messageId, ErrorSeverity severity, String... params) {
        if (!errorMap.containsKey(container)) {
            errorMap.put(container, new HashMap<Integer, Object[]>());
        }

        Map<Integer, Object[]> assignment = errorMap.get(container);
        Object[] values = new Object[params != null ? params.length + 2 : 2];

        int id = -1;
        if (registeredHandlers.containsKey(affectedResource)) {
            id = registeredHandlers.get(affectedResource).getId();
        } else {
            id = affectedResource;
        }

        values[0] = Integer.valueOf(messageId);
        values[1] = severity;

        if (params != null && params.length > 0) {
            int i = 2;
            for (String param : params) {
                values[i] = param;
                i++;
            }
        }

        assignment.put(id, values);
    }

    /**
     * let all errors impact on the passed view. All bound fields and error handlers
     * associated with the passed will highlight any error mappeable to the view.
     *
     * @param container - the containing view
     */
    public void applyErrorsOnView(View container) {
        if (!errorMap.containsKey(container)) {
            return;
        }

        Map<Integer, Object[]> assignments = errorMap.get(container);

        for (Map.Entry<Integer, Object[]> set: assignments.entrySet()) {
            View v = container.findViewById(set.getKey());
            // First, handle specific custom handler for using own error handling
            // technique
            if (v != null && CustomErrorHandler.class.isAssignableFrom(v.getClass())) {
                ErrorHandler customErrorHandler= (ErrorHandler) v;
                if (customErrorHandler != null) {
                    Object[] params = set.getValue();
                    if (params.length > 2) {
                        String[] strings =new String[params.length - 2];
                        for (int i = 0; i < params.length-2; ++i){
                            strings[i] = (String) params[i];
                        }
                        Integer msgId = (Integer) params[0];
                        ErrorSeverity severity = (ErrorSeverity) params[1];
                        customErrorHandler.handleError(container, msgId, severity, strings);
                    } else {
                        Integer msgId = (Integer) params[0];
                        ErrorSeverity severity = (ErrorSeverity) params[1];
                        customErrorHandler.handleError(container, msgId, severity);
                    }
                }
            } else {
                // Now try all standard handlers
                for (StandardErrorHandler handler : standardHandlers) {
                    if (v != null && handler.canHandleView(v)) {
                        Object[] params = set.getValue();
                        TextView t = (TextView) v;
                        Integer msgId = (Integer) params[0];
                        ErrorSeverity severity = (ErrorSeverity) params[1];
                        if (params.length > 2) {
                            String[] strings =new String[params.length - 2];
                            for (int i = 0; i < params.length-2; ++i){
                                strings[i] = String.valueOf(params[i]);
                            }
                            handler.handleError(v,msgId, severity, strings);
                        } else {
                            handler.handleError(v, msgId, severity);
                        }
                    }
                }
            }
        }
    }

    /**
     * Determines, whether a view is sticked with errors
     * @param v - the view to check
     * @return true, if any bound errors to the view are found in the errorMap
     */
    public boolean viewHasErrors(View v){
        if (errorMap.containsKey(v)){
            return errorMap.get(v).size() > 0;
        }
        return false;
    }

    /**
     * remove all errors from the passed view
     * @param container - the view to clear
     */
    public void resetErrors(View container) {
        if (errorMap.containsKey(container)) {
            Map<Integer, Object[]> assignments = errorMap.get(container);

            for (Integer key: assignments.keySet()) {
                View v = container.findViewById(key);

                if (CustomErrorHandler.class.isAssignableFrom(v.getClass())) {
                    CustomErrorHandler customErrorHandler= (CustomErrorHandler) v;
                    // Object[] params =assignments.get(key);
                    customErrorHandler.reset(container);
                }

                if (TextView.class.isAssignableFrom(v.getClass())) {
                    TextView t = (TextView) v;
                    t.setError(null);
                }
            }

            errorMap.put(container, new HashMap<Integer, Object[]>());
        }
    }

    /**
     * register an error handler. an error handler normally is bound to another field in
     * the view. The error is raised by attaching an error to the field (view) component
     * bound to the CustomErrorHandler's idToDisplayFor-property
     * @param CustomErrorHandler
     */
    public void registerCustomErrorHandler(CustomErrorHandler CustomErrorHandler) {
        if (CustomErrorHandler.getIdToDisplayFor() != -1) {
            registeredHandlers.put(CustomErrorHandler.getIdToDisplayFor(), CustomErrorHandler);
        }
    }

    /**
     * unregister all error handlers for the passed field. If you use the
     * net.mantucon.baracus.context.ManagedFragment component and set the View
     *
     * @param container
     */
    public void unregisterCustomErrorHandlersForView(View container) {
        errorMap.remove(container);
    }

    /**
     * register an error handling for the use of the general android error
     * handling stuff
     *
     * @param handler - the handler
     * @see TextEditErrorHandler
     */
    public void registerStandardErrorHandler(StandardErrorHandler handler) {
        standardHandlers.add(handler);
    }


    @Override
    public void onDestroy() {
        registeredHandlers.clear();
        standardHandlers.clear();
        errorMap.clear();
    }

    @Override
    public void postConstruct() {
        registerStandardErrorHandler(new TextEditErrorHandler());
    }

    /**
     * @return the map with all custom error handlers
     */
    public Map<Integer, CustomErrorHandler> getRegisteredHandlers() {
        return registeredHandlers;
    }

    /**
     * @return all standard handlers
     */
    public Set<StandardErrorHandler> getStandardHandlers() {
        return standardHandlers;
    }
}
