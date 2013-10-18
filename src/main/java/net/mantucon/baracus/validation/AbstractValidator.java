package net.mantucon.baracus.validation;


import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.13
 * Time: 10:21
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractValidator<T> implements Validator<T> {

    /**
     * Default Implementation to avoid every Validator implementing this function.
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
