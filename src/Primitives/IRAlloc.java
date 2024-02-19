package Primitives;

public class IRAlloc extends IRStatement {
    IRVariable variable;
    IRVariable fieldAddress;
    int space;

    public IRAlloc(IRVariable variable, int space, IRVariable fieldAddress) {
        this.variable = variable;
        this.space = space;
        this.fieldAddress = fieldAddress;
    }

    @Override
    public String toString() {
        return variable + " = " + "alloc" + "(" + space + ")";
    }
}
