package net.mantucon.baracus.orm;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.12
 * Time: 11:18
 *
 * indicate a field. notice, no type information is needed here, because of the
 * use of rowmappers in the dao layer.
 *
 */
public class Field implements Comparable<Field> {
    /**
     * field index, increment by one per field, set default to AbstractModelBase.fieldList.size() , see AbstractModelBase example for details
     */
    public int fieldIndex;
    /**
     * the name of the field in the db
     */
    public final String fieldName;

    /**
     * indicates a key attribute. normally - when deriving AbstractModelBase - you can use the constructor
     * using fieldName and fieldIndex or just set it to false
     */
    public final boolean isKeyAttribute;

    /**
     * Constructor for regular fields. Assumes, that your field is not part of the key.
     *
     * @param fieldName - db name of the field
     * @param fieldIndex - the index of the field in the list
     */
    public Field(String fieldName, int fieldIndex) {
        this.fieldName = fieldName;
        this.fieldIndex = fieldIndex;
        isKeyAttribute = false;
    }

    /**
     * Constructor for regular fields. Assumes, that your field is not part of the key.
     *
     * @param fieldName - db name of the field
     */
    public Field(String fieldName) {
        this.fieldName = fieldName;
        this.fieldIndex = -1; // rely on validation to set the parameter index correctly
        isKeyAttribute = false;
    }

    /**
     * Constructor carrying a key information. normally the other constructor should fit your purpose.
     *
     * @param fieldName - db name of the field
     * @param fieldIndex - the index of the field in the list
     * @param isKeyAttribute - key attribute indicator
     */
    public Field(String fieldName, int fieldIndex, boolean isKeyAttribute) {
        this.fieldName = fieldName;
        this.fieldIndex = fieldIndex;
        this.isKeyAttribute = isKeyAttribute;
    }

    @Override
    public final String toString() {
        return fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)  { return true; }
        if (!(o instanceof Field)) { return false; }

        Field field = (Field) o;

        if (fieldIndex != field.fieldIndex) { return false; }
        if (fieldName != null ? !fieldName.equals(field.fieldName) : field.fieldName != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = fieldIndex;
        result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Field field) {
        return (Integer.valueOf(this.fieldIndex)).compareTo(Integer.valueOf(field.fieldIndex));
    }
}
