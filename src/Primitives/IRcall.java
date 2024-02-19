package Primitives;

public class IRcall extends IRStatement {
    IRVariable codeAddress;
    IRVariable classObject;
    IRVariable variable;

    public IRcall(IRVariable codeAddress, IRVariable classObject, IRVariable variable) {
        this.variable = variable;
        this.codeAddress = codeAddress;
        this.classObject = classObject;
    }

    @Override
    public String toString() {
        return variable.toString() + " = call(" + codeAddress.toString() + ", " + classObject.toString() + ")";
    }


}
