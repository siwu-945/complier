package Primitives;

public class IRSet extends IRStatement {
    IRVariable array_variable;
    IRVariable index;
    IRVariable variable;

    public IRSet(IRVariable array_variable, IRVariable index, IRVariable variable) {
        this.array_variable = array_variable;
        this.index = index;
        this.variable = variable;
    }

    @Override
    public String toString() {
        return String.format("setelt(%s, %s, %s)", array_variable.toString(), index.toString(), variable.toString());
    }
}
