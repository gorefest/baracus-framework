package org.baracus.signalling;

import org.baracus.orm.AbstractModelBase;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 27.01.13
 * Time: 12:18
 * be aware of deletions on a certain entity type's recordset. @see DataSetChangeAwareComponent
 */
public interface DeleteAwareComponent<T extends AbstractModelBase> {
    public void onDelete();
}
