package Primitives;

public class IRAlloc extends IRStatement {
    IRVariable variable;
    int space;

    public IRAlloc(IRVariable variable, int space) {
        this.variable = variable;
        this.space = space;
    }

    @Override
    public String toString() {
        return variable + " = " + "alloc" + "(" + space + ")";
    }
}
