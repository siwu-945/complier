package Class;

public class Field {
    private String fieldName;

    public Field(String filedName) {
        this.fieldName = filedName;
    }

    @Override
    public String toString() {
        return fieldName;
    }

}
