package net.mantucon.baracus.validation;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Interface used by the validation factory to listen to
 */
public interface ConstrainedView<T> {
    T getCurrentValue();

    String getValidators();
}
