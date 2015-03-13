package org.baracus.validation;

import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.13
 * Time: 06:45
 * <p/>
 * Validator interface. Validators are held seperately from the beans. If You want to
 * use a bean as a validator instead, make sure, you use the createPrototypeBean from
 * the ApplicationContext to instantiate it and get injections done. It is disregarded
 * to use Singleton Beans as Validators!
 * <p/>
 * For more details on validation, check the Baracus blog's form validation tutorials.
 */
public interface Validator<T> {

    public boolean validate(ConstrainedView<T> view);

    public int getMessageId();

    public String[] viewToMessageParams(View v);

}
