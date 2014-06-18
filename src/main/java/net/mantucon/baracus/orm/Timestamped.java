package net.mantucon.baracus.orm;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.12
 * Time: 07:32
 * <p/>
 * Indicates an entity bean carrying a modification timestamp
 */
public interface Timestamped {

    public Date getCreationDate();

    public Date getLastModificationDate();

    public void setCreationDate(Date date);

    public void setLastModificationDate(Date date);
}
