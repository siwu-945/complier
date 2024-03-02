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

//    public String argumentString(){
//        StringBuilder argumentsString = new StringBuilder();
//        int count = arguments.size();
//        for (IRVariable expression : arguments) {
//            argumentsString.append(expression.toString());
//            if(count < arguments.size()){
//                argumentsString.append(", ");
//            }
//            count++;
//        }
//        return argumentsString.toString();
//    }

    @Override
    public String toString() {
        if (arguments.equals("")) {
            return variable.toString() + " = call(" + codeAddress.toString() + ", " + classObject.toString() + ")";
        }
        return variable.toString() + " = call(" + codeAddress.toString() + ", " + classObject.toString() + ", " + arguments + ")";
    }


}
