package Primitives;

public class IRcall extends IRStatement {
    IRVariable codeAddress;
    IRVariable classObject;
    IRVariable variable;
    String arguments;

    public IRcall(IRVariable codeAddress, IRVariable classObject, IRVariable variable, String arguments) {
        this.variable = variable;
        this.codeAddress = codeAddress;
        this.classObject = classObject;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return variable.toString() + " = call(" + codeAddress.toString() + ", " + classObject.toString() + ")";
    }


}
