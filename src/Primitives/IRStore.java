package Primitives;

public class IRStore extends IRStatement {
    IRVariable variable;
    String right;

    public IRStore(IRVariable variable, String right) {
        this.variable = variable;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("store(%s, %s)", variable.toString(), right);
    }
}
