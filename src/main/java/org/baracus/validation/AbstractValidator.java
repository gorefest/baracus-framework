package org.baracus.validation;


import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.13
 * Time: 10:21
 *
 * Default Implementation for validators in order to avoid implementing viewToMessageParam
 * again and again and again.
 *
 */
public abstract class AbstractValidator<T> implements Validator<T> {

    /**
     * Default implementation to avoid every Validator implementing this function.
     * In most cases - if you do not want the view content used in the message - implementing this
     * abstract class instead of a view will save you some time
     *
     * @param v - the view
     * @return null; -> default implementation
     */
    @Override
    public String[] viewToMessageParams(View v) {
        return null;
    }
}
