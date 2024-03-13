package Primitives;

public class IRgetelt extends IRStatement {
    IRVariable variable;
    int methodID;
    IRVariable loadedField;

    public IRgetelt(IRVariable variable, IRVariable loadedField, int methodID) {
        this.variable = variable;
        this.loadedField = loadedField;
        this.methodID = methodID;
    }

    @Override
    public String toString() {
        return variable + " = " + String.format("getelt(%s, %d)", loadedField.toString(), methodID);
    }
}
