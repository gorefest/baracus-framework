package net.mantucon.baracus.validation;

import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.13
 * Time: 06:46
 * To change this template use File | Settings | File Templates.
 */
public interface ConstrainedView<T> {
    T getCurrentValue();
    String getValidators();
}
