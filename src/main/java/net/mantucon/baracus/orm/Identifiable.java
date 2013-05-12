package net.mantucon.baracus.orm;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.12
 * Time: 07:31
 *
 * Interface indicating that your entity bean is identifiable by a standard long id.
 * All deriving classes of AbstractModelBase will be identifiable, having a surrogate long id.
 *
 *
 */
public interface Identifiable {

    public Long getId();
    public void setId(Long id);

}
