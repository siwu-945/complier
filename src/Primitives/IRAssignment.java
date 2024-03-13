package Primitives;

public class IRAssignment extends IRStatement {

    IRVariable variable;
    String right;

    public IRAssignment(IRVariable variable, String right) {
        this.variable = variable;
        this.right = right;
    }

    @Override
    public String toString() {
        return variable + " = " + right.toString();
    }

}
