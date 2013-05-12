package net.mantucon.baracus.orm;

import net.mantucon.baracus.dao.BaseDao;

/**
 * Reference loader. Lazy Loading for Objects. Use this in rowMapper functions. This item
 * needs to have the DAO passed using the getById() function of the DAO in order to load
 * the item via it's id
 * <p/>
 * example :
 * <p/>
 * public CalculationSpreadSheet from(Cursor c) {
 * CalculationSpreadSheet result = new CalculationSpreadSheet();
 * ...
 * final Long bankAccountId = c.getLong(bankAccountCol.fieldIndex);
 * result.setBankAccountReference(new LazyReference<BankAccount>(new ReferenceLoader(bankAccountDao, bankAccountId)));
 * ...
 *
 * @param <T>
 */
public class ReferenceLoader<T extends AbstractModelBase> {
    private final BaseDao<T> dao;
    private final Long id;

    public ReferenceLoader(BaseDao<T> dao, Long id) {
        this.dao = dao;
        this.id = id;
    }

    /**
     * load the referenced entity NOW and return it. This function will be triggered by
     * the lazy reference.
     * @return
     */
    public T loadObject() {
        return dao.getById(id);
    }

    public Long getId() {
        return id;
    }
}
