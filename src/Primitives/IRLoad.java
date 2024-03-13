package Primitives;

public class IRLoad extends IRStatement {
    IRVariable variable;
    IRVariable right;

    public IRLoad(IRVariable variable, IRVariable right) {
        this.variable = variable;
        this.right = right;
    }

    @Override
    public String toString() {
        return variable + " = " + String.format("load(%s)", right.toString());
    }
}
