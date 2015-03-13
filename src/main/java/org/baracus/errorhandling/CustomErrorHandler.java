package org.baracus.errorhandling;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 25.09.13
 * Time: 17:38
 * <p/>
 * Component interface for making a custom error handling.
 * <p/>
 * There is a vast difference to the StandardErrorHandlers : A custom error handler
 * always is bound to a view component instance and therefore it needs to be hashed
 * by its component id, whilst the StandardErrorHandlers simply rely on the type of
 * the passed component (So you only will find a set containing all StandardHandlers)
 */
public interface CustomErrorHandler extends ErrorHandler {
    /**
     * @return the component Id of the error handler widget
     */
    public int getId();

    /**
     * @return true if the component id, of the referenced Id;
     */
    public int getIdToDisplayFor();

}
