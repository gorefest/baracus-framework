package org.baracus.orm;

import org.baracus.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.12
 * Time: 11:20
 * <p/>
 * The field list of an entity.
 */
public class FieldList {

    public static class FieldListLockedException extends RuntimeException {

    }

    private final List<Field> fields = new ArrayList<Field>();

    private String binder;

    boolean locked = false;

    public FieldList(String binder) {
        this.binder = binder;
    }

    /**
     * Locing constructor. Add all fields here. After that, this instance is locked
     * for modifications.
     *
     * @param binder    - the binding entity
     * @param fieldList - the fields to add
     */
    public FieldList(String binder, Field... fieldList) {

        if (locked) {
            throw new FieldListLockedException();
        }

        this.binder = binder;
        for (Field field : fieldList) {
            add(field);
        }
        locked = true;
    }

    private static final Logger logger = new Logger(FieldList.class);

    private boolean dirty = true;

    public void add(Field field) {
        checkField(field);
        fields.add(field);
        dirty = true;
    }

    private void checkField(Field field) {
        if (fields.contains(field)) {
            logger.fatal("Field $1 already present in list", field);
        }
    }

    public void add(List<Field> fields) {
        for (Field f : fields) {
            checkField(f);
        }

        this.fields.addAll(fields);
        dirty = true;
    }

    public void add(FieldList fieldList) {
        add(fieldList.fields);
        dirty = true;
    }

    public String[] getFieldNames() {
        String[] result = new String[getFields().size()];
        int i = 0;
        for (Field f : getFields()) {
            result[i++] = f.fieldName;
        }
        return result;
    }

    public String getFieldNamesAsString() {
        StringBuilder sb = new StringBuilder();
        for (Field f : getFields()) {
            sb.append(f.fieldName).append(",");
        }
        String result = sb.toString();
        return result.substring(0, result.length() - 1);
    }

    public String getFieldNamesAsStringWithoutKeyColumn() {
        StringBuilder sb = new StringBuilder();
        for (Field f : getFields()) {
            if (!f.isKeyAttribute) {
                sb.append(f.fieldName).append(",");
            }
        }
        String result = sb.toString();
        return result.substring(0, result.length() - 1);
    }

    public int size() {
        return fields.size();
    }

    /**
     * @return a string containing all field parameters as JDBC variables
     */
    public String getParamsAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            sb.append("?,");
        }

        String result = sb.toString();
        return result.substring(0, result.length() - 1);
    }

    /**
     * @return a string containing all field parameters as JDBC variables except the key column
     */
    public String getParamsAsStringWithoutKeyColumn() {
        StringBuilder sb = new StringBuilder();
        for (Field f : fields) {
            if (!f.isKeyAttribute) {
                sb.append("?,");
            }
        }
        String result = sb.toString();
        return result.substring(0, result.length() - 1);
    }

    public List<Field> getFields() {
        if (dirty) {
            Collections.sort(fields);
//            validate();
            dirty = false;
        }
        return fields;
    }

    public void validate() {
        int i = 0;
        for (Field f : fields) {
            if (i != f.fieldIndex) {
                logger.warn("WARNING! $1's field $2 is placed at $3, but should be on $4", binder, f.fieldName, f.fieldIndex, i);
//                f.fieldIndex = i;
            }
            ++i;
        }
    }
}
